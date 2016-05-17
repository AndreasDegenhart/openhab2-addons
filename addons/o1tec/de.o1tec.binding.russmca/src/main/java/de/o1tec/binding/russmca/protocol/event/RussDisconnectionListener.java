/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.protocol.event;

/**
 * A listener which is notified when an Russound Controller is disconnected.
 *
 * @author Andreas Degenhart
 *
 */
public interface RussDisconnectionListener {

    /**
     * Called when an Controller is disconnected.
     * 
     * @param event
     */
    public void onDisconnection(RussDisconnectionEvent event);

}
