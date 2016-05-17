/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca;

import java.util.regex.Pattern;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link RussMcaBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Andreas Degenhart - Initial contribution
 */
public class RussMcaBindingConstants {

    // default zone count
    public static final int MCAC5_ZONE_COUNT = 8;

    public static final String BINDING_ID = "russmca";

    // List of all Thing Type UIDs
    public final static ThingTypeUID RUSSOUND_MCA_THING_TYPE = new ThingTypeUID(BINDING_ID, "controller");

    // List of thing parameters names
    public final static String HOST_PARAMETER = "address";
    public final static String TCP_PORT_PARAMETER = "port";
    public static final Object CONTROLLER_COUNT = "controllerCount";

    public final static Pattern CHANNEL_SYSTEM_PATTERN = Pattern.compile("system#(.*?)");
    public final static Pattern CHANNEL_ZONE_PATTERN = Pattern.compile("zone(\\d+)#(.*?)");
    public final static Pattern CHANNEL_SOURCE_PATTERN = Pattern.compile("source(\\d+)#(.*?)");

    // List of all Channel ids
    public final static String CHANNEL_SYSTEM_FMT = "system#%s";
    public final static String CHANNEL_ZONE_FMT = "zone%d#%s";
    public final static String CHANNEL_SOURCE_FMT = "source%d#%s";

    public final static String CHANNEL_POWER_ALL = "allZonesPower";

    public final static String CHANNEL_NAME = "name";
    public final static String CHANNEL_CURRENT_SOURCE = "currentSource";

    public final static String CHANNEL_VOLUME_DIMMER = "volumeDimmer";
    public final static String CHANNEL_VOLUME_ABS = "volumeAbsolute";

    public final static String CHANNEL_BASS_DIMMER = "bassDimmer";
    public final static String CHANNEL_BASS_ABS = "bassAbsolute";

    public final static String CHANNEL_TREBLE_DIMMER = "trebleDimmer";
    public final static String CHANNEL_TREBLE_ABS = "trebleAbsolute";

    public final static String CHANNEL_BALANCE_DIMMER = "balanceDimmer";
    public final static String CHANNEL_BALANCE_ABS = "balanceAbsolute";

    public final static String CHANNEL_TURNON_VALUE_DIMMER = "turnOnVolumeDimmer";
    public final static String CHANNEL_TURNON_VALUE_ABS = "turnOnVolumeAbsolute";

    public final static String CHANNEL_POWER = "power";
    public final static String CHANNEL_MUTE = "mute";
    public final static String CHANNEL_LOUDNESS = "loudness";

    public final static String CHANNEL_DND = "dnd";
    public final static String CHANNEL_PARTY_MODE = "partyMode";
    public final static String CHANNEL_SHARED_SOURCE = "sharedSource";
    public final static String CHANNEL_PAGE = "page";
    public final static String CHANNEL_SLEEPTIME_REMAINING = "sleepTimeRemaining";

    public final static String CHANNEL_SOURCE_TYPE = "type";
    public final static String CHANNEL_SOURCE_NAME = "name";
    public final static String CHANNEL_SOURCE_COMPOSER_NAME = "composerName";
    public final static String CHANNEL_SOURCE_CHANNEL_NAME = "channelName";
    public final static String CHANNEL_SOURCE_GENRE = "genre";
    public final static String CHANNEL_SOURCE_ARTIST_NAME = "artistName";
    public final static String CHANNEL_SOURCE_ALBUM_NAME = "albumName";
    public final static String CHANNEL_SOURCE_PLAYLIST_NAME = "playlistName";
    public final static String CHANNEL_SOURCE_SONG_NAME = "songName";

    public final static String CHANNEL_SOURCE_COVER_ART_URL = "coverArtURL";
    public final static String CHANNEL_SOURCE_CHANNEL = "channel";
    public final static String CHANNEL_SOURCE_PROGRAMM_SERVICE_NAME = "programServiceName";
    public final static String CHANNEL_SOURCE_RADIOTEXT = "radioText";
    public final static String CHANNEL_SOURCE_RADIOTEXT2 = "radioText2";
    public final static String CHANNEL_SOURCE_RADIOTEXT3 = "radioText3";
    public final static String CHANNEL_SOURCE_RADIOTEXT4 = "radioText4";

    public final static String CHANNEL_SOURCE_SHUFFLE_MODE = "shuffleMode";
    public final static String CHANNEL_SOURCE_REPEAT_MODE = "repeatMode";
}
