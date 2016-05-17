/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.ui.smart.internal.framework.servlet;

import javax.servlet.http.HttpServlet;

import org.eclipse.smarthome.core.items.ItemRegistry;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/**
 * This is the base servlet class for other servlet in the Basic UI.
 *
 * @author Andreas Degenhart
 */
public abstract class BaseServlet extends HttpServlet {

    protected HttpService httpService;
    protected ItemRegistry itemRegistry;

    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void unsetItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = null;
    }

    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    public void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    /**
     * Creates a {@link HttpContext}
     *
     * @return a {@link HttpContext}
     */
    protected HttpContext createHttpContext() {
        HttpContext defaultHttpContext = httpService.createDefaultHttpContext();
        return defaultHttpContext;
    }

    protected abstract String getServletAlias();
}
