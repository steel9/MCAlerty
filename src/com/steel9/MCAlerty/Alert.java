package com.steel9.MCAlerty;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.*;

public class Alert implements ConfigurationSerializable {
    private String mMessage = "";
    private ArrayList<UUID> mPlayersRead;

    /**
     * A container for alerts.
     * @param message The message of the Alert.
     */
    public Alert(String message) {
        setMessage(message);
        mPlayersRead = new ArrayList<UUID>();
    }

    public Alert(Map<String, Object> data) {
        mMessage= (String)data.get("message");

        mPlayersRead = new ArrayList<>();
        //List<UUID> playersRead = (List<UUID>)data.get("playersRead");
        List<String> playersRead = (List<String>)data.get("playersRead");
        if (playersRead != null) {
            //mPlayersRead.addAll(playersRead);
            for (String uuid : playersRead) {
                mPlayersRead.add(UUID.fromString(uuid));
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("message", mMessage);

        //data.put("playersRead", mPlayersRead);
        ArrayList<String> playersRead = new ArrayList<>();
        for (UUID uuid : mPlayersRead) {
            playersRead.add(uuid.toString());
        }
        data.put("playersRead", playersRead);

        return data;
    }


    /**
     * Gets the message of the Alert.
     * @return Returns the retrieved message.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Sets the message of the Alert.
     * @param message The new message.
     */
    public void setMessage(String message) {
        mMessage = message;
    }

    /**
     * Checks if the Alert has been read previously, by a specific player.
     * @param playerID The unique identifier of the player to check.
     * @return True if the Alert has been read already by the player, otherwise False.
     */
    public boolean shownFor(UUID playerID) {
        return mPlayersRead.contains(playerID);
    }

    /**
     * Marks the Alert as read for the specified player.
     * @param playerID The player to mark the Alert as read for.
     */
    public void setRead(UUID playerID) {
        setRead(playerID, true);
    }

    private void setRead(UUID playerID, boolean read) {
        if (read) {
            if (!mPlayersRead.contains(playerID)) {
                mPlayersRead.add(playerID);
            }
        }
        else {
            if (mPlayersRead.contains(playerID)) {
                mPlayersRead.remove(playerID);
            }
        }
    }
}
