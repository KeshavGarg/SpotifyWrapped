package com.broyojo.spotifywrappedclone.backend.stats;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Stats implements Serializable {
    private Date date;
    private List<String> topArtists;
    private List<String> topTracks;
    private List<String> recommendations;
    private Set<String> topGenres;
    private int listeningTime;
    private String llmRecommendation;

    public Stats() {
        date = Calendar.getInstance().getTime();
        topArtists = new ArrayList<>();
        topTracks = new ArrayList<>();
        recommendations = new ArrayList<>();
        topGenres = new HashSet<>();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getTopArtists() {
        return topArtists;
    }

    public void setTopArtists(List<String> topArtists) {
        this.topArtists = topArtists;
    }

    public List<String> getTopTracks() {
        return topTracks;
    }

    public void setTopTracks(List<String> topTracks) {
        this.topTracks = topTracks;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public Set<String> getTopGenres() {
        return topGenres;
    }

    public void setTopGenres(Set<String> topGenres) {
        this.topGenres = topGenres;
    }

    public int getListeningTime() {
        return listeningTime;
    }

    public void setListeningTime(int listeningTime) {
        this.listeningTime = listeningTime;
    }

    public String getLlmRecommendation() {
        return llmRecommendation;
    }

    public void setLlmRecommendation(String llmRecommendation) {
        this.llmRecommendation = llmRecommendation;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
