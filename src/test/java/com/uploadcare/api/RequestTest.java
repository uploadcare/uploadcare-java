package com.uploadcare.api;

import com.google.api.client.http.HttpMethods;
import org.junit.Before;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class RequestTest {

    private String formattedDate;
    private Client client;

    @Before
    public void setUp() {
        client = Client.demoClient();

        TimeZone utc = TimeZone.getTimeZone("UTC");
        Calendar calendar = new GregorianCalendar(utc);
        calendar.set(1989, Calendar.NOVEMBER, 17, 0, 0, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Request.DATE_FORMAT);
        dateFormat.setTimeZone(utc);
        formattedDate = dateFormat.format(calendar.getTime());
    }

    @Test
    public void test_makeSignature() throws InvalidKeyException, NoSuchAlgorithmException {
        assertEquals("Fri, 17 Nov 1989 00:00:00 +0000", formattedDate);

        Url url = Url.filesUrl("27c7846b-a019-4516-a5e4-de635f822161");
        Request request = new Request(client, HttpMethods.GET, url);
        String signature = request.makeSignature(formattedDate);

        assertEquals("3daee4a1cd7349bacc3e396b5bfff9e3cfb7648a", signature);
    }

}
