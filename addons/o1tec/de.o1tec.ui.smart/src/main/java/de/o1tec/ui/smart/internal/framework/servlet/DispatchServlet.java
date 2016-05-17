/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.ui.smart.internal.framework.servlet;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smarthome.model.sitemap.SitemapProvider;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import de.o1tec.ui.smart.internal.framework.config.WebAppConfig;
import de.o1tec.ui.smart.internal.framework.controller.IController;
import de.o1tec.ui.smart.internal.framework.controller.ModelAndView;
import de.o1tec.ui.smart.internal.framework.util.UriTemplate;

/**
 * This is the main servlet for the Smart UI.
 * It serves the Html code based on the sitemap model.
 *
 * @author Andreas Degenhart
 *
 */
public abstract class DispatchServlet extends BaseServlet {

    private final Logger logger = LoggerFactory.getLogger(DispatchServlet.class);

    private static final Pattern ptStatic = Pattern.compile(".*?\\.(?:jpg|gif|png|svg|css|js)$");
    private static final String URL_PARAMS = "_o1tec_dispatchServlet__RequestParams_";

    /** the name of the servlet to be used in the URL */
    public static final String SERVLET_NAME = "app";
    public static final String STATIC_RESOURCES_NAME = "static";

    private TemplateEngine templateEngine;

    protected WebAppConfig config = new WebAppConfig();

    protected Set<SitemapProvider> sitemapProviders = new CopyOnWriteArraySet<>();

    public void addSitemapProvider(SitemapProvider sitemapProvider) {
        this.sitemapProviders.add(sitemapProvider);
    }

    public void removeSitemapProvider(SitemapProvider sitemapProvider) {
        this.sitemapProviders.remove(sitemapProvider);
    }

    protected abstract Map<String, IController> getControllerMap();

    protected void activate(Map<String, Object> configProps) {
        config.applyConfig(configProps);
        try {
            Hashtable<String, String> props = new Hashtable<String, String>();

            httpService.registerServlet(getServletAlias(), this, props, createHttpContext());

            httpService.registerResources(getStaticResourcesAlias(), "web", null);

            logger.info("Started Smart UI at " + getServletAlias());
        } catch (NamespaceException e) {
            logger.error("Error during servlet startup", e);
        } catch (ServletException e) {
            logger.error("Error during servlet startup", e);
        }
    }

    protected void modified(Map<String, Object> configProps) {
        config.applyConfig(configProps);
    }

    protected void deactivate() {
        httpService.unregister(getServletAlias());
        httpService.unregister(getStaticResourcesAlias());
        logger.info("Stopped Smart UI");
    }

    @Override
    protected String getServletAlias() {
        return config.getWebAppContextName() + "/" + SERVLET_NAME;
        // return config.getWebAppContextName();
    }

    protected String getStaticResourcesAlias() {
        // return config.getWebAppContextName() + "/" + STATIC_RESOURCES_NAME;
        return config.getWebAppContextName();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(
                config.getServletContext());

        // XHTML is the default mode, but we will set it anyway for better understanding of code
        templateResolver.setTemplateMode("HTML");

        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix("/web/templates/");
        templateResolver.setSuffix(".html");

        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.setCacheable(false);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            logger.debug("doGet: URI={} Path={}", req.getRequestURI(), getRequestPath(req));

            IController controller = resolveControllerForRequest(req);
            if (controller != null) {
                ModelAndView mv = controller.process(req, resp);

                resp.setContentType("text/html;charset=UTF-8");
                resp.setHeader("Pragma", "no-cache");
                resp.setHeader("Cache-Control", "no-cache");
                resp.setDateHeader("Expires", 0);

                WebContext ctx = new WebContext(req, resp, req.getServletContext(), req.getLocale());

                // set model variables
                if (mv.getModel() != null) {
                    ctx.setVariables(mv.getModel());
                }

                // set url params
                if (getUrlParams(req) != null) {
                    ctx.setVariables(getUrlParams(req));
                }

                templateEngine.process(mv.getViewName(), ctx, resp.getWriter());
            }

        } catch (Throwable e) {
            // show error page
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, e.toString());
        }
    }

    private Map<String, Object> getUrlParams(final HttpServletRequest request) {
        return (Map<String, Object>) request.getAttribute(URL_PARAMS);
    }

    protected IController resolveControllerForRequest(final HttpServletRequest request) {
        final String path = getRequestPath(request);

        // static files, eg. img, css
        if (ptStatic.matcher(path).matches()) {
            return null;
        }

        if (getControllerMap() == null || getControllerMap().isEmpty()) {
            logger.error("Empty Controller Map: Please define a controller mapping!");
            return null;
        }

        IController controller = getControllerMap().get(path);

        // not found, now check for wildcards
        if (controller == null) {
            for (Entry<String, IController> entry : getControllerMap().entrySet()) {
                UriTemplate uriTemplate = new UriTemplate(entry.getKey());

                if (uriTemplate.matches(path)) {
                    Map<String, String> params = uriTemplate.match(path);
                    request.setAttribute(URL_PARAMS, params);
                    return entry.getValue();
                }

            }
        }
        return controller;
    }

    public String getRequestPath(final HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        // final String contextPath = request.getContextPath();
        final String servletAlias = getServletAlias();

        final int fragmentIndex = requestURI.indexOf(';');
        if (fragmentIndex != -1) {
            requestURI = requestURI.substring(0, fragmentIndex);
        }

        // if (requestURI.startsWith(contextPath)) {
        // return requestURI.substring(contextPath.length());
        // }
        if (requestURI.startsWith(servletAlias)) {
            String path = requestURI.substring(servletAlias.length());
            return path.isEmpty() ? "/" : path;
        }
        return requestURI;
    }
}
