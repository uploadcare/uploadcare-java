package com.uploadcare.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.uploadcare.data.FileData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileTest
{

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    public void enumFails() throws Exception {
        String json = "{ \"color_mode\": \"RGBa\"}";

        Bug bug = mapper.readValue(json, Bug.class);

        Assert.assertTrue("Color mode was not properly converted!", File.ColorMode.RGBa.equals(bug.colorMode));
    }

    @Test
    public void testContentInfoDeserialization() throws Exception {
        String json = "{"
                + "\"uuid\": \"22240276-2f06-41f8-9411-755c8ce926ed\","
                + "\"is_image\": true,"
                + "\"is_ready\": true,"
                + "\"mime_type\": \"image/jpeg\","
                + "\"size\": 642,"
                + "\"content_info\": {"
                + "  \"mime\": {"
                + "    \"mime\": \"image/jpeg\","
                + "    \"type\": \"image\","
                + "    \"subtype\": \"jpeg\""
                + "  },"
                + "  \"image\": {"
                + "    \"format\": \"JPEG\","
                + "    \"width\": 500,"
                + "    \"height\": 500,"
                + "    \"sequence\": false,"
                + "    \"color_mode\": \"RGB\","
                + "    \"orientation\": 6"
                + "  }"
                + "}"
                + "}";

        FileData fileData = mapper.readValue(json, FileData.class);

        assertNotNull("contentInfo should not be null", fileData.contentInfo);
        assertNotNull("contentInfo.mime should not be null", fileData.contentInfo.mime);
        assertEquals("image/jpeg", fileData.contentInfo.mime.mime);
        assertEquals("image", fileData.contentInfo.mime.type);
        assertEquals("jpeg", fileData.contentInfo.mime.subtype);

        assertNotNull("contentInfo.image should not be null", fileData.contentInfo.image);
        assertEquals("JPEG", fileData.contentInfo.image.format);
        assertEquals(500, fileData.contentInfo.image.width);
        assertEquals(500, fileData.contentInfo.image.height);
        assertEquals(File.ColorMode.RGB, fileData.contentInfo.image.colorMode);
    }

    @Test
    public void testVideoContentInfoDeserialization() throws Exception {
        String json = "{"
                + "\"uuid\": \"abc123\","
                + "\"is_image\": false,"
                + "\"is_ready\": true,"
                + "\"mime_type\": \"video/mp4\","
                + "\"size\": 1048576,"
                + "\"content_info\": {"
                + "  \"mime\": {"
                + "    \"mime\": \"video/mp4\","
                + "    \"type\": \"video\","
                + "    \"subtype\": \"mp4\""
                + "  },"
                + "  \"video\": {"
                + "    \"format\": \"MP4\","
                + "    \"duration\": 12000,"
                + "    \"bitrate\": 1500000,"
                + "    \"video\": {"
                + "      \"height\": 1080,"
                + "      \"width\": 1920,"
                + "      \"frame_rate\": 25.0,"
                + "      \"bitrate\": 1400000,"
                + "      \"codec\": \"h264\""
                + "    },"
                + "    \"audio\": {"
                + "      \"bitrate\": 128000,"
                + "      \"codec\": \"aac\","
                + "      \"channels\": \"2\","
                + "      \"sample_rate\": 44100"
                + "    }"
                + "  }"
                + "}"
                + "}";

        FileData fileData = mapper.readValue(json, FileData.class);

        assertNotNull("contentInfo should not be null", fileData.contentInfo);
        assertNotNull("contentInfo.mime should not be null", fileData.contentInfo.mime);
        assertEquals("video/mp4", fileData.contentInfo.mime.mime);
        assertEquals("video", fileData.contentInfo.mime.type);

        assertNotNull("contentInfo.video should not be null", fileData.contentInfo.video);
        assertEquals("MP4", fileData.contentInfo.video.format);
        assertEquals(12000, fileData.contentInfo.video.duration);
        assertNotNull("video.video should not be null", fileData.contentInfo.video.video);
        assertEquals(1920, fileData.contentInfo.video.video.width);
        assertEquals(1080, fileData.contentInfo.video.video.height);
        assertNotNull("video.audio should not be null", fileData.contentInfo.video.audio);
        assertEquals("aac", fileData.contentInfo.video.audio.codec);
    }

    @Test
    public void testMetadataDeserialization() throws Exception {
        String json = "{"
                + "\"uuid\": \"22240276-2f06-41f8-9411-755c8ce926ed\","
                + "\"metadata\": {"
                + "  \"subsystem\": \"uploader\","
                + "  \"pet\": \"cat\""
                + "}"
                + "}";

        FileData fileData = mapper.readValue(json, FileData.class);

        assertNotNull("metadata should not be null", fileData.metadata);
        assertEquals("uploader", fileData.metadata.get("subsystem"));
        assertEquals("cat", fileData.metadata.get("pet"));
    }

    @Test
    public void testAppDataDeserialization() throws Exception {
        String json = "{"
                + "\"uuid\": \"22240276-2f06-41f8-9411-755c8ce926ed\","
                + "\"appdata\": {"
                + "  \"uc_clamav_virus_scan\": {"
                + "    \"data\": {"
                + "      \"infected\": false,"
                + "      \"infected_with\": null"
                + "    },"
                + "    \"version\": \"0.104.2\","
                + "    \"datetime_created\": \"2021-09-21T11:24:33\","
                + "    \"datetime_updated\": \"2021-09-21T11:24:33\""
                + "  }"
                + "}"
                + "}";

        FileData fileData = mapper.readValue(json, FileData.class);

        assertNotNull("appdata should not be null", fileData.appdata);
        assertTrue("appdata should contain uc_clamav_virus_scan", fileData.appdata.containsKey("uc_clamav_virus_scan"));

        File.AppData appData = fileData.appdata.get("uc_clamav_virus_scan");
        
        assertNotNull("appdata entry should not be null", appData);
        assertEquals("0.104.2", appData.version);
        assertNotNull("appdata.data should not be null", appData.data);
    }

    static class Bug {
        public File.ColorMode colorMode;
    }

}
