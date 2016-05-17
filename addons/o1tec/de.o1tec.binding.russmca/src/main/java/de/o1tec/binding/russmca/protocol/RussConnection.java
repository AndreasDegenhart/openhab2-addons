/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.protocol;

import de.o1tec.binding.russmca.protocol.event.RussDisconnectionListener;
import de.o1tec.binding.russmca.protocol.event.RussUpdateListener;

/**
 * Represent a connection to a remote Russound Controller
 *
 * @author Andreas Degenhart
 *
 */
public interface RussConnection {

    /**
     * Add an update listener. It is notified when an update is received from the Controller.
     *
     * @param listener
     */
    public void addUpdateListener(RussUpdateListener listener);

    /**
     * Add a disconnection listener. It is notified when the Controller is disconnected.
     *
     * @param listener
     */
    public void addDisconnectionListener(RussDisconnectionListener listener);

    /**
     * Connect to the receiver. Return true if the connection has succeeded or if already connected.
     *
     **/
    public boolean connect();

    /**
     * Return true if this manager is connected to the Controller.
     *
     * @return
     */
    public boolean isConnected();

    /**
     * Closes the connection.
     **/
    public void close();

    public int getControllerCount();

    // public Integer getRussController(int logicalZone);
    //
    // public Integer getRussZone(int logicalZone);
    //
    // public Integer getRussLogicalZone(int controller, int zone);

    public boolean sendCommand(RussCommand ipControlCommand);

    // /**
    // * Send a system status query to the Controller
    // *
    // * @return
    // */
    // public boolean sendSystemStatusQuery();
    //
    // public boolean sendSystemLanguageQuery();
    //
    // public boolean sendWatchZoneQuery(int zone, boolean on);
    //
    // public boolean sendWatchAllZonesQuery(boolean on);

    // /**
    // * Send a power command ot the Controller based on the OpenHAB command
    // *
    // * @param command
    // * @return
    // */
    // public boolean sendPowerCommand(int zone, Command command) throws CommandTypeNotSupportedException;
    //
    // /**
    // * Send a volume command to the Controller based on the OpenHAB command
    // *
    // * @param command
    // * @return
    // */
    // public boolean sendVolumeCommand(int zone, Command command) throws CommandTypeNotSupportedException;
    //
    // /**
    // * Send a mute command to the Controller based on the OpenHAB command
    // *
    // * @param command
    // * @return
    // */
    // public boolean sendMuteCommand(int zone, Command command) throws CommandTypeNotSupportedException;
    //
    // public boolean sendBassCommand(int zone, Command command) throws CommandTypeNotSupportedException;
    //
    // public boolean sendTrebleCommand(int zone, Command command) throws CommandTypeNotSupportedException;
    //
    // public boolean sendBalanceCommand(int zone, Command command) throws CommandTypeNotSupportedException;
    //
    // /**
    // * Send a source input selection command to the Controller based on the OpenHAB command
    // *
    // * @param command
    // * @return
    // */
    // public boolean sendInputSourceCommand(Command command) throws CommandTypeNotSupportedException;

    /**
     * Return the connection name
     *
     * @return
     */
    public String getConnectionName();

}