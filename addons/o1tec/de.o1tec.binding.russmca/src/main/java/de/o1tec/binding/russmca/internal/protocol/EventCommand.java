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
public class EventCommand extends AbstractBaseCommand {

    /**
     * List of the GET commands.
     *
     * @author Andreas Degenhart
     *
     */
    public enum EventCommandType implements RussCommand.CommandType {

        SELECT_PHYSICAL_SOURCE("C[%d].Z[%d]!SelectSource %d", 3),
        ALL_ZONES_ON("C[1].Z[1]!AllOn", 0),
        ALL_ZONES_OFF("C[1].Z[1]!AllOff", 0),
        ZONE_ON("C[%d].Z[%d]!ZoneOn", 2),
        ZONE_OFF("C[%d].Z[%d]!ZoneOff", 2),
        KEY_PRESS("C[%d].Z[%d]!KeyPress %s", 3),

        VOLUME("C[%d].Z[%d]!KeyPress Volume %d", 3),
        VOLUME_UP("C[%d].Z[%d]!KeyPress VolumeUp", 2),
        VOLUME_DOWN("C[%d].Z[%d]!KeyPress VolumeDown", 2),

        MUTE("C[%d].Z[%d]!KeyRelease Mute", 2),

        KEY_RELEASE("C[%d].Z[%d]!KeyRelease %s", 3),
        KEY_HOLD("C[%d].Z[%d]!KeyHold %s", 3),
        KEY_UEI_CODE("C[%d].Z[%d]!KeyCode %d", 3),
        PARTY_MODE("C[%d].Z[%d]!PartyMode %s", 3),
        PARTY_MODE_OFF("C[%d].Z[%d]!PartyMode off", 2),
        PARTY_MODE_ON("C[%d].Z[%d]!PartyMode on", 2),
        PARTY_MODE_MASTER("C[%d].Z[%d]!PartyMode master", 2),
        DND("C[%d].Z[%d]!DoNotDisturb %s", 3),
        DND_OFF("C[%d].Z[%d]!DoNotDisturb off", 2),
        DND_ON("C[%d].Z[%d]!DoNotDisturb on", 2),
        SHUFFLE_MODE("C[%d].Z[%d]!Shuffle", 2),
        REPEAT_MODE("C[%d].Z[%d]!Repeat", 2),

        SAVE_ZONE_FAV("C[%d].Z[%d]!saveZoneFavorite \"%s\" %d", 4),
        SAVE_SYSTEM_FAV("C[%d].Z[%d]!saveSystemFavorite \"%s\" %d", 4),
        RESTORE_ZONE_FAV("C[%d].Z[%d]!restoreZoneFavorite %d", 3),
        RESTORE_SYSTEM_FAV("C[%d].Z[%d]!restoreSystemFavorite %d", 3),
        DELETE_ZONE_FAV("C[%d].Z[%d]!deleteZoneFavorite %d", 3),
        DELETE_SYSTEM_FAV("C[%d].Z[%d]!deleteSystemFavorite %d", 3),

        SAVE_PRESET_WITH_NAME("C[%d].Z[%d]!savePreset \"%s\" %d", 4),
        SAVE_PRESET("C[%d].Z[%d]!savePreset %d", 3),
        RESTORE_PRESET("C[%d].Z[%d]!restorePreset %d", 3),
        DELETE_PRESET("C[%d].Z[%d]!deletePreset %d", 3);

        private String command;
        private int parameterCount;

        private EventCommandType(String command, int parameterCount) {
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

    private static final String COMMAND_PREFIX = "EVENT ";

    protected EventCommand(EventCommandType command) {
        super(command, command.getParameterCount());
    }

    @Override
    protected String commandPrefix() {
        return COMMAND_PREFIX;
    }

}
