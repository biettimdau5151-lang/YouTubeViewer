package com.youtubeviewer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.youtubeviewer.MainActivity;
import com.youtubeviewer.R;

public class BackgroundPlayerService extends Service {
    private static final String CHANNEL_ID = "YouTubeViewerChannel";
    private static final int NOTIFICATION_ID = 1;

    private ExoPlayer player;
    private String currentVideoId;
    private String currentTitle;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if ("PLAY".equals(action)) {
                currentVideoId = intent.getStringExtra("video_id");
                currentTitle = intent.getStringExtra("video_title");
                playVideo();
            } else if ("PAUSE".equals(action)) {
                pausePlayer();
            } else if ("STOP".equals(action)) {
                stopPlayer();
                stopSelf();
                return START_NOT_STICKY;
            }
        }

        return START_STICKY;
    }

    private void playVideo() {
        if (player == null) {
            player = new ExoPlayer.Builder(this).build();
        }

        if (currentVideoId != null && !currentVideoId.isEmpty()) {
            Uri videoUri = Uri.parse("https://www.youtube.com/watch?v=" + currentVideoId);
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();

            showNotification();
        }
    }

    private void pausePlayer() {
        if (player != null) {
            player.pause();
        }
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
        stopForeground(true);
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent pauseIntent = new Intent(this, BackgroundPlayerService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(
                this, 1, pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent stopIntent = new Intent(this, BackgroundPlayerService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 2, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(currentTitle)
                .setContentText("Đang phát video")
                .setSmallIcon(R.drawable.ic_play_circle)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_play_circle, "Tạm dừng", pausePendingIntent)
                .addAction(R.drawable.ic_play_circle, "Dừng", stopPendingIntent)
                .setOngoing(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "YouTube Viewer",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Thông báo phát video nền");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
