package com.uploadcare.urls;

import com.uploadcare.api.File;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CdnPathBuilderTest {

    private static final String FILE_ID = "27c7846b-a019-4516-a5e4-de635f822161";
    private CdnPathBuilder builder;

    @Before
    public void setUp() {
        File file = mock(File.class);
        when(file.getFileId()).thenReturn(FILE_ID);

        builder = new CdnPathBuilder(file);
    }

    @Test
    public void test_fileUrl() {
        String path = builder.build();
        assertEquals("/" + FILE_ID + "/", path);
    }

    @Test
    public void test_allOperations() {
        String path = builder
                .crop(100, 110)
                .cropColor(120, 130, Color.BLACK)
                .cropCenter(140, 150)
                .cropCenterColor(160, 170, Color.RED)
                .resize(100, 110)
                .resizeWidth(120)
                .resizeHeight(130)
                .scaleCrop(100, 110)
                .scaleCropCenter(120, 130)
                .flip()
                .grayscale()
                .invert()
                .mirror()
                .blur()
                .blur(5500)
                .sharp()
                .sharp(25)
                .preview(100,150)
                .format(CdnPathBuilder.ImageFormat.FORMAT_JPEG)
                .quality(CdnPathBuilder.ImageQuality.QUALITY_BEST)
                .build();
        assertEquals("/" + FILE_ID +
                "/-/crop/100x110" +
                "/-/crop/120x130/000000" +
                "/-/crop/140x150/center" +
                "/-/crop/160x170/center/ff0000" +
                "/-/resize/100x110" +
                "/-/resize/120x" +
                "/-/resize/x130" +
                "/-/scale_crop/100x110" +
                "/-/scale_crop/120x130/center" +
                "/-/flip" +
                "/-/grayscale" +
                "/-/invert" +
                "/-/mirror" +
                "/-/blur" +
                "/-/blur/10" +
                "/-/sharp" +
                "/-/sharp/5" +
                "/-/preview/100x150" +
                "/-/format/jpeg" +
                "/-/quality/best" +
                "/",
                path
        );
    }

    @Test
    public void test_detectFaces() {
        String path = builder.detectFaces().build();
        assertEquals("/" + FILE_ID + "/detect_faces/", path);
    }

    @Test
    public void test_dimensionGuard() {
        builder.resizeWidth(1);
        builder.resizeWidth(2048);
        try {
            builder.resizeWidth(0);
        } catch (IllegalArgumentException e1) {
            try {
                builder.resizeWidth(2049);
            } catch (IllegalArgumentException e2) {
                return;
            }
        }
        fail();
    }

    @Test
    public void test_dimensionsGuard() {
        builder.resize(1024, 634);
        builder.resize(634, 1024);
        try {
            builder.resize(1024, 635);
        } catch (IllegalArgumentException e1) {
            try {
                builder.resize(635, 1024);
            } catch (IllegalArgumentException e2) {
                return;
            }
        }
        fail();
    }

}
