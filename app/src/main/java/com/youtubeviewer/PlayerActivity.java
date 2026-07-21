package com.youtubeviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.youtubeviewer.api.YouTubeApiHelper;
import com.youtubeviewer.model.Video;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerActivity extends AppCompatActivity {
    private PlayerView playerView;
    private ExoPlayer player;
    private TextView videoTitle;
    private TextView videoViews;
    private TextView channelName;
    private TextView videoDescription;
    private TextView subscriberCount;
    private Button btnDownload;
    private Button btnSubscribe;

    private YouTubeApiHelper apiHelper;
    private ExecutorService executorService;
    private String videoId;
    private boolean isDescriptionExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        videoId = getIntent().getStringExtra("video_id");
        String title = getIntent().getStringExtra("video_title");
        String description = getIntent().getStringExtra("video_description");
        String channel = getIntent().getStringExtra("channel_name");
        long views = getIntent().getLongExtra("view_count", 0);

        initViews();
        initPlayer();
        setupUI(title, description, channel, views);
        setupButtons();
    }

    private void initViews() {
        playerView = findViewById(R.id.player_view);
        videoTitle = findViewById(R.id.video_title);
        videoViews = findViewById(R.id.video_views);
        channelName = findViewById(R.id.channel_name);
        videoDescription = findViewById(R.id.video_description);
        subscriberCount = findViewById(R.id.subscriber_count);
        btnDownload = findViewById(R.id.btn_download);
        btnSubscribe = findViewById(R.id.btn_subscribe);
    }

    private void initPlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (videoId != null && !videoId.isEmpty()) {
            Uri videoUri = Uri.parse("https://www.youtube.com/watch?v=" + videoId);
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    }

    private void setupUI(String title, String description, String channel, long views) {
        videoTitle.setText(title);
        channelName.setText(channel);

        Video tempVideo = new Video();
        tempVideo.setViewCount(views);
        videoViews.setText(tempVideo.getFormattedViewCount());

        if (description != null && !description.isEmpty()) {
            videoDescription.setText(description);
        } else {
            videoDescription.setVisibility(View.GONE);
        }

        apiHelper = new YouTubeApiHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        loadChannelInfo();
    }

    private void loadChannelInfo() {
        // Channel info will be loaded from API if needed
        subscriberCount.setText("");
    }

    private void setupButtons() {
        btnDownload.setOnClickListener(v -> {
            downloadVideo();
        });

        btnSubscribe.setOnClickListener(v -> {
            subscribeToChannel();
        });

        TextView btnShowMore = findViewById(R.id.btn_show_more);
        if (btnShowMore != null) {
            btnShowMore.setOnClickListener(v -> {
                isDescriptionExpanded = !isDescriptionExpanded;
                videoDescription.setMaxLines(isDescriptionExpanded ? Integer.MAX_VALUE : 4);
                btnShowMore.setText(isDescriptionExpanded ? "Thu gọn" : "Xem thêm");
            });
        }
    }

    private void downloadVideo() {
        if (videoId == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.youtube.com/watch?v=" + videoId));
        startActivity(intent);
    }

    private void subscribeToChannel() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng ký kênh")
                .setMessage("Tính năng đăng ký sẽ có trong phiên bản tiếp theo!")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
