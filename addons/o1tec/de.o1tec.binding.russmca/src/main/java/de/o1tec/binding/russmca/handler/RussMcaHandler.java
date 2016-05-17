/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.handler;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import de.o1tec.binding.russmca.RussMcaBindingConstants;
import de.o1tec.binding.russmca.internal.states.ResponseStateValues;
import de.o1tec.binding.russmca.protocol.CommandTypeNotSupportedException;
import de.o1tec.binding.russmca.protocol.RussResponse;
import de.o1tec.binding.russmca.protocol.event.RussDisconnectionEvent;
import de.o1tec.binding.russmca.protocol.event.RussDisconnectionListener;
import de.o1tec.binding.russmca.service.RussMcaService;
import de.o1tec.binding.russmca.service.RussMcaService.OnOff;
import de.o1tec.binding.russmca.service.RussMcaService.OnOffMaster;
import de.o1tec.binding.russmca.service.event.RussResponseEvent;
import de.o1tec.binding.russmca.service.event.RussResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link RussMcaHandler} is responsible for handling commands, which are sent to one of the channels through
 * an
 * Russound connection.
 *
 * @author Andreas Degenhart - Initial contribution
 */
public class RussMcaHandler extends BaseThingHandler implements RussResponseListener, RussDisconnectionListener {

    private Logger logger = LoggerFactory.getLogger(RussMcaHandler.class);

    private RussMcaService russService = null;

    private ScheduledFuture<?> statusCheckerFuture;

    public RussMcaHandler(Thing thing, RussMcaService russService) {
        super(thing);

        String host = (String) this.getConfig().get(RussMcaBindingConstants.HOST_PARAMETER);
        Integer tcpPort = ((Number) this.getConfig().get(RussMcaBindingConstants.TCP_PORT_PARAMETER)).intValue();
        Integer controllerCount = ((Number) this.getConfig().get(RussMcaBindingConstants.CONTROLLER_COUNT)).intValue();

        this.russService = russService;
        russService.initializeService(host, tcpPort, controllerCount);
        russService.addResponseListener(this);
    }

