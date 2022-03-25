package com.uploadcare.api;

import com.uploadcare.data.FileData;
import com.uploadcare.urls.CdnPathBuilder;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The main Uploadcare resource, represents a user-uploaded file.
 */
public class File {

    private final Client client;
    private final FileData fileData;

    File(Client client, FileData fileData) {
        this.client = client;
        this.fileData = fileData;
    }

    public String getFileId() {
        return fileData.uuid;
    }

    public boolean isStored() {
        return fileData.datetimeStored != null;
    }

    public String getMimeType() {
        return fileData.mimeType;
    }

    public boolean hasOriginalFileUrl() {
        return fileData.originalFileUrl != null;
    }

    public URI getOriginalFileUrl() {
        return fileData.originalFileUrl;
    }

    public String getOriginalFilename() {
        return fileData.originalFilename;
    }

    public boolean isRemoved() {
        return fileData.datetimeRemoved != null;
    }

    public Date getRemoved() {
        return fileData.datetimeRemoved;
    }

    public long getSize() {
        return fileData.size;
    }

    public Date getUploadDate() {
        return fileData.datetimeUploaded;
    }

    public Date getStoredDate() {
        return fileData.datetimeStored;
    }

    public Date getRemovedDate() {
        return fileData.datetimeRemoved;
    }

    public String getSource() {
        return fileData.source;
    }

    public boolean isReady() {
        return fileData.isReady;
    }

    public boolean isImage() {
        return fileData.isImage;
    }

    public ImageInfo getImageInfo() {
        return fileData.imageInfo;
    }

    public VideoInfo getVideoInfo() {
        return fileData.videoInfo;
    }

    public Map<String, Float> getRekognitionInfo() {
        return fileData.rekognitionInfo;
    }

    public Map<String, String> getVariations() {
        return fileData.variations;
    }

    /**
     * Returns the unique REST URL for this resource.
     *
     * @return REST URL
     */
    public URI getUrl() {
        return fileData.url;
    }

    /**
     * Refreshes file data from Uploadcare.
     *
     * This does not mutate the current {@code File} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    public File update() {
        return client.getFile(fileData.uuid);
    }

    /**
     * Deletes this file from Uploadcare.
     *
     * This does not mutate the current {@code File} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    public File delete() {
        client.deleteFile(fileData.uuid);
        return update();
    }

    /**
     * Saves this file on Uploadcare (marks it to be kept).
     *
     * This does not mutate the current {@code File} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    public File save() {
        client.saveFile(fileData.uuid);
        return update();
    }

    /**
     * Creates a CDN path builder for this file.
     *
     * @return CDN path builder
     *
     * @see com.uploadcare.urls.Urls#cdn(CdnPathBuilder)
     */
    public CdnPathBuilder cdnPath() {
        return new CdnPathBuilder(this);
    }

    @Override
    public String toString() {
        return "File{" +
                "fileData=" + fileData +
                '}';
    }

    public static class ImageInfo {
        public String format;
        public int height;
        public int width;
        public int orientation;
        public boolean sequence;
        public ColorMode colorMode;
        public GeoLocation geoLocation;
        public List<Float> dpi;
        public Date datetimeOriginal;

        @Override
        public String toString() {
            return "ImageInfo{" +
                    "format='" + format + '\'' +
                    ", height=" + height +
                    ", width=" + width +
                    ", orientation=" + orientation +
                    ", sequence=" + sequence +
                    ", colorMode=" + colorMode +
                    ", geoLocation=" + geoLocation +
                    ", dpi=" + dpi +
                    ", datetimeOriginal=" + datetimeOriginal +
                    '}';
        }
    }

    public static class VideoInfo {
        public String format;
        public int duration;
        public int bitrate;
        public Audio audio;
        public Video video;

        @Override
        public String toString() {
            return "VideoInfo{" +
                    "format='" + format + '\'' +
                    ", duration=" + duration +
                    ", bitrate=" + bitrate +
                    ", audio=" + audio +
                    ", video=" + video +
                    '}';
        }
    }

    public static class GeoLocation {
        public float latitude;
        public float longitude;

        @Override
        public String toString() {
            return "GeoLocation{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    public static class Audio {
        public int bitrate;
        public String codec;
        public String channels;
        public int sampleRate;

        @Override
        public String toString() {
            return "Audio{" +
                    "bitrate=" + bitrate +
                    ", codec='" + codec + '\'' +
                    ", channels='" + channels + '\'' +
                    ", sampleRate=" + sampleRate +
                    '}';
        }
    }

    public static class Video {
        public int bitrate;
        public String codec;
        public int height;
        public int width;
        public float frameRate;

        @Override
        public String toString() {
            return "Video{" +
                    "bitrate=" + bitrate +
                    ", codec='" + codec + '\'' +
                    ", height=" + height +
                    ", width=" + width +
                    ", frameRate=" + frameRate +
                    '}';
        }
    }

    public enum ColorMode {
        RGB, RGBA, RGBa, RGBX, L, LA, La, P, PA, CMYK, YCbCr, HSV, LAB
    }
}
