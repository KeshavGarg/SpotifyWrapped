package com.broyojo.spotifywrappedclone.backend.stats;

import android.util.ArraySet;
import android.widget.TextView;

import com.broyojo.spotifywrappedclone.R;
import com.broyojo.spotifywrappedclone.backend.account.User;
import com.broyojo.spotifywrappedclone.backend.account.UserDatabase;
import com.broyojo.spotifywrappedclone.backend.spotify.SpotifyApi;
import com.broyojo.spotifywrappedclone.frontend.StatsActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Request;

public class StatsManager {
    private final User user;
    private final UserDatabase database;
    private final SpotifyApi api;
    private final LLM llm;
    private final StatsActivity activity;

    public StatsManager(StatsActivity activity, UserDatabase database, User user) {
        this.user = user;
        this.database = database;
        this.api = new SpotifyApi(user.getAccessToken());
        this.llm = new LLM("You are an agent which gives recommendations for what users should wear look given the Spotify song genres they listen to. You only output a description and do not mention yourself at all. Do not say 'certainly' or 'sure' at the beginning of your response as the user does not see the message history, only your message");
        this.activity = activity;
    }

    public void calculate() {
        Stats stats = new Stats();
        user.getStatsHistory().add(stats);

        getTopArtists(artists -> activity.runOnUiThread(() -> {
            TextView textView = activity.findViewById(R.id.top_artists_content);
            if (artists.isEmpty()) {
                textView.setText("None (You are uncultured)");
            } else {
                stats.setTopArtists(artists);
                textView.setText(collectionToString(artists));
            }
        }));

        getTopTracks(tracks -> activity.runOnUiThread(() -> {
            TextView textView = activity.findViewById(R.id.top_tracks_content);
            if (tracks.isEmpty()) {
                textView.setText("None (You are uncultured)");
            } else {
                stats.setTopTracks(tracks);
                textView.setText(collectionToString(tracks));
            }
        }));

        getRecommendations(recommendations -> activity.runOnUiThread(() -> {
            TextView textView = activity.findViewById(R.id.artists_rec_content);
            if (recommendations.isEmpty()) {
                textView.setText("None (You are uncultured)");
            } else {
                stats.setRecommendations(recommendations);
                textView.setText(collectionToString(recommendations));
            }
        }));

        getTopGenres(genres -> activity.runOnUiThread(() -> {
            TextView textView = activity.findViewById(R.id.top_genres_content);
            if (genres.isEmpty()) {
                textView.setText("None (You are uncultured)");
            } else {
                stats.setTopGenres(genres);
                textView.setText(collectionToString(genres));
            }
        }));

        getListeningTime(time -> activity.runOnUiThread(() -> {
            TextView textView = activity.findViewById(R.id.total_time_spent_content);
            stats.setListeningTime(time);
            textView.setText(formatMilliseconds(time) + " (last 50 tracks)");
        }));

        getLLMRecommendation(recommendation -> activity.runOnUiThread(() -> {
            TextView textView = activity.findViewById(R.id.LLM_description_content);
            stats.setLlmRecommendation(recommendation);
            textView.setText(recommendation);
        }));
    }

    public void setStats(Stats stats) {
        activity.runOnUiThread(() -> {
            TextView textView = activity.findViewById(R.id.top_artists_content);
            textView.setText(collectionToString(stats.getTopArtists()));

            TextView textView2 = activity.findViewById(R.id.top_tracks_content);
            textView2.setText(collectionToString(stats.getTopTracks()));

            TextView textView3 = activity.findViewById(R.id.artists_rec_content);
            textView3.setText(collectionToString(stats.getRecommendations()));

            TextView textView4 = activity.findViewById(R.id.top_genres_content);
            textView4.setText(collectionToString(stats.getTopGenres()));

            TextView textView5 = activity.findViewById(R.id.total_time_spent_content);
            textView5.setText(formatMilliseconds(stats.getListeningTime()));

            TextView textView6 = activity.findViewById(R.id.LLM_description_content);
            textView6.setText(stats.getLlmRecommendation());
        });
    }

    public interface StatsCallback<T> {
        void receive(T response);
    }

