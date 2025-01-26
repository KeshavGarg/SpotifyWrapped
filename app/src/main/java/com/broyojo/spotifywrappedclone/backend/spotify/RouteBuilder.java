package com.broyojo.spotifywrappedclone.backend.spotify;

import java.util.Objects;

import okhttp3.HttpUrl;

public class RouteBuilder {
    private final HttpUrl.Builder urlBuilder;

    public RouteBuilder(String route) {
        urlBuilder = Objects.requireNonNull(HttpUrl.parse(SpotifyApi.BASE_URL + route)).newBuilder();
    }

    public RouteBuilder with(String key, Object value) {
        urlBuilder.addQueryParameter(key, value.toString());
        return this;
    }

    public HttpUrl build() {
        return urlBuilder.build();
    }
}
