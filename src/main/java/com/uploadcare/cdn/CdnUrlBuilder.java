package com.uploadcare.cdn;

import com.uploadcare.api.File;

import java.awt.*;

public class CdnUrlBuilder {

    public static final String CDN_ROOT = "https://ucarecdn.com/";
    private final StringBuilder sb = new StringBuilder(CDN_ROOT);

    public CdnUrlBuilder(File file) {
        sb.append(file.getFileId());
    }

    private String colorToHex(Color color) {
        String rgb = Integer.toHexString(color.getRGB());
        return rgb.substring(2);
    }

    public CdnUrlBuilder crop(int width, int height) {
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

    public CdnUrlBuilder cropCenter(int width, int height) {
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center");
        return this;
    }

    public CdnUrlBuilder cropColor(int width, int height, Color color) {
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/")
                .append(colorToHex(color));
        return this;
    }

    public CdnUrlBuilder cropCenterColor(int width, int height, Color color) {
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center/")
                .append(colorToHex(color));
        return this;
    }

    public CdnUrlBuilder resizeWidth(int width) {
        sb.append("/-/resize/")
                .append(width)
                .append("x");
        return this;
    }

    public CdnUrlBuilder resizeHeight(int height) {
        sb.append("/-/resize/x")
                .append(height);
        return this;
    }

    public CdnUrlBuilder resize(int width, int height) {
        sb.append("/-/resize/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

    public CdnUrlBuilder scaleCrop(int width, int height) {
        sb.append("/-/scale_crop/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

    public CdnUrlBuilder scaleCropCenter(int width, int height) {
        sb.append("/-/scale_crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center");
        return this;
    }

    public CdnUrlBuilder flip() {
        sb.append("/-/effect/flip");
        return this;
    }

    public CdnUrlBuilder grayscale() {
        sb.append("/-/effect/grayscale");
        return this;
    }

    public CdnUrlBuilder invert() {
        sb.append("/-/effect/invert");
        return this;
    }

    public CdnUrlBuilder mirror() {
        sb.append("/-/effect/mirror");
        return this;
    }

    public CdnUrl build() {
        String path = sb.append("/").toString();
        return new CdnUrl(path);
    }

}
