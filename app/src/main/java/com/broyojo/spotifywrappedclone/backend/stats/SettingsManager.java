package com.broyojo.spotifywrappedclone.backend.stats;

import android.app.AlertDialog;
import android.app.Dialog;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.broyojo.spotifywrappedclone.R;
import com.broyojo.spotifywrappedclone.backend.account.User;
import com.broyojo.spotifywrappedclone.backend.account.UserDatabase;
import com.broyojo.spotifywrappedclone.frontend.StatsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Collections;
import java.util.List;

public class SettingsManager {
    private final StatsActivity activity;

    private User user;
    private UserDatabase userDatabase;
    private HolidayManager holidayManager;

    private FloatingActionButton settingsButton;
    private Dialog settingsDialog;
    private static boolean firstLaunch = true;

    private Stats currentStats;

    public SettingsManager(StatsActivity activity, UserDatabase userDatabase, User user) {
        this.activity = activity;
        this.userDatabase = userDatabase;
        this.user = user;
        this.holidayManager = new HolidayManager(activity);

        System.out.println("STATS HISTORY: " + user.getStatsHistory());

        setupUI();
    }

    private void setupUI() {
        setupLogoutButton();

        if (firstLaunch) {
            firstLaunch = false;
            if (user.isDarkModeEnabled()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        if (user.getTheme().equals("Holiday")) {
            holidayManager.enable();
        } else {
            holidayManager.disable();
        }

        settingsButton = (FloatingActionButton) activity.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this::buildSettings);
    }

    public void buildSettings(View view1) {
        Dialog dialog = new Dialog(view1.getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.settings);

        settingsDialog = dialog;

        TextView usernameTextView = dialog.findViewById(R.id.current_username);
        usernameTextView.setText("Username: " + user.getName());

        setupTimeRangeSpinner(dialog);
        setupThemeSpinner(dialog);
        setupDarkModeSwitch(dialog);
        setupDeleteAccountButton(dialog);
        setupAccountUpdateButtons(dialog);
        setupPrevStatsSpinner(dialog);

        dialog.show();

        Button updateSettingsButton = (Button) dialog.findViewById(R.id.update_settings_button);
        updateSettingsButton.setOnClickListener(v -> {
            updateTimeRange(dialog);
            updateThemeSpinner(dialog);
            updateDarkModeSwitch(dialog);
            updatePrevStatsSpinner(dialog);

            activity.getStats().calculate();
            userDatabase.save();

            dialog.dismiss();
        });
    }

    private void setupThemeSpinner(Dialog dialog) {
        Spinner themeSpinner = (Spinner) dialog.findViewById(R.id.select_theme_spinner);
        switch (user.getTheme()) {
            case "Standard":
                themeSpinner.setSelection(0);
                break;
            case "Holiday":
                themeSpinner.setSelection(1);
                break;
            default:
                activity.alert("Invalid theme: " + user.getTheme());
        }
    }

    private void updateThemeSpinner(Dialog dialog) {
        Spinner themeSpinner = (Spinner) dialog.findViewById(R.id.select_theme_spinner);
        String spinnerTheme = themeSpinner.getSelectedItem().toString();
        switch (spinnerTheme) {
            case "Holiday Theme":
                user.setTheme("Holiday");
                holidayManager.enable();
                break;
            case "Standard Mode":
                user.setTheme("Standard");
                holidayManager.disable();
                break;
            default:
                activity.alert("Invalid theme: " + spinnerTheme);
        }
    }

    private void setupTimeRangeSpinner(Dialog dialog) {
        Spinner timeRangeSpinner = (Spinner) dialog.findViewById(R.id.select_timeline_spinner);

        switch (user.getTimeRange()) {
            case "short_term":
                timeRangeSpinner.setSelection(0);
                break;
            case "medium_term":
                timeRangeSpinner.setSelection(1);
                break;
            case "long_term":
                timeRangeSpinner.setSelection(2);
                break;
            default:
                activity.alert("Invalid time range: " + user.getTimeRange());
        }
    }

    private void updateTimeRange(Dialog dialog) {
        Spinner timeRangeSpinner = (Spinner) dialog.findViewById(R.id.select_timeline_spinner);
        String spinnerTimeRange = timeRangeSpinner.getSelectedItem().toString();

        switch (spinnerTimeRange) {
            case "Past 4 Weeks":
                user.setTimeRange("short_term");
                break;
            case "Past 6 Months":
                user.setTimeRange("medium_term");
                break;
            case "Past Year":
                user.setTimeRange("long_term");
                break;
            default:
                activity.alert("Invalid time range: " + spinnerTimeRange);
        }
    }

    private void setupDarkModeSwitch(Dialog dialog) {
        SwitchMaterial darkModeSwitch = (SwitchMaterial) dialog.findViewById(R.id.dark_mode_switch);
        darkModeSwitch.setChecked(user.isDarkModeEnabled());

        darkModeSwitch.setOnCheckedChangeListener((v, isChecked) -> {
            user.setDarkModeEnabled(isChecked);
            userDatabase.save();
        });
    }

    private void updateDarkModeSwitch(Dialog dialog) {
        AppCompatDelegate.setDefaultNightMode(user.isDarkModeEnabled() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void setupLogoutButton() {
        FloatingActionButton logoutButton = (FloatingActionButton) activity.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            firstLaunch = true;
            userDatabase.save();
            activity.logout();
        });
    }

    private void setupDeleteAccountButton(Dialog dialog) {
        Button deleteAccountButton = dialog.findViewById(R.id.delete_account_button);
        deleteAccountButton.setOnClickListener(v -> {
            new AlertDialog.Builder(dialog.getContext())
                    .setTitle("Confirm Account Deletion")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialogInterface, i) -> deleteAccount())
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
        });
    }

    private void deleteAccount() {
        userDatabase.removeUser(user);
        firstLaunch = true;
        activity.logout();
    }

    private void showUpdateNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Update Username");

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString();
            if (!newName.isEmpty()) {
                if (userDatabase.getUserByName(newName) == null) {
                    user.setName(newName);
                    userDatabase.save();
                    settingsDialog.dismiss();
                    settingsButton.performClick();
                    dialog.dismiss();
                } else {
                    activity.alert("Name already taken");
                }
            } else {
                activity.alert("Username cannot be empty");
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showUpdatePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Change Password");

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String newPassword = input.getText().toString();
            if (!newPassword.isEmpty()) {
                user.setPassword(newPassword);
                userDatabase.save();
                dialog.dismiss();
            } else {
                activity.alert("Password cannot be empty");
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void setupAccountUpdateButtons(Dialog dialog) {
        Button updateNameButton = dialog.findViewById(R.id.update_name_button);
        updateNameButton.setOnClickListener(v -> showUpdateNameDialog());

        Button changePasswordButton = dialog.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(v -> showUpdatePasswordDialog());
    }

    private void setupPrevStatsSpinner(Dialog dialog) {
        Spinner prevStatsSpinner = (Spinner) dialog.findViewById(R.id.prev_stats_spinner);
        List<Stats> statsHistory = user.getStatsHistory();

        Collections.sort(statsHistory, (s1, s2) -> s2.getDate().compareTo(s1.getDate()));

        ArrayAdapter<Stats> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item,
                statsHistory);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prevStatsSpinner.setAdapter(adapter);

        if (currentStats != null) {
            int position = findStatsPosition(statsHistory, currentStats);
            if (position >= 0) {
                prevStatsSpinner.setSelection(position);
            }
        }

        prevStatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Stats selectedStats = (Stats) parent.getItemAtPosition(position);
                currentStats = selectedStats;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private int findStatsPosition(List<Stats> statsList, Stats targetStats) {
        for (int i = 0; i < statsList.size(); i++) {
            if (statsList.get(i).getDate().equals(targetStats.getDate())) {
                return i;
            }
        }
        return -1;
    }

    private void updatePrevStatsSpinner(Dialog dialog) {
        activity.getStats().setStats(currentStats);
    }
}
