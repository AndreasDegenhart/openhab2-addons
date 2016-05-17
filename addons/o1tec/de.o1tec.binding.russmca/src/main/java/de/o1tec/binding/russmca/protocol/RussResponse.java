/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.protocol;

import de.o1tec.binding.russmca.internal.protocol.Response.ResponseType;

/**
 * Represent a response of the Russound Controller.
 *
 * @author Andreas Degenhart
 *
 */
public interface RussResponse {

    /**
     * Represent the type of a response.
     *
     * @author Andreas Degenhart
     *
     */
    public interface RepsonseType {
        public static final int HAS_VALUE = 1;
        public static final int HAS_CONTROLLER = 2;
        public static final int HAS_ZONE = 4;
        public static final int HAS_SOURCE = 8;
        public static final int HAS_FAVORITE = 16;
        public static final int HAS_BANK = 32;
        public static final int HAS_PRESET = 64;

        /**
         * Return the parameter pattern (RegEx) of the response.
         *
         * @return
         */
        public String getParameterPattern();

        /**
         * Return true if the responseData matches with this responseType
         *
         * @param responseData
         * @return
         */
        public boolean match(String responseData);

        public boolean isNotification();

        public boolean hasParameterType(int type);

        public int getParameterCount();

        public String[] getParameter();
    }

    /**
     * Return the response type of this response
     *
     * @return
     */
    public ResponseType getResponseType();

    public Integer getController();

    public Integer getZone();

    public Integer getSource();

    public Integer getFavorite();

    public Integer getBank();

    public Integer getPreset();

    public Integer getLogicalZone();

    public String getValue();

}
