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
public class GetCommand extends AbstractBaseCommand {

    /**
     * List of the GET commands.
     *
     * @author Andreas Degenhart
     *
     */
    public enum GetCommandType implements RussCommand.CommandType {

        SYSTEM_STATUS("System.status", 0),
        SYSTEM_LANGUAGE("System.language", 0),
        SYSTEM_FAV_VALID("System.favorite[%d].valid", 1),
        SYSTEM_FAV_NAME("System.favorite[%d].name", 1),
        IP_ADDRESS("C[%d].ipAddress", 1),
        MAC_ADDRESS("C[%d].macAddress", 1),
        TYPE("C[%d].type", 1),

        ZONE_NAME("C[%d].Z[%d].name", 2),
        ZONE_CURRENT_SOURCE("C[%d].Z[%d].currentSource", 2),
        ZONE_VOLUME("C[%d].Z[%d].volume", 2),
        ZONE_BASS("C[%d].Z[%d].bass", 2),
        ZONE_TREBLE("C[%d].Z[%d].treble", 2),
        ZONE_BALANCE("C[%d].Z[%d].balance", 2),
        ZONE_LOUDNESS("C[%d].Z[%d].loudness", 2),
        ZONE_TURN_ON_VOL("C[%d].Z[%d].turnOnVloume", 2),
        ZONE_DND("C[%d].Z[%d].doNotDisturb", 2),
        ZONE_PARTY_MODE("C[%d].Z[%d].partyMode", 2),
        ZONE_STATUS("C[%d].Z[%d].status", 2),
        ZONE_MUTE("C[%d].Z[%d].mute", 2),
        ZONE_SHARED_SOURCE("C[%d].Z[%d].sharedSource", 2),
        ZONE_LAST_ERROR("C[%d].Z[%d].lastError", 2),
        ZONE_PAGE("C[%d].Z[%d].page", 2),
        ZONE_SLEEPTIME_REMAINING("C[%d].Z[%d].sleepTimeRemaining", 2),
        ZONE_FAV_VALID("C[%d].Z[%d].favorite[%d].valid", 3),
        ZONE_FAV_NAME("C[%d].Z[%d].favorite[%d].name", 3),
        ZONE_ENABLED("C[%d].Z[%d].enabled", 2),
        ZONE_SOURCE_ENABLED("C[%d].Z[%d].S[%d].enabled", 3),

        SOURCE_NAME("S[%d].name", 1),
        SOURCE_TYPE("S[%d].type", 1),
        SOURCE_COMPOSER_NAME("S[%d].composerName", 1),
        SOURCE_IP_ADDRESS("S[%d].ipAddress", 1),
        SOURCE_CHANNEL("S[%d].channel", 1),
        SOURCE_COVERART_URL("S[%d].coverArtURL", 1),
        SOURCE_CHANNEL_NAME("S[%d].channelName", 1),
        SOURCE_GENRE("S[%d].genre", 1),
        SOURCE_ARTIST_NAME("S[%d].artistName", 1),
        SOURCE_ALBUM_NAME("S[%d].albumName", 1),
        SOURCE_PLAYLIST_NAME("S[%d].playlistName", 1),
        SOURCE_SONG_NAME("S[%d].songName", 1),
        SOURCE_PROGRAMSERVICE_NAME("S[%d].programServiceName", 1),
        SOURCE_RADIO_TEXT("S[%d].radioText", 1),
        SOURCE_RADIO_TEXT2("S[%d].radioText2", 1),
        SOURCE_RADIO_TEXT3("S[%d].radioText3", 1),
        SOURCE_RADIO_TEXT4("S[%d].radioText4", 1),
        SOURCE_SHUFFLE_MODE("S[%d].shuffleMode", 1),
        SOURCE_REPEAT_MODE("S[%d].repeatMode", 1),
        SOURCE_MODE("S[%d].mode", 1),
        SOURCE_SUPPORT_MM_LONGLIST("S[%d].Support.MM.longList", 1),
        SOURCE_RATING("S[%d].rating", 1),
        SOURCE_BANK_NAME("S[%d].B[%d].name", 2),
        SOURCE_BANK_PRESET_NAME("S[%d].B[%d].P[%d].name", 3),
        SOURCE_BANK_PRESET_VALID("S[%d].B[%d].P[%d].valid", 3);

        private String command;
        private int parameterCount;

        private GetCommandType(String command, int parameterCount) {
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

    private static final String COMMAND_PREFIX = "GET ";

    protected GetCommand(GetCommandType command) {
        super(command, command.getParameterCount());
    }

    @Override
    protected String commandPrefix() {
        return COMMAND_PREFIX;
    }

}
