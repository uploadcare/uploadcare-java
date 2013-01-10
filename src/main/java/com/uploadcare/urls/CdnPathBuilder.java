package com.uploadcare.urls;

import com.uploadcare.api.File;

import java.awt.*;

public class CdnPathBuilder {

    private final StringBuilder sb = new StringBuilder("/");

    /**
     * Creates a new CDN path builder for some image file.
     *
     * @param file File to be used for the path
     *
     * @see com.uploadcare.api.File#cdnPath()
     */
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

    /**
     * Adds top-left-aligned crop.
     *
     * @param width Crop width
     * @param height Crop height
     */
    public CdnPathBuilder crop(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

    /**
     * Adds center-aligned crop.
     *
     * @param width Crop width
     * @param height Crop height
     */
    public CdnPathBuilder cropCenter(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center");
        return this;
    }

    /**
     * Adds top-left-aligned crop with a filled background.
     *
     * @param width Crop width
     * @param height Crop height
     * @param color Background color
     */
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

    /**
     * Adds center-aligned crop with a filled background.
     *
     * @param width Crop width
     * @param height Crop height
     * @param color Background color
     */
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

    /**
     * Resizes width, keeping the aspect ratio.
     *
     * @param width New width
     */
    public CdnPathBuilder resizeWidth(int width) {
        dimensionGuard(width);
        sb.append("/-/resize/")
                .append(width)
                .append("x");
        return this;
    }

    /**
     * Resizes height, keeping the aspect ratio.
     *
     * @param height New height
     */
    public CdnPathBuilder resizeHeight(int height) {
        dimensionGuard(height);
        sb.append("/-/resize/x")
                .append(height);
        return this;
    }

    /**
     * Resizes width and height
     *
     * @param width New width
     * @param height New height
     */
    public CdnPathBuilder resize(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/resize/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

    /**
     * Scales the image until one of the dimensions fits,
     * then crops the bottom or right side.
     *
     * @param width New width
     * @param height New height
     */
    public CdnPathBuilder scaleCrop(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/scale_crop/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

     /**
     * Scales the image until one of the dimensions fits,
     * centers it, then crops the rest.
     *
     * @param width New width
     * @param height New height
     */
    public CdnPathBuilder scaleCropCenter(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/scale_crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center");
        return this;
    }

    /**
     * Flips the image.
     */
    public CdnPathBuilder flip() {
        sb.append("/-/effect/flip");
        return this;
    }

    /**
     * Adds a grayscale effect.
     */
    public CdnPathBuilder grayscale() {
        sb.append("/-/effect/grayscale");
        return this;
    }

    /**
     * Inverts colors.
     */
    public CdnPathBuilder invert() {
        sb.append("/-/effect/invert");
        return this;
    }

    /**
     * Horizontally mirror image.
     */
    public CdnPathBuilder mirror() {
        sb.append("/-/effect/mirror");
        return this;
    }

    /**
     * Returns the current CDN path as a string.
     *
     * Avoid using directly.
     * Instead, pass the configured builder to a URL factory.
     *
     * @return CDN path
     *
     * @see com.uploadcare.urls.Urls#cdn(CdnPathBuilder)
     */
    public String build() {
        return sb.append("/").toString();
    }

}
