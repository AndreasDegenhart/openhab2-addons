package de.o1tec.binding.russmca.service;

import de.o1tec.binding.russmca.protocol.RussConnection;
import de.o1tec.binding.russmca.service.event.RussResponseListener;

public interface RussMcaService {

    public enum OnOff {
        ON("ON"),
        OFF("OFF");

        private final String value;

        private OnOff(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public enum OnOffMaster {
        ON("ON"),
        OFF("OFF"),
        MASTER("MASTER");

        private final String value;

        private OnOffMaster(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum OnOffSlave {
        ON("ON"),
        OFF("OFF"),
        SLAVE("SLAVE");

        private final String value;

        private OnOffSlave(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public void initializeService(String receiverHost, Integer ipControlPort, Integer numControllers);

    public void stopService();

    public RussConnection getConnection();

    public String getConnectionName();

    public void addResponseListener(RussResponseListener listener);

    //
    // get commands
    //
    public boolean querySystemStatus();

    public boolean querySystemLanguage();

    public boolean queryControllerIpAddress(int controller);

    public boolean queryControllerMacAdress(int controller);

    public boolean queryControllerType(int controller);

    public boolean queryZoneName(int zone);

    public boolean queryZoneCurrentSource(int zone);

    public boolean queryZoneVolume(int zone);

    public boolean queryZoneBass(int zone);

    public boolean queryZoneTreble(int zone);

    public boolean queryZoneBalance(int zone);

    public boolean queryZoneLoudness(int zone);

    public boolean queryZoneTurnOnVolume(int zone);

    public boolean queryZoneDnd(int zone);

    public boolean queryZonePartyMode(int zone);

    public boolean queryZonePower(int zone);

    public boolean queryZoneMute(int zone);

    public boolean queryZoneSharedSource(int zone);

    public boolean queryZoneLastError(int zone);

    public boolean queryZonePage(int zone);

    public boolean queryZoneSleepTimeRemaining(int zone);

    public boolean queryFavoriteValid(int zone, int favorite);

    public boolean queryFavoritename(int zone, int favorite);

    public boolean queryZoneAvailable(int zone);

    public boolean queryZoneSourceAvailable(int zone, int source);

    public boolean querySourceName(int source);

    public boolean querySourceType(int source);

    public boolean querySourceComposerName(int source);

    public boolean querySourceIpAddress(int source);

    public boolean querySourceChannel(int source);

    public boolean querySourcecoverArtURL(int source);

    public boolean querySourceChannelName(int source);

    public boolean querySourceGenre(int source);

    public boolean querySourceArtistName(int source);

    public boolean querySourceAlbumName(int source);

    public boolean querySourcePlaylistName(int source);

    public boolean querySourceSongName(int source);

    public boolean querySourceProgramServiceName(int source);

    public boolean querySourceRadioText(int source);

    public boolean querySourceRadioText2(int source);

    public boolean querySourceRadioText3(int source);

    public boolean querySourceRadioText4(int source);

    public boolean querySourceShuffleMode(int source);

    public boolean querySourceRepeatMode(int source);

    public boolean querySourceMode(int source);

    public boolean querySourceSupportMMLongList(int source);

    public boolean querySourceSupportRating(int source);

    public boolean querySourceBankName(int source, int bank);

    public boolean querySourcePresetName(int source, int bank, int preset);

    public boolean querySourcePresetValid(int source, int bank, int preset);

    //
    // watch commands
    //

    public boolean watchZone(int zone, boolean on);

    public boolean watchAllZones(boolean on);

    //
    // event commands
    //
    public boolean zoneSource(int zone, int source);

    public boolean zonePower(int zone, boolean on);

    public boolean allZonesPower(boolean on);

    public boolean toggleMute(int zone);

    public boolean toggleShuffle(int zone);

    public boolean toggleRepeat(int zone);

    public boolean zoneVolumeUp(int zone);

    public boolean zoneVolumeDown(int zone);

    public boolean zoneVolume(int zone, int volume);

    public boolean zonePartyMode(int zone, OnOffMaster mode);

    public boolean zoneDnd(int zone, OnOff mode);

    //
    // set commands
    //
    public boolean zoneBass(int zone, int value);

    public boolean zoneBassUp(int zone);

    public boolean zoneBassDown(int zone);

    public boolean zoneTreble(int zone, int value);

    public boolean zoneTrebleUp(int zone);

    public boolean zoneTrebleDown(int zone);

    public boolean zoneBalance(int zone, int value);

    public boolean zoneBalanceLeft(int zone);

    public boolean zoneBalanceRight(int zone);

    public boolean zoneTurnOnVolume(int zone, int value);

    public boolean zoneTurnOnVolumeUp(int zone);

    public boolean zoneTurnOnVolumeDown(int zone);

    public boolean zoneSleepTimeRemaining(int zone, int minutes);

    public boolean zoneLoudness(int zone, boolean on);

    public boolean sourceBankName(int source, String name);
}
