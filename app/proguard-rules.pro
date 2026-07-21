# ProGuard rules for YouTube Viewer

# Keep YouTube API models
-keep class com.google.api.services.youtube.** { *; }
-keep class com.youtubeviewer.model.** { *; }

# Keep Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }

# Keep ExoPlayer
-keep class androidx.media3.** { *; }
