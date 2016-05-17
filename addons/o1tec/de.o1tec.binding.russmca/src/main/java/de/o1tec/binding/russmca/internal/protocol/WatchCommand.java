/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.internal.protocol;

import de.o1tec.binding.russmca.protocol.RussCommand;

/**
 * A Russound WATCH Command
 *
 * @author Andreas Degenhart
 *
 */
public class WatchCommand extends AbstractBaseCommand {

    /**
     * List of the WATCH commands.
     *
     * @author Andreas Degenhart
     *
     */
    public enum WatchCommandType implements RussCommand.CommandType {

        SYSTEM_WATCH_ON("System ON", 0),
        SYSTEM_WATCH_OFF("System OFF", 0),
        SOURCE_WATCH_ON("S[%d] ON", 1),
        SOURCE_WATCH_OFF("S[%d] OFF", 1),
        ZONE_WATCH_ON("C[%d].Z[%d] ON", 2),
        ZONE_WATCH_OFF("C[%d].Z[%d] OFF", 2);

        private String command;
        private int parameterCount;

        private WatchCommandType(String command, int parameterCount) {
            this.command = command;
            this.parameterCount = parameterCount;
        }

        @Override
        public String getCommand() {
            return command;
        }

        public int getParameterCount() {
            return parameterCount;
        }
    }

    private static final String COMMAND_PREFIX = "WATCH ";

    protected WatchCommand(WatchCommandType command) {
        super(command, command.getParameterCount());
    }

    @Override
    protected String commandPrefix() {
        return COMMAND_PREFIX;
    }

}
