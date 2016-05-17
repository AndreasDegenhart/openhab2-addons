/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.internal;

import static de.o1tec.binding.russmca.RussMcaBindingConstants.RUSSOUND_MCA_THING_TYPE;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import de.o1tec.binding.russmca.handler.RussMcaHandler;
import de.o1tec.binding.russmca.service.RussMcaService;

/**
 * The {@link RussMcaHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Andreas Degenhart - Initial contribution
 */
public class RussMcaHandlerFactory extends BaseThingHandlerFactory {

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(RUSSOUND_MCA_THING_TYPE);

    private RussMcaService russMcaService;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(RUSSOUND_MCA_THING_TYPE)) {
            return new RussMcaHandler(thing, russMcaService);
        }

        return null;
    }

    protected void setRussMcaService(RussMcaService russMcaService) {
        this.russMcaService = russMcaService;
    }

    protected void unsetRussMcaService(RussMcaService russMcaService) {
        this.russMcaService = null;
    }

}
