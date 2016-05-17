/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.internal.protocol;

import java.util.IllegalFormatException;

import de.o1tec.binding.russmca.protocol.RussCommand;
import de.o1tec.binding.russmca.protocol.RussConnectionException;

/**
 * A command base class
 *
 * @author Andreas Degenhart
 *
 */
public abstract class AbstractBaseCommand implements RussCommand {

    private CommandType commandType;
    private Object[] parameter;
    private int parameterCount;

    protected abstract String commandPrefix();

    public AbstractBaseCommand(CommandType commandType, int parameterCount) {
        this.commandType = commandType;
        this.parameterCount = parameterCount;
    }

    /**
     * Return the command to send to the MCA-C with the parameter value configured.
     *
     * throws {@link RussConnectionException} if the parameter is null, empty or has a bad format.
     */
    @Override
    public String getCommand() throws RussConnectionException {
        String commandString = getCommandString();
        if (this.parameter == null) {
            if (this.parameterCount != 0) {
                throw new RussConnectionException("the command " + commandString + " expects no parameter.");
            }
            return commandString;
        } else {
            if (this.parameterCount != this.parameter.length) {
                throw new RussConnectionException("the parameter for the command " + commandString + " did not match.");
            }

            try {
                return String.format(commandString, this.parameter);
            } catch (IllegalFormatException e) {
                throw new RussConnectionException(
                        "the parameter format string for the command " + commandString + " is not correct");
            }
        }
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }

    public Object[] getParameter() {
        return parameter;
    }

    public void setParameter(Object[] parameter) {
        this.parameter = parameter;
    }

    private String getCommandString() {
        return commandPrefix() + commandType.getCommand() + "\r";
    }
}
