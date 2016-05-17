package de.o1tec.ui.smart.internal.framework.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IController {

    public ModelAndView process(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
