package com.youtubeviewer.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YouTubeApiHelper {
    private static final String PREFS_NAME = "YouTubeViewerPrefs";
    private static final String KEY_API_KEY = "api_key";
    private static final String APPLICATION_NAME = "YouTube Viewer";

    private YouTube youtubeService;
    private String apiKey;

    public YouTubeApiHelper(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.apiKey = prefs.getString(KEY_API_KEY, "");
        this.youtubeService = new YouTube.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                request -> {}
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isEmpty();
    }

    public List<com.youtubeviewer.model.Video> searchVideos(String query, int maxResults) throws IOException {
        YouTube.Search.List search = youtubeService.search().list("id,snippet");
        search.setKey(apiKey);
        search.setQ(query);
        search.setType("video");
        search.setMaxResults((long) maxResults);
        search.setVideoEmbeddable("true");

        SearchListResponse response = search.execute();
        List<SearchResult> results = response.getItems();

        List<String> videoIds = new ArrayList<>();
        List<com.youtubeviewer.model.Video> videos = new ArrayList<>();

        if (results != null) {
            for (SearchResult result : results) {
                videoIds.add(result.getId().getVideoId());
                com.youtubeviewer.model.Video video = new com.youtubeviewer.model.Video();
                video.setId(result.getId().getVideoId());
                video.setTitle(result.getSnippet().getTitle());
                video.setDescription(result.getSnippet().getDescription());
                video.setChannelTitle(result.getSnippet().getChannelTitle());
                video.setChannelId(result.getSnippet().getChannelId());
                video.setPublishedAt(result.getSnippet().getPublishedAt().toStringRfc3339());
                video.setThumbnailUrl(result.getSnippet().getThumbnails().getHigh().getUrl());
                videos.add(video);
            }
        }

        if (!videoIds.isEmpty()) {
            String ids = String.join(",", videoIds);
            YouTube.Videos.List videosList = youtubeService.videos().list("statistics,contentDetails");
            videosList.setKey(apiKey);
            videosList.setId(ids);

            VideoListResponse videoResponse = videosList.execute();
            List<com.google.api.services.youtube.model.Video> videoDetails = videoResponse.getItems();

            if (videoDetails != null) {
                for (int i = 0; i < Math.min(videos.size(), videoDetails.size()); i++) {
                    com.google.api.services.youtube.model.Video detail = videoDetails.get(i);
                    videos.get(i).setViewCount(detail.getStatistics().getViewCount().longValue());
                    videos.get(i).setDuration(detail.getContentDetails().getDuration());
                }
            }
        }

        return videos;
    }

    public List<com.youtubeviewer.model.Video> getTrendingVideos(int maxResults) throws IOException {
        YouTube.Videos.List videos = youtubeService.videos().list("snippet,statistics,contentDetails");
        videos.setKey(apiKey);
        videos.setChart("mostPopular");
        videos.setRegionCode("VN");
        videos.setMaxResults((long) maxResults);

        VideoListResponse response = videos.execute();
        List<com.google.api.services.youtube.model.Video> items = response.getItems();
        List<com.youtubeviewer.model.Video> result = new ArrayList<>();

        if (items != null) {
            for (com.google.api.services.youtube.model.Video video : items) {
                com.youtubeviewer.model.Video appVideo = new com.youtubeviewer.model.Video();
                appVideo.setId(video.getId());
                appVideo.setTitle(video.getSnippet().getTitle());
                appVideo.setDescription(video.getSnippet().getDescription());
                appVideo.setChannelTitle(video.getSnippet().getChannelTitle());
                appVideo.setChannelId(video.getSnippet().getChannelId());
                appVideo.setThumbnailUrl(video.getSnippet().getThumbnails().getHigh().getUrl());
                appVideo.setViewCount(video.getStatistics().getViewCount().longValue());
                appVideo.setDuration(video.getContentDetails().getDuration());
                result.add(appVideo);
            }
        }

        return result;
    }

    public List<com.youtubeviewer.model.Video> getRelatedVideos(String videoId, int maxResults) throws IOException {
        YouTube.Search.List search = youtubeService.search().list("id,snippet");
        search.setKey(apiKey);
        search.setRelatedToVideoId(videoId);
        search.setType("video");
        search.setMaxResults((long) maxResults);
        search.setVideoEmbeddable("true");

        SearchListResponse response = search.execute();
        List<SearchResult> results = response.getItems();
        List<com.youtubeviewer.model.Video> videos = new ArrayList<>();

        if (results != null) {
            for (SearchResult result : results) {
                com.youtubeviewer.model.Video video = new com.youtubeviewer.model.Video();
                video.setId(result.getId().getVideoId());
                video.setTitle(result.getSnippet().getTitle());
                video.setChannelTitle(result.getSnippet().getChannelTitle());
                video.setChannelId(result.getSnippet().getChannelId());
                video.setThumbnailUrl(result.getSnippet().getThumbnails().getHigh().getUrl());
                videos.add(video);
            }
        }

        return videos;
    }
}