    /**
     * Initialize the state of the AVR.
     */
    @Override
    public void initialize() {
        super.initialize();
        logger.debug("Initializing handler for Russound MCA-C5...");

        // only for testing
        russService.getConnection().addDisconnectionListener(this);

        // Start the status checker
        Runnable statusChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("Checking status of Russound MCA-C5 @{}", russService.getConnectionName());
                    checkStatus();
                } catch (LinkageError e) {
                    logger.warn("Failed to check the status for Russound MCA-C5 @{}. Cause: {}",
                            russService.getConnectionName(), e.getMessage());
                    // Stop to check the status of this Controller.
                    if (statusCheckerFuture != null) {
                        statusCheckerFuture.cancel(false);
                    }
                }
            }
        };
        statusCheckerFuture = scheduler.scheduleWithFixedDelay(statusChecker, 1, 10, TimeUnit.SECONDS);
    }

    /**
     * Close the connection and stop the status checker.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (statusCheckerFuture != null) {
            statusCheckerFuture.cancel(true);
        }
        russService.stopService();
    }

    /**
     * Check the status of the AVR. Return true if the AVR is online, else return false.
     *
     * @return
     */
    private void checkStatus() {
        // for Russound get the system status: GET System.status
        if (!russService.querySystemStatus()) {
            updateStatus(ThingStatus.OFFLINE);
        } else {
            // IF the power query has succeeded, the MCA-C5 status is ONLINE.
            updateStatus(ThingStatus.ONLINE);
        }
    }

    /**
     * Send a command to the Russound Controller based on the OpenHAB command received.
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        String strChannelUID = channelUID.getId();

        Matcher channelSystemMatcher = RussMcaBindingConstants.CHANNEL_SYSTEM_PATTERN.matcher(strChannelUID);
        Matcher channelZoneMatcher = RussMcaBindingConstants.CHANNEL_ZONE_PATTERN.matcher(strChannelUID);
        Matcher channelSourceMatcher = RussMcaBindingConstants.CHANNEL_SOURCE_PATTERN.matcher(strChannelUID);
        if (channelZoneMatcher.matches()) {
            if (channelZoneMatcher.groupCount() == 2) {
                int zone = Integer.valueOf(channelZoneMatcher.group(1));
                String cmd = channelZoneMatcher.group(2);
                handleZoneCommand(command, zone, cmd);
            }
        } else if (channelSourceMatcher.matches()) {
            if (channelSourceMatcher.groupCount() == 2) {
                int source = Integer.valueOf(channelSourceMatcher.group(1));
                String cmd = channelSourceMatcher.group(2);
                handleSourceCommand(command, source, cmd);
            }
        } else if (channelSystemMatcher.matches()) {
            String cmd = channelSourceMatcher.group(1);
            handleSystemCommand(command, cmd);
        }

    }

    /**
     *
     * @param command
     * @param zone
     * @param channel
     */
    public void handleZoneCommand(Command command, int zone, String channel) {
        try {
            switch (channel) {
                case RussMcaBindingConstants.CHANNEL_POWER:
                    sendZonePowerCommand(zone, command);
                    break;
                case RussMcaBindingConstants.CHANNEL_MUTE:
                    russService.toggleMute(zone);
                    break;
                case RussMcaBindingConstants.CHANNEL_LOUDNESS:
                    sendZoneLoudnessCommand(zone, command);
                    break;
                case RussMcaBindingConstants.CHANNEL_VOLUME_ABS:
                case RussMcaBindingConstants.CHANNEL_VOLUME_DIMMER:
                    sendVolumeCommand(zone, command);
                    break;
                case RussMcaBindingConstants.CHANNEL_BASS_ABS:
                case RussMcaBindingConstants.CHANNEL_BASS_DIMMER:
                    sendBassCommand(zone, command);
                    break;
                case RussMcaBindingConstants.CHANNEL_TREBLE_ABS:
                case RussMcaBindingConstants.CHANNEL_TREBLE_DIMMER:
                    sendTrebleCommand(zone, command);
                    break;
                case RussMcaBindingConstants.CHANNEL_BALANCE_ABS:
                case RussMcaBindingConstants.CHANNEL_BALANCE_DIMMER:
                    sendBalanceCommand(zone, command);
                    break;
                case RussMcaBindingConstants.CHANNEL_TURNON_VALUE_ABS:
                case RussMcaBindingConstants.CHANNEL_TURNON_VALUE_DIMMER:
                    sendTurnOnVolumeCommand(zone, command);
                    break;
                case RussMcaBindingConstants.CHANNEL_DND:
                    sendZoneDndCommand(zone, command);
                    break;
                case RussMcaBindingConstants.CHANNEL_PARTY_MODE:
                    sendZonePartyModeCommand(zone, command);
                    break;
                case RussMcaBindingConstants.CHANNEL_SLEEPTIME_REMAINING:
                    sendZoneSleepTimeRemainingCommand(zone, command);
                    break;
                default:
                    logger.warn("Unsupported command type '{}'.", channel);
            }
        } catch (CommandTypeNotSupportedException e) {
            logger.warn("Unsupported zone command type received for channel {}.", channel);
        }

    }

    /**
     *
     * @param command
     * @param source
     * @param channel
     */
    public void handleSourceCommand(Command command, int source, String channel) {
    }

    /**
     *
     * @param command
     * @param channel
     */
    public void handleSystemCommand(Command command, String channel) {
        try {
            switch (channel) {
                case RussMcaBindingConstants.CHANNEL_POWER_ALL:
                    sendAllZonesPowerCommand(command);
                    break;

                default:
                    logger.warn("Unsupported command type '{}'.", channel);
            }
        } catch (CommandTypeNotSupportedException e) {
            logger.warn("Unsupported zone command type received for channel {}.", channel);
        }

    }

    public boolean sendZonePowerCommand(int zone, Command command) throws CommandTypeNotSupportedException {
        if (command instanceof OnOffType) {
            return russService.zonePower(zone, OnOffType.ON == command);
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
    }

    public boolean sendZoneLoudnessCommand(int zone, Command command) throws CommandTypeNotSupportedException {
        if (command instanceof OnOffType) {
            return russService.zoneLoudness(zone, OnOffType.ON == command);
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
    }

    public boolean sendAllZonesPowerCommand(Command command) throws CommandTypeNotSupportedException {
        if (command instanceof OnOffType) {
            return russService.allZonesPower(OnOffType.ON == command);
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
    }

    public boolean sendVolumeCommand(int zone, Command command) throws CommandTypeNotSupportedException {
        boolean commandSent = false;
        // The OnOffType for volume is equal to the Mute command
        if (command instanceof OnOffType) {
            commandSent = russService.toggleMute(zone);
        } else {
            if (command == IncreaseDecreaseType.DECREASE) {
                commandSent = russService.zoneVolumeDown(zone);
            } else if (command == IncreaseDecreaseType.INCREASE) {
                commandSent = russService.zoneVolumeUp(zone);
            } else if (command instanceof PercentType) {
                int vol = ((PercentType) command).intValue() / 2;
                commandSent = russService.zoneVolume(zone, vol);
            } else if (command instanceof DecimalType) {
                int vol = ((DecimalType) command).intValue();
                commandSent = russService.zoneVolume(zone, vol);
            } else {
                throw new CommandTypeNotSupportedException("Command type not supported.");
            }
        }
        return commandSent;
    }

    public boolean sendBassCommand(int zone, Command command) throws CommandTypeNotSupportedException {
        boolean commandSent = false;
        if (command == IncreaseDecreaseType.DECREASE) {
            commandSent = russService.zoneBassDown(zone);
        } else if (command == IncreaseDecreaseType.INCREASE) {
            commandSent = russService.zoneBassUp(zone);
        } else if (command instanceof PercentType) {
            int vol = (((PercentType) command).intValue() - 50) / 5;
            commandSent = russService.zoneBass(zone, vol);
        } else if (command instanceof DecimalType) {
            int vol = ((DecimalType) command).intValue();
            commandSent = russService.zoneBass(zone, vol);
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
        return commandSent;
    }

    public boolean sendTrebleCommand(int zone, Command command) throws CommandTypeNotSupportedException {
        boolean commandSent = false;
        if (command == IncreaseDecreaseType.DECREASE) {
            commandSent = russService.zoneTrebleDown(zone);
        } else if (command == IncreaseDecreaseType.INCREASE) {
            commandSent = russService.zoneTrebleUp(zone);
        } else if (command instanceof PercentType) {
            int vol = (((PercentType) command).intValue() - 50) / 5;
            commandSent = russService.zoneTreble(zone, vol);
        } else if (command instanceof DecimalType) {
            int vol = ((DecimalType) command).intValue();
            commandSent = russService.zoneTreble(zone, vol);
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
        return commandSent;
    }

    public boolean sendBalanceCommand(int zone, Command command) throws CommandTypeNotSupportedException {
        boolean commandSent = false;
        if (command == IncreaseDecreaseType.DECREASE) {
            commandSent = russService.zoneBalanceLeft(zone);
        } else if (command == IncreaseDecreaseType.INCREASE) {
            commandSent = russService.zoneBalanceRight(zone);
        } else if (command instanceof PercentType) {
            int vol = (((PercentType) command).intValue() - 50) / 5;
            commandSent = russService.zoneBalance(zone, vol);
        } else if (command instanceof DecimalType) {
            int vol = ((DecimalType) command).intValue();
            commandSent = russService.zoneBalance(zone, vol);
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
        return commandSent;
    }

    public boolean sendTurnOnVolumeCommand(int zone, Command command) throws CommandTypeNotSupportedException {
        boolean commandSent = false;
        if (command == IncreaseDecreaseType.DECREASE) {
            commandSent = russService.zoneTurnOnVolumeDown(zone);
        } else if (command == IncreaseDecreaseType.INCREASE) {
            commandSent = russService.zoneTurnOnVolumeUp(zone);
        } else if (command instanceof PercentType) {
            int vol = ((PercentType) command).intValue() / 2;
            commandSent = russService.zoneTurnOnVolume(zone, vol);
        } else if (command instanceof DecimalType) {
            int vol = ((DecimalType) command).intValue();
            commandSent = russService.zoneTurnOnVolume(zone, vol);
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
        return commandSent;
    }

    public boolean sendZoneDndCommand(int zone, Command command) throws CommandTypeNotSupportedException {
        if (command instanceof StringType) {
            String v = ((StringType) command).toString().toUpperCase();
            return russService.zoneDnd(zone, OnOff.valueOf(v));
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
    }

    public boolean sendZonePartyModeCommand(int zone, Command command) throws CommandTypeNotSupportedException {
        if (command instanceof StringType) {
            String v = ((StringType) command).toString().toUpperCase();
            return russService.zonePartyMode(zone, OnOffMaster.valueOf(v));
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
    }

    public boolean sendZoneSleepTimeRemainingCommand(int zone, Command command)
            throws CommandTypeNotSupportedException {
        boolean commandSent = false;
        if (command instanceof DecimalType) {
            int val = ((DecimalType) command).intValue();
            commandSent = russService.zoneSleepTimeRemaining(zone, val);
        } else {
            throw new CommandTypeNotSupportedException("Command type not supported.");
        }
        return commandSent;
    }

    @Override
    public void russResponseReceived(RussResponseEvent event) {
        RussResponse response = event.getResponse();
        switch (response.getResponseType()) {
            case SYSTEM_STATUS:
                manageSystemStatusUpdate(response);
                break;
            case ZONE_NAME:
                manageZoneUpdateString(response, RussMcaBindingConstants.CHANNEL_NAME);
                break;
            case ZONE_CURRENT_SOURCE:
                manageZoneUpdateNumber(response, RussMcaBindingConstants.CHANNEL_CURRENT_SOURCE);
                break;
            case ZONE_VOLUME:
                manageZoneVolume(response);
                break;
            case ZONE_MUTE:
                manageZoneUpdateSwitch(response, RussMcaBindingConstants.CHANNEL_MUTE);
                break;
            case ZONE_BASS:
                manageZoneBass(response);
                break;
            case ZONE_TREBLE:
                manageZoneTreble(response);
                break;
            case ZONE_BALANCE:
                manageZoneBalance(response);
                break;
            case ZONE_TURN_ON_VOL:
                manageZoneTurnOnVolume(response);
                break;
            case ZONE_LOUDNESS:
                manageZoneUpdateSwitch(response, RussMcaBindingConstants.CHANNEL_LOUDNESS);
                break;
            case ZONE_STATUS:
                manageZoneUpdateSwitch(response, RussMcaBindingConstants.CHANNEL_POWER);
                break;
            case ZONE_DND:
                manageZoneDnd(response);
                break;
            case ZONE_PARTY_MODE:
                manageZonePartyMode(response);
                break;
            case ZONE_SHARED_SOURCE:
                manageZoneUpdateSwitch(response, RussMcaBindingConstants.CHANNEL_SHARED_SOURCE);
                break;
            case ZONE_PAGE:
                manageZoneUpdateSwitch(response, RussMcaBindingConstants.CHANNEL_PAGE);
                break;
            case ZONE_SLEEPTIME_REMAINING:
                manageZoneUpdateNumber(response, RussMcaBindingConstants.CHANNEL_SLEEPTIME_REMAINING);
                break;

            case SOURCE_TYPE:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_TYPE);
                break;
            case SOURCE_NAME:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_NAME);
                break;

            case SOURCE_COMPOSER_NAME:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_COMPOSER_NAME);
                break;
            case SOURCE_CHANNEL_NAME:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_CHANNEL_NAME);
                break;

            case SOURCE_GENRE:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_GENRE);
                break;
            case SOURCE_ARTIST_NAME:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_ARTIST_NAME);
                break;
            case SOURCE_ALBUM_NAME:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_ALBUM_NAME);
                break;
            case SOURCE_PLAYLIST_NAME:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_PLAYLIST_NAME);
                break;
            case SOURCE_SONG_NAME:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_SONG_NAME);
                break;
            case SOURCE_COVERART_URL:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_COVER_ART_URL);
                break;
            case SOURCE_CHANNEL:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_CHANNEL);
                break;
            case SOURCE_PROGRAMSERVICE_NAME:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_PROGRAMM_SERVICE_NAME);
                break;
            case SOURCE_RADIO_TEXT:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_RADIOTEXT);
                break;
            case SOURCE_RADIO_TEXT2:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_RADIOTEXT2);
                break;
            case SOURCE_RADIO_TEXT3:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_RADIOTEXT3);
                break;
            case SOURCE_RADIO_TEXT4:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_RADIOTEXT4);
                break;
            case SOURCE_SHUFFLE_MODE:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_SHUFFLE_MODE);
                break;
            case SOURCE_REPEAT_MODE:
                manageSourceUpdateString(response, RussMcaBindingConstants.CHANNEL_SOURCE_REPEAT_MODE);
                break;
            default:
                logger.debug("Response not processed: {}", response.getResponseType().name());
        }
    }

    /**
     *
     * @param response
     */
    private void manageZoneVolume(RussResponse response) {
        int vol = Integer.valueOf(response.getValue());

        BigDecimal percentage = new BigDecimal(vol * 2);

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_VOLUME_DIMMER), new PercentType(percentage));

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_VOLUME_ABS), new DecimalType(vol));

    }

    /**
     *
     * @param response
     */
    private void manageZoneBass(RussResponse response) {
        int vol = Integer.valueOf(response.getValue());

        BigDecimal percentage = new BigDecimal(vol * 5 + 50);

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_BALANCE_DIMMER), new PercentType(percentage));

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_BASS_ABS), new DecimalType(vol));

    }

    /**
     *
     * @param response
     */
    private void manageZoneTreble(RussResponse response) {
        int vol = Integer.valueOf(response.getValue());

        BigDecimal percentage = new BigDecimal(vol * 5 + 50);

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_TREBLE_DIMMER), new PercentType(percentage));

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_TREBLE_ABS), new DecimalType(vol));

    }

    /**
     *
     * @param response
     */
    private void manageZoneBalance(RussResponse response) {
        int vol = Integer.valueOf(response.getValue());

        BigDecimal percentage = new BigDecimal(vol * 5 + 50);

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_BALANCE_DIMMER), new PercentType(percentage));

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_BALANCE_ABS), new DecimalType(vol));

    }

    /**
     *
     * @param response
     */
    private void manageZoneTurnOnVolume(RussResponse response) {
        int vol = Integer.valueOf(response.getValue());

        BigDecimal percentage = new BigDecimal(vol * 2);

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_TURNON_VALUE_DIMMER), new PercentType(percentage));

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_TURNON_VALUE_ABS), new DecimalType(vol));

    }

    /**
     *
     * @param response
     */
    private void manageZoneDnd(RussResponse response) {
        String value = response.getValue();
        if (value != null) {
            value = value.trim().toUpperCase();
        }
        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_DND), new StringType(value));
    }

    /**
     *
     * @param response
     */
    private void manageZonePartyMode(RussResponse response) {
        String value = response.getValue();
        if (value != null) {
            value = value.trim().toUpperCase();
        }
        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(),
                RussMcaBindingConstants.CHANNEL_PARTY_MODE), new StringType(value));
    }

    /**
     *
     * @param response
     * @param channelID
     */
    private void manageZoneUpdateString(RussResponse response, String channelID) {
        String value = response.getValue();

        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(), channelID),
                new StringType(value));
    }

    /**
     *
     * @param response
     * @param channelID
     */
    private void manageSourceUpdateString(RussResponse response, String channelID) {
        String value = response.getValue();

        updateState(String.format(RussMcaBindingConstants.CHANNEL_SOURCE_FMT, response.getSource(), channelID),
                new StringType(value));
    }

    /**
     *
     * @param response
     * @param channelID
     */
    private void manageZoneUpdateNumber(RussResponse response, String channelID) {
        String value = response.getValue();
        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(), channelID),
                new DecimalType(value));
    }

    /**
     *
     * @param response
     * @param channelID
     */
    private void manageZoneUpdateSwitch(RussResponse response, String channelID) {
        String value = response.getValue();
        updateState(String.format(RussMcaBindingConstants.CHANNEL_ZONE_FMT, response.getLogicalZone(), channelID),
                "ON".equalsIgnoreCase(value) ? OnOffType.ON : OnOffType.OFF);
    }

    /**
     * Called when the Controller is disconnected
     */
    @Override
    public void onDisconnection(RussDisconnectionEvent event) {
        onDisconnection();
    }

    /**
     * Process the Controller disconnection.
     */
    private void onDisconnection() {
        updateStatus(ThingStatus.OFFLINE);
    }

    /**
     * Notify an Controller power state update to OpenHAB
     *
     * @param response
     */
    private void manageSystemStatusUpdate(RussResponse response) {

        OnOffType state = ResponseStateValues.ON.equals(response.getValue()) ? OnOffType.ON : OnOffType.OFF;
        if (OnOffType.ON == state) {
            // setup watch commands
            russService.watchAllZones(true);
        }
    }

}
