/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.internal.protocol;

import de.o1tec.binding.russmca.internal.protocol.AdjustCommand.AdjustCommandType;
import de.o1tec.binding.russmca.internal.protocol.EventCommand.EventCommandType;
import de.o1tec.binding.russmca.internal.protocol.GetCommand.GetCommandType;
import de.o1tec.binding.russmca.internal.protocol.SetCommand.SetCommandType;
import de.o1tec.binding.russmca.internal.protocol.WatchCommand.WatchCommandType;

/**
 * Factory that allows to build IpControl commands/responses.
 *
 * @author Andreas Degenhart
 *
 */
public final class RequestResponseFactory {

    /**
     * Return a GetCommand of the type given in parameter. The
     * parameter of the command is set with the given paramter value.
     *
     * @param command
     * @param parameter
     * @return
     */
    public static GetCommand getIpControlCommand(GetCommandType command, Object... parameter) {
        GetCommand result = new GetCommand(command);
        result.setParameter(parameter);
        return result;
    }

    /**
     * Return a SetCommand of the type given in parameter. The
     * parameter of the command is set with the given paramter value.
     *
     * @param command
     * @param parameter
     * @return
     */
    public static SetCommand getIpControlCommand(SetCommandType command, Object... parameter) {
        SetCommand result = new SetCommand(command);
        result.setParameter(parameter);
        return result;
    }

    /**
     * Return a AdjustCommand of the type given in parameter. The
     * parameter of the command is set with the given paramter value.
     *
     * @param command
     * @param parameter
     * @return
     */
    public static AdjustCommand getIpControlCommand(AdjustCommandType command, Object... parameter) {
        AdjustCommand result = new AdjustCommand(command);
        result.setParameter(parameter);
        return result;
    }

    /**
     * Return a EventCommand of the type given in parameter. The
     * parameter of the command is set with the given paramter value.
     *
     * @param command
     * @param parameter
     * @return
     */
    public static EventCommand getIpControlCommand(EventCommandType command, Object... parameter) {
        EventCommand result = new EventCommand(command);
        result.setParameter(parameter);
        return result;
    }

    /**
     * Return a WatchCommand of the type given in parameter. The
     * parameter of the command is set with the given paramter value.
     *
     * @param command
     * @param parameter
     * @return
     */
    public static WatchCommand getIpControlCommand(WatchCommandType command, Object... parameter) {
        WatchCommand result = new WatchCommand(command);
        result.setParameter(parameter);
        return result;
    }

    /**
     * Return a IpControlResponse object based on the given response data.
     *
     * @param responseData
     * @return
     */
    public static Response getIpControlResponse(String responseData) {
        return new Response(responseData);
    }

}
