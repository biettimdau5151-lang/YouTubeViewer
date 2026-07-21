package com.youtubeviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.youtubeviewer.adapter.VideoAdapter;
import com.youtubeviewer.api.YouTubeApiHelper;
import com.youtubeviewer.model.Video;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements VideoAdapter.OnVideoClickListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SearchView searchView;
    private VideoAdapter adapter;
    private YouTubeApiHelper apiHelper;
    private ExecutorService executorService;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);
        searchView = findViewById(R.id.search_view);

        executorService = Executors.newSingleThreadExecutor();
        apiHelper = new YouTubeApiHelper(this);

        setupRecyclerView();
        setupSearchView();
        setupBottomNav();

        if (!apiHelper.hasApiKey()) {
            showApiKeyDialog();
        } else {
            loadTrendingVideos();
        }
    }

    private void setupRecyclerView() {
        adapter = new VideoAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                searchVideos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadTrendingVideos();
                return true;
            } else if (id == R.id.nav_trending) {
                loadTrendingVideos();
                return true;
            } else if (id == R.id.nav_subscriptions) {
                showComingSoon("Đăng ký");
                return true;
            } else if (id == R.id.nav_library) {
                showComingSoon("Thư viện");
                return true;
            }
            return false;
        });
    }

    private void showApiKeyDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint(R.string.developer_key_hint);
        input.setSingleLine(true);

        new AlertDialog.Builder(this)
                .setTitle("YouTube API Key")
                .setMessage("Nhập API Key từ Google Cloud Console để sử dụng app.\n\nCách lấy:\n1. Vào console.cloud.google.com\n2. Tạo project mới\n3. Bật YouTube Data API v3\n4. Tạo API Key")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String apiKey = input.getText().toString().trim();
                    if (!apiKey.isEmpty()) {
                        apiHelper.setApiKey(apiKey);
                        apiHelper.getApiKey();
                        getSharedPreferences("YouTubeViewerPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("api_key", apiKey)
                                .apply();
                        loadTrendingVideos();
                    }
                })
                .setNegativeButton("Bỏ qua", null)
                .show();
    }

    private void loadTrendingVideos() {
        showLoading(true);
        executorService.execute(() -> {
            try {
                List<Video> videos = apiHelper.getTrendingVideos(20);
                runOnUiThread(() -> {
                    showLoading(false);
                    if (videos.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setVideos(videos);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Không thể tải video: " + e.getMessage());
                });
            }
        });
    }

    private void searchVideos(String query) {
        if (query.isEmpty()) return;
        showLoading(true);
        executorService.execute(() -> {
            try {
                List<Video> videos = apiHelper.searchVideos(query, 20);
                runOnUiThread(() -> {
                    showLoading(false);
                    if (videos.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setVideos(videos);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Lỗi tìm kiếm: " + e.getMessage());
                });
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("Thử lại", (dialog, which) -> {
                    if (!currentQuery.isEmpty()) {
                        searchVideos(currentQuery);
                    } else {
                        loadTrendingVideos();
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showComingSoon(String feature) {
        new AlertDialog.Builder(this)
                .setTitle(feature)
                .setMessage("Tính năng này sẽ có trong phiên bản tiếp theo!")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onVideoClick(Video video) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("video_id", video.getId());
        intent.putExtra("video_title", video.getTitle());
        intent.putExtra("video_description", video.getDescription());
        intent.putExtra("channel_name", video.getChannelTitle());
        intent.putExtra("channel_id", video.getChannelId());
        intent.putExtra("view_count", video.getViewCount());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
