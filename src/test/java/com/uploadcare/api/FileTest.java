package com.uploadcare.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileTest
{

    @Test
    void enumFails() throws Exception {
        String json = "{ \"color_mode\": \"RGBa\"}";

        // duplicate the way the mapper is configured in uploadcare
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Bug bug = mapper.readValue(json, Bug.class);

        assertEquals(File.ColorMode.RGBa, bug.colorMode, "Color mode was not properly converted!");
    }

    static class Bug {
        public File.ColorMode colorMode;
    }

}
