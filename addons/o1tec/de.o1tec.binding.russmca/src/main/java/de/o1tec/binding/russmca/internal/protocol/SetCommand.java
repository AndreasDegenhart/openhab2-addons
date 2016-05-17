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
public class SetCommand extends AbstractBaseCommand {

    /**
     * List of the SET commands.
     *
     * @author Andreas Degenhart
     *
     */
    public enum SetCommandType implements RussCommand.CommandType {

        SYSTEM_LANGUAGE("System.language=\"%s\"", 1),

        ZONE_BASS("C[%d].Z[%d].bass=\"%d\"", 3),
        ZONE_TREBLE("C[%d].Z[%d].treble=\"%d\"", 3),
        ZONE_BALANCE("C[%d].Z[%d].balance=\"%d\"", 3),
        ZONE_LOUDNESS("C[%d].Z[%d].loudness=\"%d\"", 3),
        ZONE_TURN_ON_VOL("C[%d].Z[%d].turnOnVloume=\"%d\"", 3),
        ZONE_SLEEPTIME_REMAINING("C[%d].Z[%d].sleepTimeRemaining=\"%d\"", 3),

        SOURCE_BANK_NAME("S[%d].B[%d].name=\"%s\"", 3);

        private String command;
        private int parameterCount;

        private SetCommandType(String command, int parameterCount) {
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

    private static final String COMMAND_PREFIX = "SET ";

    protected SetCommand(SetCommandType command) {
        super(command, command.getParameterCount());
    }

    @Override
    protected String commandPrefix() {
        return COMMAND_PREFIX;
    }

}
