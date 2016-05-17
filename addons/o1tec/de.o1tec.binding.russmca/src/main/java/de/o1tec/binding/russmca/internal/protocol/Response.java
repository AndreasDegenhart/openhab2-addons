/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.internal.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import de.o1tec.binding.russmca.RussMcaBindingConstants;
import de.o1tec.binding.russmca.protocol.RussConnectionException;
import de.o1tec.binding.russmca.protocol.RussResponse;

/**
 * Represent an Russound response.
 *
 * @author Andreas Degenhart
 *
 */
public class Response implements RussResponse {

    /**
     * List of all supported responses coming from AVR.
     *
     * @author Andreas Degenhart
     *
     */
    public enum ResponseType implements RussResponse.RepsonseType {

        CONTROLLER_IP_ADDRESS("C\\[(.*?)\\]\\.ipAdress=\"(.*?)\"", HAS_CONTROLLER | HAS_VALUE),
        CONTROLLER_MAC_ADDRESS("C\\[(.*?)\\]\\.macAdress=\"(.*?)\"", HAS_CONTROLLER | HAS_VALUE),
        CONTROLLER_TYPE("C\\[(.*?)\\]\\.type=\"(.*?)\"", HAS_CONTROLLER | HAS_VALUE),

        SYSTEM_STATUS("System\\.status=\"(.*?)\""),
        SYSTEM_LANGUAGE("System\\.language=\"(.*?)\""),
        SYSTEM_FAV_VALID("System\\.favorite\\[(.*?)\\]\\.valid=\"(.*?)\"", HAS_FAVORITE | HAS_VALUE),
        SYSTEM_FAV_NAME("System\\.favorite\\[(.*?)\\]\\.name=\"(.*?)\"", HAS_FAVORITE | HAS_VALUE),

