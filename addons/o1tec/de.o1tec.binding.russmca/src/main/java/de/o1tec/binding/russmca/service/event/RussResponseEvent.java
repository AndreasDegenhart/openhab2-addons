/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.service.event;

import de.o1tec.binding.russmca.protocol.RussResponse;

/**
 * The event fired when a status is received from the Controller.
 *
 * @author Andreas Degenhart
 */
public class RussResponseEvent {

    private RussResponse response;

    public RussResponseEvent(RussResponse response) {
        this.response = response;
    }

    public RussResponse getResponse() {
        return response;
    }
}
