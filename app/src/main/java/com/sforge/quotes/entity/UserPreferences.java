package com.sforge.quotes.entity;

import android.graphics.drawable.Drawable;

/**
 * User Preferences entity
 * Carries Information about The Current User's Preferences
 */
public class UserPreferences {

    /**
     * Background Preference
     */
    private String bgId;
    private String bgQuality;

    public UserPreferences() {

    }

    /**
     * Constructor of UserPreferences
     * @param backgroundId background preference
     */
    public UserPreferences(String backgroundId, String backgroundQuality){
        this.bgId = backgroundId;
        this.bgQuality = backgroundQuality;
    }

    public String getBgId() {
        return bgId;
    }
    public String getBgQuality() {
        return bgQuality;
    }
}
