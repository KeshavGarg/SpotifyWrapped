package com.broyojo.spotifywrappedclone.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.broyojo.spotifywrappedclone.R;
import com.broyojo.spotifywrappedclone.backend.account.User;
import com.broyojo.spotifywrappedclone.backend.account.UserDatabase;
import com.broyojo.spotifywrappedclone.backend.stats.ImageManager;
import com.broyojo.spotifywrappedclone.backend.stats.SettingsManager;
import com.broyojo.spotifywrappedclone.backend.stats.StatsManager;

import java.util.Objects;

public class StatsActivity extends AppCompatActivity {
    private User user;
    private SettingsManager settingsManager;
    private StatsManager stats;
    private ImageManager image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

        UserDatabase database = new UserDatabase(this.getApplicationContext());
        Bundle extras = getIntent().getExtras();
        User passedUser = (User) Objects.requireNonNull(extras).getSerializable("user");
        assert passedUser != null;
        user = database.getUserByName(passedUser.getName());

        settingsManager = new SettingsManager(this, database, user);
        stats = new StatsManager(this, database, user);
        image = new ImageManager(this);

        stats.calculate();
    }

    public void alert(String message) {
        Log.d("StatsActivity", message);
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isLoggedOut", true);
        startActivity(intent);
    }

    public StatsManager getStats() {
        return stats;
    }
}