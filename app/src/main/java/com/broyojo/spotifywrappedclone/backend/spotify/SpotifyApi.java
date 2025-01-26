package com.broyojo.spotifywrappedclone.backend.spotify;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SpotifyApi {
    public final static String BASE_URL = "https://api.spotify.com/v1";
    private final static OkHttpClient httpClient = new OkHttpClient();
    private final String accessToken;

    public interface SpotifyResponseCallback {
        void receive(JSONObject response) throws JSONException;
    }

    public SpotifyApi(String accessToken) {
        this.accessToken = accessToken;
    }

    public RequestBuilder buildRequest() {
        return new RequestBuilder();
    }

    public class RequestBuilder {
        private HttpUrl.Builder urlBuilder;

        public RequestBuilder route(String route) {
            urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + route)).newBuilder();
            return this;
        }

        public RequestBuilder with(String key, Object value) {
            if (value instanceof Collection) {
                String joined = ((Collection<?>) value).stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","));
                urlBuilder.addQueryParameter(key, joined);
            } else {
                urlBuilder.addQueryParameter(key, value.toString());
            }
            return this;
        }

        public Request build() {
            return new Request.Builder().url(urlBuilder.build()).addHeader("Authorization", "Bearer " + accessToken).build();
        }
    }

    public void executeRequest(Request request, SpotifyResponseCallback callback) {
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("SpotifyApi", "API call failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.body() == null) {
                    Log.e("SpotifyApi", "Response body was null!");
                }

                ResponseBody body = response.body();
                try {
                    if (!response.isSuccessful()) {
                        String error = body.string();
                        Log.e("SpotifyApi", "Response was unsuccessful: " + error);
                        System.out.println(accessToken);
                    } else {
                        callback.receive(new JSONObject(body.string()));
                    }
                } catch (JSONException e) {
                    Log.e("SpotifyApi", "Error during Json Parsing: " + e.getMessage());
                } catch (IOException e) {
                    Log.e("SpotifyApi", "Error reading body: " + e.getMessage());
                }
            }
        });
    }
}
