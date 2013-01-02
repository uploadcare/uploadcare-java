package com.uploadcare.urls;

import com.uploadcare.api.File;

import java.awt.*;

public class CdnPathBuilder {

    private final StringBuilder sb = new StringBuilder("/");

    public CdnPathBuilder(File file) {
        sb.append(file.getFileId());
    }

    private void dimensionGuard(int dim) {
        if (dim < 1 || dim > 1024) {
            throw new IllegalArgumentException("Dimensions must be in the range 1-1024");
        }
    }

    private void dimensionsGuard(int width, int height) {
        dimensionGuard(width);
        dimensionGuard(height);
        if (width > 634 && height > 634) {
            throw new IllegalArgumentException("At least one dimension must be less than 634");
        }
    }

    private String colorToHex(Color color) {
        String rgb = Integer.toHexString(color.getRGB());
        return rgb.substring(2);
    }

    public CdnPathBuilder crop(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

    public CdnPathBuilder cropCenter(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center");
        return this;
    }

    public CdnPathBuilder cropColor(int width, int height, Color color) {
        dimensionsGuard(width, height);
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/")
                .append(colorToHex(color));
        return this;
    }

    public CdnPathBuilder cropCenterColor(int width, int height, Color color) {
        dimensionsGuard(width, height);
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center/")
                .append(colorToHex(color));
        return this;
    }

    public CdnPathBuilder resizeWidth(int width) {
        dimensionGuard(width);
        sb.append("/-/resize/")
                .append(width)
                .append("x");
        return this;
    }

    public CdnPathBuilder resizeHeight(int height) {
        dimensionGuard(height);
        sb.append("/-/resize/x")
                .append(height);
        return this;
    }

    public CdnPathBuilder resize(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/resize/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

    public CdnPathBuilder scaleCrop(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/scale_crop/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

    public CdnPathBuilder scaleCropCenter(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/scale_crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center");
        return this;
    }

    public CdnPathBuilder flip() {
        sb.append("/-/effect/flip");
        return this;
    }

    public CdnPathBuilder grayscale() {
        sb.append("/-/effect/grayscale");
        return this;
    }

    public CdnPathBuilder invert() {
        sb.append("/-/effect/invert");
        return this;
    }

    public CdnPathBuilder mirror() {
        sb.append("/-/effect/mirror");
        return this;
    }

    public String build() {
        return sb.append("/").toString();
    }

}
