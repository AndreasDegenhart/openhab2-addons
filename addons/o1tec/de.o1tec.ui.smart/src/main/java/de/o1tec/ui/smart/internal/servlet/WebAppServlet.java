package de.o1tec.ui.smart.internal.servlet;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.o1tec.ui.smart.internal.framework.controller.DefaultController;
import de.o1tec.ui.smart.internal.framework.controller.IController;
import de.o1tec.ui.smart.internal.framework.servlet.DispatchServlet;

public class WebAppServlet extends DispatchServlet {
    private final Logger logger = LoggerFactory.getLogger(WebAppServlet.class);

    private Map<String, IController> controllerURLMapping = new HashMap<String, IController>();

    @Override
    protected void activate(Map<String, Object> configProps) {
        super.activate(configProps);

        String platformName = config.getPlatformName();

        // setup controllerURLMapping
        controllerURLMapping.put("/", new DefaultController(platformName + "/index"));
        controllerURLMapping.put("/index", new DefaultController(platformName + "/index"));
        controllerURLMapping.put("/about", new DefaultController(platformName + "/about"));
    }

    @Override
    protected Map<String, IController> getControllerMap() {
        return controllerURLMapping;
    }

}
