package com.youtubeviewer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.youtubeviewer.R;
import com.youtubeviewer.model.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<Video> videos = new ArrayList<>();
    private OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

    public VideoAdapter(OnVideoClickListener listener) {
        this.listener = listener;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    public void addVideos(List<Video> newVideos) {
        int startPos = videos.size();
        videos.addAll(newVideos);
        notifyItemRangeInserted(startPos, newVideos.size());
    }

    public void clear() {
        videos.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;
        private TextView title;
        private TextView channelName;
        private TextView viewCount;
        private TextView duration;
        private ImageView playButton;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            channelName = itemView.findViewById(R.id.channel_name);
            viewCount = itemView.findViewById(R.id.view_count);
            duration = itemView.findViewById(R.id.duration);
            playButton = itemView.findViewById(R.id.play_button_overlay);
        }

        void bind(Video video) {
            title.setText(video.getTitle());
            channelName.setText(video.getChannelTitle());
            viewCount.setText(video.getFormattedViewCount());
            duration.setText(video.getFormattedDuration());

            Glide.with(itemView.getContext())
                    .load(video.getThumbnailUrl())
                    .centerCrop()
                    .into(thumbnail);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVideoClick(video);
                }
            });
        }
    }
}
