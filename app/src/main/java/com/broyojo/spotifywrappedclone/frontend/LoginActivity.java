package com.broyojo.spotifywrappedclone.frontend;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.broyojo.spotifywrappedclone.R;
import com.broyojo.spotifywrappedclone.backend.account.User;
import com.broyojo.spotifywrappedclone.backend.account.UserDatabase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "094e486018d24c44a12ec9046d74a618";
    private static final String REDIRECT_URI = "spotify-sdk://auth";
    private static final int AUTH_TOKEN_REQUEST_CODE = 0;
    private static final String[] SCOPES = {
            "ugc-image-upload",
            "user-read-playback-state",
            "user-modify-playback-state",
            "user-read-currently-playing",
            "app-remote-control",
            "streaming",
            "playlist-read-private",
            "playlist-read-collaborative",
            "playlist-modify-private",
            "playlist-modify-public",
            "user-follow-modify",
            "user-follow-read",
            "user-read-playback-position",
            "user-top-read",
            "user-read-recently-played",
            "user-library-modify",
            "user-library-read",
            "user-read-email",
            "user-read-private",
    };
    private UserDatabase userDatabase;
    private User user;
    private String accessToken;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("lastLogin", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        userDatabase = new UserDatabase(this.getApplicationContext());
        setContentView(R.layout.landing_page);

        if (isLoggedOut()) {
            sharedPreferences.edit().remove("username").apply();
            System.out.println("LOGGED OUT");
            AuthorizationClient.clearCookies(getApplicationContext());
        }

        String lastLoginUsername = sharedPreferences.getString("username", null);
        System.out.println("LAST LOGIN: " + lastLoginUsername);
        if (lastLoginUsername != null) {
            if (!userDatabase.containsUsername(lastLoginUsername)) {
                sharedPreferencesEditor.remove("username");
                sharedPreferencesEditor.apply();
            }

            user = userDatabase.getUserByName(lastLoginUsername);
            spotifyLogin();
        }

        findViewById(R.id.get_started_button).setOnClickListener(v -> {
            dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.login);

            EditText username = dialog.findViewById(R.id.username);
            EditText password = dialog.findViewById(R.id.password);
            Button loginButton = dialog.findViewById(R.id.login_button);
            Button createAccountButton = dialog.findViewById(R.id.create_account_button);

            dialog.show();

            loginButton.setOnClickListener(v1 -> {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                user = userDatabase.getUserByName(usernameText);
                if (user == null || !user.getPassword().equals(passwordText)) {
                    alert("No user found with that username and password");
                    return;
                }

                dialog.dismiss();
                spotifyLogin();
            });

            createAccountButton.setOnClickListener(v1 -> {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                String error = isValidUsername(usernameText);
                if (error != null) {
                    alert(error);
                    return;
                }

                error = isValidPassword(passwordText);
                if (error != null) {
                    alert(error);
                    return;
                }

                if (userDatabase.getUserByName(usernameText) != null) {
                    alert("User already exists with that username");
                    return;
                }

                user = new User(usernameText, passwordText, null);
                userDatabase.addUser(user);

                dialog.dismiss();
                spotifyLogin();
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (requestCode == AUTH_TOKEN_REQUEST_CODE && response.getType() == AuthorizationResponse.Type.TOKEN) {
            accessToken = response.getAccessToken();
            System.out.println("ACCESS TOKEN: " + accessToken);
            user.setAccessToken(accessToken);
            userDatabase.save();
            sharedPreferencesEditor.putString("username", user.getName());
            sharedPreferencesEditor.apply();
            goToStats();
        }
    }

    private void spotifyLogin() {
        AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, Uri.parse(REDIRECT_URI).toString())
                .setShowDialog(false)
                .setScopes(SCOPES)
                .setCampaign("your-campaign-token") // is this necessary?
                .build();
    }

    private void alert(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private String isValidUsername(String username) {
        if (username.isEmpty()) {
            return "Username cannot be empty";
        }

        return null;
    }

    private String isValidPassword(String password) {
        if (password.isEmpty()) {
            return "Password cannot be empty";
        }

        return null;
    }

    private boolean isLoggedOut() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return false;
        }
        return Objects.requireNonNull(extras).getBoolean("isLoggedOut");
    }

    private void goToStats() {
        Intent intent = new Intent(this, StatsActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
