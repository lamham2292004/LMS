package com.app.lms.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeUtils {

    // Regex hỗ trợ các dạng URL YouTube phổ biến
    private static final String YOUTUBE_REGEX = "(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/)([a-zA-Z0-9_-]{11})";

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(YOUTUBE_REGEX);

    /**
     * Extract YouTube Video ID từ URL.
     * Hỗ trợ các dạng:
     * - https://www.youtube.com/watch?v=VIDEO_ID
     * - https://youtu.be/VIDEO_ID
     * - https://www.youtube.com/embed/VIDEO_ID
     *
     * @param url YouTube URL
     * @return Video ID hoặc null nếu không hợp lệ
     */
    public static String extractVideoId(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        Matcher matcher = YOUTUBE_PATTERN.matcher(url.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Tạo embed URL từ video ID.
     *
     * @param videoId YouTube Video ID
     * @return Embed URL dạng https://www.youtube.com/embed/VIDEO_ID
     */
    public static String buildEmbedUrl(String videoId) {
        if (videoId == null || videoId.isBlank()) {
            return null;
        }
        return "https://www.youtube.com/embed/" + videoId;
    }

    /**
     * Kiểm tra URL có phải YouTube URL hợp lệ không.
     *
     * @param url URL cần kiểm tra
     * @return true nếu là YouTube URL hợp lệ
     */
    public static boolean isValidYoutubeUrl(String url) {
        return extractVideoId(url) != null;
    }

    /**
     * Tạo embed URL trực tiếp từ YouTube URL.
     *
     * @param youtubeUrl YouTube URL gốc
     * @return Embed URL hoặc null nếu URL không hợp lệ
     */
    public static String toEmbedUrl(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);
        return buildEmbedUrl(videoId);
    }
}
