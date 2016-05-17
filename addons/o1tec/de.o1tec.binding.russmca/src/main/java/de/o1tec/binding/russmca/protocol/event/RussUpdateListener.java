/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.protocol.event;

import java.util.EventListener;

/**
 * This interface defines interface to receive status updates from Russound Controller.
 *
 * @author Andreas Degenhart
 */
public interface RussUpdateListener extends EventListener {

    /**
     * Procedure for receive status update from Russound Controller.
     */
    public void statusUpdateReceived(RussStatusUpdateEvent event);

}
