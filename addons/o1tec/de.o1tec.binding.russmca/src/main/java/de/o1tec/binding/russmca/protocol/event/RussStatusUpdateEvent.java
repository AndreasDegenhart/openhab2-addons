/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.protocol.event;

import de.o1tec.binding.russmca.protocol.RussConnection;

/**
 * The event fired when a status is received from the Controller.
 *
 * @author Andreas Degenhart
 */
public class RussStatusUpdateEvent {

    private RussConnection connection;
    private String data;

    public RussStatusUpdateEvent(RussConnection connection, String data) {
        this.connection = connection;
        this.data = data;
    }

    public RussConnection getConnection() {
        return connection;
    }

    public String getData() {
        return data;
    }

}
