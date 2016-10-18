package de.o1tec.binding.russmca.internal.actions;

import org.eclipse.smarthome.model.script.engine.action.ActionService;

import de.o1tec.binding.russmca.actions.RussMca;
import de.o1tec.binding.russmca.service.RussMcaService;

public class RussMcaActionService implements ActionService {

    public static RussMcaService russMcaService;

    @Override
    public String getActionClassName() {
        return RussMca.class.getCanonicalName();
    }

    @Override
    public Class<?> getActionClass() {
        return RussMca.class;
    }

    protected void setRussMcaService(RussMcaService service) {
        RussMcaActionService.russMcaService = service;
    }

    protected void unsetRussMcaService(RussMcaService service) {
        RussMcaActionService.russMcaService = null;
    }
}
