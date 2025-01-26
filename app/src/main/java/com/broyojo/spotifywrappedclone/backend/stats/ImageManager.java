package com.broyojo.spotifywrappedclone.backend.stats;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RelativeLayout;

import com.broyojo.spotifywrappedclone.R;
import com.broyojo.spotifywrappedclone.frontend.StatsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;

public class ImageManager {
    private final StatsActivity activity;

    public ImageManager(StatsActivity activity) {
        this.activity = activity;

        FloatingActionButton ssButton = activity.findViewById(R.id.ss_button);
        ssButton.setOnClickListener(v -> {
            Bitmap bitmap = createBitmap();
            saveImage(bitmap);
        });
    }

    public Bitmap createBitmap() {
        activity.alert("Creating screenshot...");

        RelativeLayout relativeLayout = activity.findViewById(R.id.relativeLayout);

        relativeLayout.measure(
                View.MeasureSpec.makeMeasureSpec(relativeLayout.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        relativeLayout.layout(0, 0, relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        relativeLayout.draw(canvas);

        return bitmap;
    }

    public void saveImage(Bitmap bitmap) {
        String date = DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "MySpotifyWrapped");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "My Spotify Wrapped " + date);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + activity.getString(R.string.app_name));
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (OutputStream out = activity.getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
            activity.alert("Failed to save image.");
        } finally {
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            activity.getContentResolver().update(uri, values, null, null);
        }
    }
}