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
 * A Russound GET Command
 *
 * @author Andreas Degenhart
 *
 */
public class AdjustCommand extends AbstractBaseCommand {

    /**
     * List of the ADJUST commands.
     *
     * @author Andreas Degenhart
     *
     */
    public enum AdjustCommandType implements RussCommand.CommandType {

        ZONE_BASS("C[%d].Z[%d].bass=\"%d\"", 3),
        ZONE_TREBLE("C[%d].Z[%d].treble=\"%d\"", 3),
        ZONE_BALANCE("C[%d].Z[%d].balance=\"%d\" ", 3),
        ZONE_TURN_ON_VOL("C[%d].Z[%d].turnOnVloume=\"%d\"", 3);

        private String command;
        private int parameterCount;

        private AdjustCommandType(String command, int parameterCount) {
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

    private static final String COMMAND_PREFIX = "ADJUST ";

    protected AdjustCommand(AdjustCommandType command) {
        super(command, command.getParameterCount());
    }

    @Override
    protected String commandPrefix() {
        return COMMAND_PREFIX;
    }

}
