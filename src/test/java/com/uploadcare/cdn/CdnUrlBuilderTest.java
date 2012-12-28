package com.uploadcare.cdn;

import com.uploadcare.api.File;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CdnUrlBuilderTest {

    private static final String fileId = "27c7846b-a019-4516-a5e4-de635f822161";
    private CdnUrlBuilder builder;

    @Before
    public void setUp() {
        File file = mock(File.class);
        when(file.getFileId()).thenReturn(fileId);

        builder = new CdnUrlBuilder(file);
    }

    @Test
    public void test_fileUrl() {
        CdnUrl url = builder.build();
        assertEquals("/" + fileId + "/", url.getRawPath());
    }

    @Test
    public void test_allOperations() {
        CdnUrl url = builder
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
                .build();
        assertEquals("/" + fileId +
                "/-/crop/100x110" +
                "/-/crop/120x130/000000" +
                "/-/crop/140x150/center" +
                "/-/crop/160x170/center/ff0000" +
                "/-/resize/100x110" +
                "/-/resize/120x" +
                "/-/resize/x130" +
                "/-/scale_crop/100x110" +
                "/-/scale_crop/120x130/center" +
                "/-/effect/flip" +
                "/-/effect/grayscale" +
                "/-/effect/invert" +
                "/-/effect/mirror" +
                "/",
                url.getRawPath()
        );
    }

}
