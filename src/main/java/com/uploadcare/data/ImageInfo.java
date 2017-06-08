package com.uploadcare.data;

import java.util.Date;

/**
 * Created by Egor Petushkov
 * on 09.06.17.
 */
public class ImageInfo {

    public static class GeoLocation {
        public double latitude;
        public double longitude;
    }

    public int width;
    public int height;
    public String format;
    public Integer orientation;
    public int[] dpi;
    public Date datetimeOriginal;
    public GeoLocation geoLocation;

}
