package com.uploadcare.urls;

import com.uploadcare.api.File;

import java.awt.*;

public class CdnPathBuilder {

    public enum ImageFormat {
        FORMAT_JPEG {
            public String toString() {
                return "jpeg";
            }
        },

        FORMAT_PNG {
            public String toString() {
                return "png";
            }
        },

        FORMAT_WEBP {
            public String toString() {
                return "webp";
            }
        },

        FORMAT_AUTO {
            public String toString() {
                return "auto";
            }
        }
    }

    public enum ImageQuality {
        QUALITY_NORMAL {
            public String toString() {
                return "normal";
            }
        },

        QUALITY_BETTER {
            public String toString() {
                return "better";
            }
        },
        QUALITY_BEST {
            public String toString() {
                return "best";
            }
        },

        QUALITY_LIGHTER {
            public String toString() {
                return "lighter";
            }
        },
        QUALITY_LIGHTEST {
            public String toString() {
                return "lightest";
            }
        },

        QUALITY_SMART {
            public String toString() {
                return "smart";
            }
        },
        QUALITY_SMART_RETINA {
            public String toString() {
                return "smart_retina";
            }
        }
    }

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
        if (dim < 1 || dim > 2048) {
            throw new IllegalArgumentException("Dimensions must be in the range 1-2048");
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
        sb.append("/-/flip");
        return this;
    }

    /**
     * Adds a grayscale effect.
     */
    public CdnPathBuilder grayscale() {
        sb.append("/-/grayscale");
        return this;
    }

    /**
     * Inverts colors.
     */
    public CdnPathBuilder invert() {
        sb.append("/-/invert");
        return this;
    }

    /**
     * Horizontally mirror image.
     */
    public CdnPathBuilder mirror() {
        sb.append("/-/mirror");
        return this;
    }

    /**
     * Performs Gaussian blur on result image.
     */
    public CdnPathBuilder blur() {
        sb.append("/-/blur");
        return this;
    }

    /**
     * Performs Gaussian blur on result image.
     *
     * @param strength Strength is standard deviation (aka blur radius) multiplied by ten. Strength
     *                 can be up to 5000. Default is 10.
     */
    public CdnPathBuilder blur(int strength) {
        if (strength < 0 || strength > 5000) {
            strength = 10;
        }
        sb.append("/-/blur/")
                .append(strength);
        return this;
    }

    /**
     * Performs sharpening on result image. This can be useful after scaling down.
     */
    public CdnPathBuilder sharp() {
        sb.append("/-/sharp");
        return this;
    }

    /**
     * Performs sharpening on result image. This can be useful after scaling down.
     *
     * @param strength Strength can be from 0 to 20. Default is 5.
     */
    public CdnPathBuilder sharp(int strength) {
        if (strength < 0 || strength > 20) {
            strength = 5;
        }
        sb.append("/-/sharp/")
                .append(strength);
        return this;
    }

    /**
     * Reduces an image proportionally in order to fit it into given dimensions.
     *
     * @param width New width
     * @param height New height
     */
    public CdnPathBuilder preview(int width, int height) {
        dimensionsGuard(width, height);
        sb.append("/-/preview/")
                .append(width)
                .append("x")
                .append(height);
        return this;
    }

    /**
     * Turn an image to one of the following formats:
     *   FORMAT_JPEG
     *   FORMAT_PNG
     *   FORMAT_WEBP
     *   FORMAT_AUTO
     *
     * @param format {@link ImageFormat}.
     */
    public CdnPathBuilder format(ImageFormat format) {
        sb.append("/-/format/")
                .append(format.toString());
        return this;
    }

    /**
     * Image quality affects size of image and loading speed. Has no effect on non-JPEG images, but does not force format to JPEG.
     *
     * @param quality {@link ImageQuality}
     * QUALITY_NORMAL – used by default. Fine in most cases.
     * QUALITY_BETTER – can be used on relatively small previews with lots of details. ≈125% file size compared to normal image.
     * QUALITY_BEST – useful if you're a photography god and you want to get perfect quality without paying attention to size. ≈170% file size.
     * QUALITY_LIGHTER – can be used on relatively large images to save traffic without significant quality loss. ≈80% file size.
     * QUALITY_LIGHTEST — useful for retina resolutions, when you don't wory about quality of each pixel. ≈50% file size.
     * QUALITY_SMART – adjusts compression and format automatically to preserve visual quality while minimizing the file size.
     * QUALITY_SMART_RETINA — similar to smart, yet optimized for high pixel ratios.
     */
    public CdnPathBuilder quality(ImageQuality quality) {
        sb.append("/-/quality/")
                .append(quality.toString());
        return this;
    }

    /**
     * Runs faces detection on the image
     *
     */
    public CdnPathBuilder detectFaces() {
        this.sb.append("/detect_faces");
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
