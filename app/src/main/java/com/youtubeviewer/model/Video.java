package com.youtubeviewer.model;

public class Video {
    private String id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String channelTitle;
    private String channelId;
    private String channelThumbnailUrl;
    private long viewCount;
    private long likeCount;
    private String duration;
    private String publishedAt;

    public Video() {}

    public Video(String id, String title, String thumbnailUrl, String channelTitle, long viewCount) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.channelTitle = channelTitle;
        this.viewCount = viewCount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getChannelTitle() { return channelTitle; }
    public void setChannelTitle(String channelTitle) { this.channelTitle = channelTitle; }

    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getChannelThumbnailUrl() { return channelThumbnailUrl; }
    public void setChannelThumbnailUrl(String channelThumbnailUrl) { this.channelThumbnailUrl = channelThumbnailUrl; }

    public long getViewCount() { return viewCount; }
    public void setViewCount(long viewCount) { this.viewCount = viewCount; }

    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getFormattedViewCount() {
        if (viewCount >= 1_000_000_000) {
            return String.format("%.1f tỷ lượt xem", viewCount / 1_000_000_000.0);
        } else if (viewCount >= 1_000_000) {
            return String.format("%.1f Tr lượt xem", viewCount / 1_000_000.0);
        } else if (viewCount >= 1_000) {
            return String.format("%.1f N lượt xem", viewCount / 1_000.0);
        } else {
            return viewCount + " lượt xem";
        }
    }

    public String getFormattedDuration() {
        if (duration == null || duration.isEmpty()) return "";
        try {
            String[] parts = duration.replace("PT", "").replace("H", ":").replace("M", ":").replace("S", "").split(":");
            if (parts.length == 3) {
                return String.format("%s:%02d:%02d", parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            } else if (parts.length == 2) {
                return String.format("%d:%02d", Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            } else {
                return parts[0] + "s";
            }
        } catch (Exception e) {
            return duration;
        }
    }
}