        ZONE_NAME("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.name=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_CURRENT_SOURCE("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.currentSource=\"(.*?)\"",
                HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_VOLUME("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.volume=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_BASS("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.bass=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_TREBLE("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.treble=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_BALANCE("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.balance=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_LOUDNESS("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.loudness=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_TURN_ON_VOL("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.turnOnVolume=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_DND("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.doNotDisturb=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_PARTY_MODE("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.partyMode=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_STATUS("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.status=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_MUTE("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.mute=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_SHARED_SOURCE("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.sharedSource=\"(.*?)\"",
                HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_LAST_ERROR("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.lastError=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_PAGE("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.page=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_SLEEPTIME_REMAINING("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.sleepTimeRemaining=\"(.*?)\"",
                HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),

        ZONE_FAV_VALID("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.favorite\\[(.*?)\\]\\.valid=\"(.*?)\"",
                HAS_CONTROLLER | HAS_ZONE | HAS_FAVORITE | HAS_VALUE),
        ZONE_FAV_NAME("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.favorite\\[(.*?)\\]\\.name=\"(.*?)\"",
                HAS_CONTROLLER | HAS_ZONE | HAS_FAVORITE | HAS_VALUE),

        ZONE_ENABLED("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.enables=\"(.*?)\"", HAS_CONTROLLER | HAS_ZONE | HAS_VALUE),
        ZONE_SOURCE_ENABLED("C\\[(.*?)\\]\\.Z\\[(.*?)\\]\\.S\\[(.*?)\\]\\.valid=\"(.*?)\"",
                HAS_CONTROLLER | HAS_ZONE | HAS_SOURCE | HAS_VALUE),

        SOURCE_NAME("S\\[(.*?)\\]\\.name=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_TYPE("S\\[(.*?)\\]\\.type=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_COMPOSER_NAME("S\\[(.*?)\\]\\.composerName=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_IP_ADDRESS("S\\[(.*?)\\]\\.ipAddress=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_CHANNEL("S\\[(.*?)\\]\\.channel=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_COVERART_URL("S\\[(.*?)\\]\\.coverArtURL=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_CHANNEL_NAME("S\\[(.*?)\\]\\.channelName=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_GENRE("S\\[(.*?)\\]\\.genre=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_ARTIST_NAME("S\\[(.*?)\\]\\.artistName=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_ALBUM_NAME("S\\[(.*?)\\]\\.albumName=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_PLAYLIST_NAME("S\\[(.*?)\\]\\.playlistName=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_SONG_NAME("S\\[(.*?)\\]\\.songName=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_PROGRAMSERVICE_NAME("S\\[(.*?)\\]\\.programServiceName=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_RADIO_TEXT("S\\[(.*?)\\]\\.radioText=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_RADIO_TEXT2("S\\[(.*?)\\]\\.radioText2=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_RADIO_TEXT3("S\\[(.*?)\\]\\.radioText3=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_RADIO_TEXT4("S\\[(.*?)\\]\\.radioText4=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_SHUFFLE_MODE("S\\[(.*?)\\]\\.shuffleMode=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_REPEAT_MODE("S\\[(.*?)\\]\\.repeatMode=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_MODE("S\\[(.*?)\\]\\.mode=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_SUPPORT_MM_LONGLIST("S\\[(.*?)\\]\\.Support\\.MM\\.longList=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),
        SOURCE_RATING("S\\[(.*?)\\]\\.rating=\"(.*?)\"", HAS_SOURCE | HAS_VALUE),

        SOURCE_BANK_NAME("S\\[(.*?)\\]\\.B\\[(.*?)\\]\\.name=\"(.*?)\"", HAS_SOURCE | HAS_BANK | HAS_VALUE),
        SOURCE_BANK_PRESET_NAME("S\\[(.*?)\\]\\.B\\[(.*?)\\]\\.P\\[(.*?)\\]\\.name=\"(.*?)\"",
                HAS_SOURCE | HAS_BANK | HAS_PRESET | HAS_VALUE),
        SOURCE_BANK_PRESET_VALID("S\\[(.*?)\\]\\.B\\[(.*?)\\]\\.P\\[(.*?)\\]\\.valid=\"(.*?)\"",
                HAS_SOURCE | HAS_BANK | HAS_PRESET | HAS_VALUE);

        private String parameterPattern;
        private Pattern matchPattern;
        private String responseType;
        private int parameterType;
        private String[] parameter;

        private ResponseType(String parameterPattern) {
            this(parameterPattern, HAS_VALUE);
        }

        private ResponseType(String parameterPattern, int parameterType) {
            this.parameterPattern = parameterPattern;
            this.parameterType = parameterType;
            this.matchPattern = Pattern.compile("(.*?) " + parameterPattern);
        }

        @Override
        public String getParameterPattern() {
            return parameterPattern;
        }

        /**
         * Return true if the responseData matches with this responseType
         *
         * @param responseData
         * @return
         */
        @Override
        public boolean match(String responseData) {
            Matcher matcher = matchPattern.matcher(responseData);

            int parameterCount = matcher.groupCount();
            boolean matches = matcher.matches();
            if (matches) {
                responseType = matcher.group(1);
                parameter = new String[parameterCount - 1];
                for (int i = 0; i < parameterCount - 1; i++) {
                    parameter[i] = matcher.group(i + 2);
                }
            }
            return matches;
        }

        @Override
        public boolean isNotification() {
            return "N".equalsIgnoreCase(responseType);
        }

        @Override
        public int getParameterCount() {
            return parameter != null ? parameter.length : 0;
        }

        @Override
        public String[] getParameter() {
            return parameter;
        }

        @Override
        public boolean hasParameterType(int type) {
            return (this.parameterType & type) == type;
        }

    }

    private ResponseType responseType;
    private Integer controller;
    private Integer zone;
    private Integer source;
    private Integer favorite;
    private Integer bank;
    private Integer preset;
    private String value;

    public Response(String responseData) throws RussConnectionException {
        if (StringUtils.isEmpty(responseData)) {
            throw new RussConnectionException("responseData is empty. Cannot parse the response.");
        }

        this.responseType = parseResponseType(responseData);

        if (this.responseType == null) {
            throw new RussConnectionException("Cannot find the responseType of the responseData " + responseData);
        }

        int parameterCounter = 0;
        int parameterCount = this.responseType.getParameterCount();
        String[] parameter = this.responseType.getParameter();

        if (this.responseType.hasParameterType(RepsonseType.HAS_CONTROLLER)) {
            this.controller = parameterCount > parameterCounter ? Integer.valueOf(parameter[parameterCounter++]) : null;
        }
        if (this.responseType.hasParameterType(RepsonseType.HAS_ZONE)) {
            this.zone = parameterCount > parameterCounter ? Integer.valueOf(parameter[parameterCounter++]) : null;
        }
        if (this.responseType.hasParameterType(RepsonseType.HAS_SOURCE)) {
            this.source = parameterCount > parameterCounter ? Integer.valueOf(parameter[parameterCounter++]) : null;
        }
        if (this.responseType.hasParameterType(RepsonseType.HAS_FAVORITE)) {
            this.favorite = parameterCount > parameterCounter ? Integer.valueOf(parameter[parameterCounter++]) : null;
        }
        if (this.responseType.hasParameterType(RepsonseType.HAS_BANK)) {
            this.bank = parameterCount > parameterCounter ? Integer.valueOf(parameter[parameterCounter++]) : null;
        }
        if (this.responseType.hasParameterType(RepsonseType.HAS_PRESET)) {
            this.preset = parameterCount > parameterCounter ? Integer.valueOf(parameter[parameterCounter++]) : null;
        }
        if (this.responseType.hasParameterType(RepsonseType.HAS_VALUE)) {
            this.value = parameterCount > parameterCounter ? parameter[parameterCounter++] : null;
        }

    }

    /**
     * Return the responseType corresponding to the given responseData. Return
     * null if no ResponseType can be matched.
     *
     * @param responseData
     * @return
     */
    private ResponseType parseResponseType(String responseData) {
        ResponseType result = null;
        for (ResponseType responseType : ResponseType.values()) {
            if (responseType.match(responseData)) {
                result = responseType;
            }
        }
        return result;
    }

    @Override
    public ResponseType getResponseType() {
        return responseType;
    }

    @Override
    public Integer getController() {
        return controller;
    }

    @Override
    public Integer getZone() {
        return zone;
    }

    @Override
    public Integer getLogicalZone() {
        return (controller - 1) * RussMcaBindingConstants.MCAC5_ZONE_COUNT + zone;
    }

    @Override
    public Integer getSource() {
        return source;
    }

    @Override
    public Integer getFavorite() {
        return favorite;
    }

    @Override
    public Integer getBank() {
        return bank;
    }

    @Override
    public Integer getPreset() {
        return preset;
    }

    @Override
    public String getValue() {
        return value;
    }

}
