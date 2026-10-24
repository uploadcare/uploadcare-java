package com.uploadcare.urls;

import com.uploadcare.api.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CdnPathBuilderTest {

    private static final String FILE_ID = "27c7846b-a019-4516-a5e4-de635f822161";

    @Mock
    private File file;

    private CdnPathBuilder builder;

    @BeforeEach
    public void setUp() {
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
        assertThrows(IllegalArgumentException.class, () -> builder.resizeWidth(0));
        assertThrows(IllegalArgumentException.class, () -> builder.resizeWidth(2049));
    }

    @Test
    public void test_dimensionsGuard() {
        builder.resize(1024, 634);
        builder.resize(634, 1024);
        assertThrows(IllegalArgumentException.class, () -> builder.resize(1024, 635));
        assertThrows(IllegalArgumentException.class, () -> builder.resize(635, 1024));
    }

}
