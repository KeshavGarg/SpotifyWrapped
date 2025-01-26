package com.broyojo.spotifywrappedclone.backend.stats;

import android.widget.ImageView;

import com.broyojo.spotifywrappedclone.R;
import com.broyojo.spotifywrappedclone.frontend.StatsActivity;

import java.util.Calendar;

public class HolidayManager {
    private final StatsActivity activity;

    public HolidayManager(StatsActivity activity) {
        this.activity = activity;
    }

    public void enable() {
        ImageView bg = activity.findViewById(R.id.background);
        bg.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int num = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        if (month == 0 && date == 1) {
            bg.setBackgroundResource(R.drawable.january);
        } else if (month == 1 && date == 14) {
            bg.setBackgroundResource(R.drawable.february);
        } else if (month == 2 && date == 17) {
            bg.setBackgroundResource(R.drawable.march);
        } else if (month == 3 && date == 1) {
            bg.setBackgroundResource(R.drawable.april);
        } else if (month == 4 && date == 1) {
            bg.setBackgroundResource(R.drawable.may);
        } else if (month == 5 && date == 1) {
            bg.setBackgroundResource(R.drawable.june);
        } else if (month == 6 && date == 4) {
            bg.setBackgroundResource(R.drawable.july);
        } else if (month == 7 && date == 1) {
            bg.setBackgroundResource(R.drawable.august);
        } else if (month == 8 && date == 1) {
            bg.setBackgroundResource(R.drawable.september);
        } else if (month == 9 && date == 31) {
            bg.setBackgroundResource(R.drawable.october);
        } else if (month == 10 && day == 5 && num == 4) {
            bg.setBackgroundResource(R.drawable.november);
        } else if (month == 11 && day == 25) {
            bg.setBackgroundResource(R.drawable.december);
        }
    }

    public void disable() {
        ImageView bg = activity.findViewById(R.id.background);
        bg.setBackgroundResource(0);
    }
}
