package de.o1tec.ui.smart.internal.framework.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultController implements IController {

    private String viewName;

    public DefaultController(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public ModelAndView process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView(viewName, null);
    }

}
