/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.protocol;

/**
 * The base interface for an Russound Command
 *
 * @author Andreas Degenhart
 *
 */
public interface RussCommand {

    /**
     * Represent a CommandType of command requests
     * 
     * @author Andreas Degenhart
     *
     */
    public interface CommandType {

        /**
         * Return the command of this command type
         * 
         * @return
         */
        public String getCommand();

        /**
         * Return the name of the command type
         * 
         * @return
         */
        public String name();
    }

    /**
     * Return the command to send to Russound Controller.
     * 
     * @return
     */
    public String getCommand();

    /**
     * Return the the command type of this command.
     * 
     * @return
     */
    public CommandType getCommandType();

}
