package de.o1tec.binding.russmca.actions;

import org.eclipse.smarthome.model.script.engine.action.ActionDoc;
import org.eclipse.smarthome.model.script.engine.action.ParamDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.o1tec.binding.russmca.internal.actions.RussMcaActionService;

public class RussMca {

    private static final Logger logger = LoggerFactory.getLogger(RussMca.class);

    @ActionDoc(text = "querys the ip adress of a specific controller")
    public static void playSound(@ParamDoc(name = "controller", text = "the controller number") int controller) {
        RussMcaActionService.russMcaService.queryControllerIpAddress(controller);
    }

}
