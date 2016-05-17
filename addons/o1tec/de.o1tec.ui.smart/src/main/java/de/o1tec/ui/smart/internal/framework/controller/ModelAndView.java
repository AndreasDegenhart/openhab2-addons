package de.o1tec.ui.smart.internal.framework.controller;

import java.util.Map;

public class ModelAndView {
    private String viewName;
    private Map<String, Object> model = null;

    public ModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
