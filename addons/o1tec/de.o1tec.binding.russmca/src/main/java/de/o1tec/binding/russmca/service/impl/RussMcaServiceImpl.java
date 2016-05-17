package de.o1tec.binding.russmca.service.impl;

import java.util.ArrayList;
import java.util.List;

import de.o1tec.binding.russmca.RussMcaBindingConstants;
import de.o1tec.binding.russmca.internal.protocol.AdjustCommand.AdjustCommandType;
import de.o1tec.binding.russmca.internal.protocol.EventCommand.EventCommandType;
import de.o1tec.binding.russmca.internal.protocol.GetCommand.GetCommandType;
import de.o1tec.binding.russmca.internal.protocol.RequestResponseFactory;
import de.o1tec.binding.russmca.internal.protocol.RussRioConnection;
import de.o1tec.binding.russmca.internal.protocol.SetCommand.SetCommandType;
import de.o1tec.binding.russmca.internal.protocol.WatchCommand.WatchCommandType;
import de.o1tec.binding.russmca.protocol.RussCommand;
import de.o1tec.binding.russmca.protocol.RussConnection;
import de.o1tec.binding.russmca.protocol.RussConnectionException;
import de.o1tec.binding.russmca.protocol.RussResponse;
import de.o1tec.binding.russmca.protocol.event.RussDisconnectionEvent;
import de.o1tec.binding.russmca.protocol.event.RussDisconnectionListener;
import de.o1tec.binding.russmca.protocol.event.RussStatusUpdateEvent;
import de.o1tec.binding.russmca.protocol.event.RussUpdateListener;
import de.o1tec.binding.russmca.service.RussMcaService;
import de.o1tec.binding.russmca.service.event.RussResponseEvent;
import de.o1tec.binding.russmca.service.event.RussResponseListener;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RussMcaServiceImpl implements RussMcaService, RussUpdateListener, RussDisconnectionListener {

    private static final String RESPONSE_SUCCESS = "S";
    private static final String RESPONSE_ERROR = "E";
    private static final String RESPONSE_NOTIFICATION = "N";

    private Logger logger = LoggerFactory.getLogger(RussMcaServiceImpl.class);
    private RussConnection connection;

    private boolean[] bZoneWatched;

    private List<RussResponseListener> responseListeners;

    public RussMcaServiceImpl() {
        this.responseListeners = new ArrayList<>();
    }

    /**
     * Start service.
     */
    public void activate(ComponentContext context) {
        logger.debug("Starting RussMcaService Service...");
    }

    /**
     * Stop service.
     */
    public void deactivate(ComponentContext context) {
        logger.debug("Stopping RussMcaService Service...");
        stopService();
    }

    @Override
    public void initializeService(String receiverHost, Integer ipControlPort, Integer numControllers) {
        connection = new RussRioConnection(receiverHost, ipControlPort, numControllers);
        connection.addDisconnectionListener(this);
        connection.addUpdateListener(this);

        bZoneWatched = new boolean[numControllers * RussMcaBindingConstants.MCAC5_ZONE_COUNT];

        logger.debug("Russound MCA Service @{} started", getConnectionName());
    }

    @Override
    public void stopService() {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public RussConnection getConnection() {
        return connection;
    }

    @Override
    public String getConnectionName() {
        return connection.getConnectionName();
    }

    @Override
    public void addResponseListener(RussResponseListener listener) {
        synchronized (responseListeners) {
            responseListeners.add(listener);
        }
    }

    @Override
    public boolean querySystemStatus() {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SYSTEM_STATUS));
    }

    @Override
    public boolean querySystemLanguage() {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SYSTEM_LANGUAGE));
    }

    @Override
    public boolean queryControllerIpAddress(int controller) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.IP_ADDRESS, controller));
    }

    @Override
    public boolean queryControllerMacAdress(int controller) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.MAC_ADDRESS, controller));
    }

    @Override
    public boolean queryControllerType(int controller) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.TYPE, controller));
    }

    @Override
    public boolean queryZoneName(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_NAME, zone));
    }

    @Override
    public boolean queryZoneCurrentSource(int zone) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_CURRENT_SOURCE, zone));
    }

    @Override
    public boolean queryZoneVolume(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_VOLUME, zone));
    }

    @Override
    public boolean queryZoneBass(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_BASS, zone));
    }

    @Override
    public boolean queryZoneTreble(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_TREBLE, zone));
    }

    @Override
    public boolean queryZoneBalance(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_BALANCE, zone));
    }

    @Override
    public boolean queryZoneLoudness(int zone) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean queryZoneTurnOnVolume(int zone) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_TURN_ON_VOL, zone));
    }

    @Override
    public boolean queryZoneDnd(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_DND, zone));
    }

    @Override
    public boolean queryZonePartyMode(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_PARTY_MODE, zone));
    }

    @Override
    public boolean queryZonePower(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_STATUS, zone));
    }

    @Override
    public boolean queryZoneMute(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_MUTE, zone));
    }

    @Override
    public boolean queryZoneSharedSource(int zone) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_SHARED_SOURCE, zone));
    }

    @Override
    public boolean queryZoneLastError(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_LAST_ERROR, zone));
    }

    @Override
    public boolean queryZonePage(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_PAGE, zone));
    }

    @Override
    public boolean queryZoneSleepTimeRemaining(int zone) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_SLEEPTIME_REMAINING, zone));
    }

    @Override
    public boolean queryFavoriteValid(int zone, int favorite) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_FAV_VALID, zone, favorite));
    }

    @Override
    public boolean queryFavoritename(int zone, int favorite) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_FAV_NAME, zone, favorite));
    }

    @Override
    public boolean queryZoneAvailable(int zone) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_ENABLED, zone));
    }

    @Override
    public boolean queryZoneSourceAvailable(int zone, int source) {
        return connection.sendCommand(
                RequestResponseFactory.getIpControlCommand(GetCommandType.ZONE_SOURCE_ENABLED, zone, source));
    }

    @Override
    public boolean querySourceName(int source) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_NAME, source));
    }

    @Override
    public boolean querySourceType(int source) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_TYPE, source));
    }

    @Override
    public boolean querySourceComposerName(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_COMPOSER_NAME, source));
    }

    @Override
    public boolean querySourceIpAddress(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_IP_ADDRESS, source));
    }

    @Override
    public boolean querySourceChannel(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_CHANNEL, source));
    }

    @Override
    public boolean querySourcecoverArtURL(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_COVERART_URL, source));
    }

    @Override
    public boolean querySourceChannelName(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_CHANNEL_NAME, source));
    }

    @Override
    public boolean querySourceGenre(int source) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_GENRE, source));
    }

    @Override
    public boolean querySourceArtistName(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_ARTIST_NAME, source));
    }

    @Override
    public boolean querySourceAlbumName(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_ALBUM_NAME, source));
    }

    @Override
    public boolean querySourcePlaylistName(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_PLAYLIST_NAME, source));
    }

    @Override
    public boolean querySourceSongName(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_SONG_NAME, source));
    }

    @Override
    public boolean querySourceProgramServiceName(int source) {
        return connection.sendCommand(
                RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_PROGRAMSERVICE_NAME, source));
    }

    @Override
    public boolean querySourceRadioText(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_RADIO_TEXT, source));
    }

    @Override
    public boolean querySourceRadioText2(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_RADIO_TEXT2, source));
    }

    @Override
    public boolean querySourceRadioText3(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_RADIO_TEXT3, source));
    }

    @Override
    public boolean querySourceRadioText4(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_RADIO_TEXT4, source));
    }

    @Override
    public boolean querySourceShuffleMode(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_SHUFFLE_MODE, source));
    }

    @Override
    public boolean querySourceRepeatMode(int source) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_REPEAT_MODE, source));
    }

    @Override
    public boolean querySourceMode(int source) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_MODE, source));
    }

    @Override
    public boolean querySourceSupportMMLongList(int source) {
        return connection.sendCommand(
                RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_SUPPORT_MM_LONGLIST, source));
    }

    @Override
    public boolean querySourceSupportRating(int source) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_RATING, source));
    }

    @Override
    public boolean querySourceBankName(int source, int bank) {
        return connection
                .sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_BANK_NAME, source, bank));
    }

    @Override
    public boolean querySourcePresetName(int source, int bank, int preset) {
        return connection.sendCommand(RequestResponseFactory.getIpControlCommand(GetCommandType.SOURCE_BANK_PRESET_NAME,
                source, bank, preset));
    }

    @Override
    public boolean querySourcePresetValid(int source, int bank, int preset) {
        return connection.sendCommand(RequestResponseFactory
                .getIpControlCommand(GetCommandType.SOURCE_BANK_PRESET_VALID, source, bank, preset));
    }

    @Override
    public boolean watchZone(int zone, boolean on) {
        if (zoneAllowed(zone)) {
            bZoneWatched[zone - 1] = on;
            return connection.sendCommand(RequestResponseFactory.getIpControlCommand(
                    on ? WatchCommandType.ZONE_WATCH_ON : WatchCommandType.ZONE_WATCH_OFF, getRussController(zone),
                    getRussZone(zone)));
        }
        return false;
    }

    @Override
    public boolean watchAllZones(boolean on) {
        for (int controller = 0; controller < connection.getControllerCount(); controller++) {
            for (int zone = 0; zone < RussMcaBindingConstants.MCAC5_ZONE_COUNT; zone++) {
                int z = (controller * RussMcaBindingConstants.MCAC5_ZONE_COUNT) + zone;
                if (!bZoneWatched[z]) {
                    bZoneWatched[z] = on;
                    connection.sendCommand(RequestResponseFactory.getIpControlCommand(
                            on ? WatchCommandType.ZONE_WATCH_ON : WatchCommandType.ZONE_WATCH_OFF, controller + 1,
                            zone + 1));
                }
            }
        }
        return true;
    }

    @Override
    public boolean zoneSource(int zone, int source) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(EventCommandType.SELECT_PHYSICAL_SOURCE,
                getRussController(zone), getRussZone(zone), source);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zonePower(int zone, boolean on) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(
                on ? EventCommandType.ZONE_ON : EventCommandType.ZONE_OFF, getRussController(zone), getRussZone(zone));
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean allZonesPower(boolean on) {
        RussCommand commandToSend = RequestResponseFactory
                .getIpControlCommand(on ? EventCommandType.ALL_ZONES_ON : EventCommandType.ALL_ZONES_OFF);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean toggleMute(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(EventCommandType.MUTE,
                getRussController(zone), getRussZone(zone));
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean toggleShuffle(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(EventCommandType.SHUFFLE_MODE,
                getRussController(zone), getRussZone(zone));
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean toggleRepeat(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(EventCommandType.REPEAT_MODE,
                getRussController(zone), getRussZone(zone));
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneVolumeUp(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(EventCommandType.VOLUME_UP,
                getRussController(zone), getRussZone(zone));
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneVolumeDown(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(EventCommandType.VOLUME_DOWN,
                getRussController(zone), getRussZone(zone));
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneVolume(int zone, int volume) {
        if (volume < 0) {
            volume = 0;
        }
        if (volume > 50) {
            volume = 50;
        }

        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(EventCommandType.VOLUME,
                getRussController(zone), getRussZone(zone), volume);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zonePartyMode(int zone, OnOffMaster mode) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(EventCommandType.PARTY_MODE,
                getRussController(zone), getRussZone(zone), mode.getValue());
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneDnd(int zone, OnOff mode) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(EventCommandType.DND,
                getRussController(zone), getRussZone(zone), mode.getValue());
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneBass(int zone, int value) {
        if (value < -10) {
            value = -10;
        }
        if (value > 10) {
            value = 10;
        }
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(SetCommandType.ZONE_BASS,
                getRussController(zone), getRussZone(zone), value);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneBassUp(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(AdjustCommandType.ZONE_BASS,
                getRussController(zone), getRussZone(zone), "+1");
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneBassDown(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(AdjustCommandType.ZONE_BASS,
                getRussController(zone), getRussZone(zone), "-1");
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneTreble(int zone, int value) {
        if (value < -10) {
            value = -10;
        }
        if (value > 10) {
            value = 10;
        }

        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(SetCommandType.ZONE_TREBLE,
                getRussController(zone), getRussZone(zone), value);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneTrebleUp(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(AdjustCommandType.ZONE_TREBLE,
                getRussController(zone), getRussZone(zone), "+1");
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneTrebleDown(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(AdjustCommandType.ZONE_TREBLE,
                getRussController(zone), getRussZone(zone), "-1");
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneBalance(int zone, int value) {
        if (value < -10) {
            value = -10;
        }
        if (value > 10) {
            value = 10;
        }
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(SetCommandType.ZONE_BALANCE,
                getRussController(zone), getRussZone(zone), value);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneBalanceLeft(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(AdjustCommandType.ZONE_BALANCE,
                getRussController(zone), getRussZone(zone), "-1");
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneBalanceRight(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(AdjustCommandType.ZONE_BALANCE,
                getRussController(zone), getRussZone(zone), "+1");
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneTurnOnVolume(int zone, int value) {
        if (value < 0) {
            value = 0;
        }
        if (value > 50) {
            value = 50;
        }
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(SetCommandType.ZONE_TURN_ON_VOL,
                getRussController(zone), getRussZone(zone), value);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneTurnOnVolumeUp(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(AdjustCommandType.ZONE_TURN_ON_VOL,
                getRussController(zone), getRussZone(zone), "+1");
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneTurnOnVolumeDown(int zone) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(AdjustCommandType.ZONE_TURN_ON_VOL,
                getRussController(zone), getRussZone(zone), "-1");
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneSleepTimeRemaining(int zone, int minutes) {
        if (minutes < 0) {
            minutes = 0;
        }
        if (minutes > 60) {
            minutes = 60;
        }
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(SetCommandType.ZONE_SLEEPTIME_REMAINING,
                getRussController(zone), getRussZone(zone), minutes);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean zoneLoudness(int zone, boolean on) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(SetCommandType.ZONE_LOUDNESS,
                getRussController(zone), getRussZone(zone), on ? OnOff.ON : OnOff.OFF);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public boolean sourceBankName(int source, String name) {
        RussCommand commandToSend = RequestResponseFactory.getIpControlCommand(SetCommandType.SOURCE_BANK_NAME, source,
                name);
        return connection.sendCommand(commandToSend);
    }

    @Override
    public void onDisconnection(RussDisconnectionEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void statusUpdateReceived(RussStatusUpdateEvent event) {
        try {
            if (event.getData().startsWith(RESPONSE_ERROR)) {
                logger.error("Response Error: {}", event.getData());
            } else if ("S".equalsIgnoreCase(event.getData())) {
                logger.debug("Response Success");
            }
            // eventData can contain multiple ResponseTypes, so split it up if it is not Notification
            else if (event.getData().startsWith(RESPONSE_NOTIFICATION)) {
                handleStatusUpdateReceived(event.getData());
            } else {
                for (String line : event.getData().split(",")) {
                    handleStatusUpdateReceived(line);
                }
            }
        } catch (RussConnectionException e) {
            logger.warn("Unkown response type from Russound MCA-C5 @{}. Response discarded: {}", event.getData(),
                    event.getConnection());
        }
    }

    private void handleStatusUpdateReceived(String data) {
        RussResponse response = RequestResponseFactory.getIpControlResponse(data);
        if (logger.isDebugEnabled()) {
            logger.debug("Status update received: Type : {}, Controller: {}, Zone: {}", response.getResponseType(),
                    response.getController(), response.getZone());
        }

        // notify all listeners
        RussResponseEvent event = new RussResponseEvent(response);
        synchronized (responseListeners) {
            for (RussResponseListener russEventListener : responseListeners) {
                russEventListener.russResponseReceived(event);
            }
        }
    }

    protected Integer getRussController(int logicalZone) {
        return (logicalZone / (RussMcaBindingConstants.MCAC5_ZONE_COUNT + 1)) + 1;
    }

    protected Integer getRussZone(int logicalZone) {
        int z = logicalZone % RussMcaBindingConstants.MCAC5_ZONE_COUNT;
        if (z == 0) {
            z = RussMcaBindingConstants.MCAC5_ZONE_COUNT;
        }
        return z;
    }

    protected Integer getRussLogicalZone(int controller, int zone) {
        return (controller - 1) * RussMcaBindingConstants.MCAC5_ZONE_COUNT + zone;
    }

    protected boolean zoneAllowed(int zone) {
        return zone > 0 && zone <= connection.getControllerCount() * RussMcaBindingConstants.MCAC5_ZONE_COUNT;
    }

}
