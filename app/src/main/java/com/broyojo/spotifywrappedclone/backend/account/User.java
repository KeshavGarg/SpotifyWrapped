package com.broyojo.spotifywrappedclone.backend.account;

import androidx.annotation.NonNull;

import com.broyojo.spotifywrappedclone.backend.stats.Stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String name;
    private String password;
    private String accessToken;

    // app settings
    private String theme;
    private boolean darkModeEnabled;
    private String timeRange;

    // past stats pages
    private List<Stats> statsHistory;

    public User(String name, String password, String accessToken) {
        this.name = name;
        this.password = password;
        this.accessToken = accessToken;

        this.theme = "Standard";
        this.darkModeEnabled = false;
        this.timeRange = "long_term";

        this.statsHistory = new ArrayList<>();
    }

    public List<Stats> getStatsHistory() {
        return statsHistory;
    }

    public void setStatsHistory(List<Stats> statsHistory) {
        this.statsHistory = statsHistory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }

        User other = (User) o;

        return this.name.equals(other.name);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isDarkModeEnabled() {
        return darkModeEnabled;
    }

    public void setDarkModeEnabled(boolean darkModeEnabled) {
        this.darkModeEnabled = darkModeEnabled;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", theme='" + theme + '\'' +
                ", darkModeEnabled=" + darkModeEnabled +
                ", timeRange='" + timeRange + '\'' +
                ", statsHistory=" + statsHistory +
                '}';
    }
}