    private void getTopArtists(StatsCallback<List<String>> callback) {
        Request request = api.buildRequest()
                .route("/me/top/artists")
                .with("time_range", user.getTimeRange())
                .with("limit", 3)
                .build();
        api.executeRequest(request, response -> {
            JSONArray items = response.getJSONArray("items");
            List<String> names = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                names.add(items.getJSONObject(i).getString("name"));
            }

            // just for demo purposes:
            names.add("Taylor Swift");
            names.add("Ariana Grande");
            names.add("Ed Sheeran");
            names.add("Drake");

            callback.receive(names);
        });
    }

    private void getTopTracks(StatsCallback<List<String>> callback) {
        Request request = api.buildRequest()
                .route("/me/top/tracks")
                .with("time_range", user.getTimeRange())
                .with("limit", 3)
                .build();
        api.executeRequest(request, response -> {
            JSONArray items = response.getJSONArray("items");
            List<String> names = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                names.add(items.getJSONObject(i).getString("name"));
            }
            callback.receive(names);
        });
    }

    private void getRecommendations(StatsCallback<List<String>> callback) {
        Request genreSeedRequest = api.buildRequest()
                .route("/recommendations/available-genre-seeds")
                .build();

        api.executeRequest(genreSeedRequest, response -> {
            JSONArray gs = response.getJSONArray("genres");
            List<String> genres = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                genres.add(gs.getString(i));
            }

            Request recommendationsRequest = api.buildRequest()
                    .route("/recommendations")
                    .with("seed_genres", genres)
                    .build();

            api.executeRequest(recommendationsRequest, response2 -> {
                JSONArray tracks = response2.getJSONArray("tracks");
                Set<String> names = new ArraySet<>();
                for (int i = 0; i < tracks.length(); i++) {
                    JSONArray artists = tracks.getJSONObject(i).getJSONArray("artists");
                    for (int j = 0; j < artists.length(); j++) {
                        names.add(artists.getJSONObject(j).getString("name"));
                    }
                }
                callback.receive(new ArrayList<>(names));
            });
        });
    }

    private void getTopGenres(StatsCallback<Set<String>> callback) {
        Request topArtistsRequest = api.buildRequest()
                .route("/me/top/artists")
                .with("time_range", user.getTimeRange())
                .with("limit", 5)
                .build();

        api.executeRequest(topArtistsRequest, response -> {
            JSONArray items = response.getJSONArray("items");
            Set<String> genres = new HashSet<>();
            for (int i = 0; i < items.length(); i++) {
                JSONArray gs = items.getJSONObject(i).getJSONArray("genres");
                for (int j = 0; j < gs.length(); j++) {
                    genres.add(gs.getString(j));
                }
            }

            // just for demo purposes:
            genres.add("Hip-hop");
            genres.add("EDM");
            genres.add("Rock");
            genres.add("Indie");
            genres.add("Pop");

            callback.receive(genres);
        });
    }

    private void getListeningTime(StatsCallback<Integer> callback) {
        Request request = api.buildRequest()
                .route("/me/player/recently-played")
                .with("limit", 50)
//                .with("after", start)
//                .with("before", end)
                .build();

        api.executeRequest(request, response -> {
            JSONArray items = response.getJSONArray("items");
            int totalMs = 0;
            for (int i = 0; i < items.length(); i++) {
                totalMs += items.getJSONObject(i).getJSONObject("track").getInt("duration_ms");
            }
            final int time = totalMs;
            callback.receive(time);
        });
    }

    private void getLLMRecommendation(StatsCallback<String> callback) {
        getTopGenres(genres -> {
            if (!genres.isEmpty()) {
                llm.getResponse(
                        String.format("Can you dynamically describe how someone who listens to my kind of music tends to act/think/dress? I listen to music with genres of: %s", collectionToString(genres)),
                        callback::receive);
            } else {
                callback.receive("You are uncultured and listen to no genres, I cannot make a recommendation for you. Come back when you've listened to some music.");
            }
        });
    }

    private static <T> String collectionToString(Collection<T> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int i = 1;
        for (T element : list) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(i + ". " + element.toString());
            i++;
        }
        return builder.toString();
    }

    private static String formatMilliseconds(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (seconds > 0 || (hours == 0 && minutes == 0)) {
            sb.append(seconds).append("s");
        }

        return sb.toString().trim();
    }
}
