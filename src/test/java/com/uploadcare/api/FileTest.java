package com.uploadcare.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.Assert;
import org.junit.Test;

public class FileTest
{

    @Test
    public void enumFails() throws Exception {
        String json = "{ \"color_mode\": \"RGBa\"}";

        // duplicate the way the mapper is configured in uploadcare
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Bug bug = mapper.readValue(json, Bug.class);

        Assert.assertTrue("Color mode was not properly converted!", File.ColorMode.RGBa.equals(bug.colorMode));
    }

    static class Bug {
        public File.ColorMode colorMode;
    }

}
