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
 * An event fired when an Russound Controller is disconnected.
 *
 * @author Andreas Degenhart
 *
 */
public class RussDisconnectionEvent {

    private RussConnection connection;
    private Throwable cause;

    public RussDisconnectionEvent(RussConnection connection, Throwable cause) {
        this.connection = connection;
        this.cause = cause;
    }

    public RussConnection getConnection() {
        return connection;
    }

    public Throwable getCause() {
        return cause;
    }

}
