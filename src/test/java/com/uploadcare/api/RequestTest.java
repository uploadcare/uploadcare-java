package com.uploadcare.api;

import com.uploadcare.urls.Urls;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class RequestTest {

    private static final String FILE_ID = "27c7846b-a019-4516-a5e4-de635f822161";
    private static final String FORMATTED_DATE = "Fri, 17 Nov 1989 00:00:00 +0000";
    private Request request;

    @Before
    public void setUp() {
        request = fileGetRequest(FILE_ID);
    }

    private Request fileGetRequest(String fileId) {
        URI url = Urls.apiFile(fileId);
        Client client = Client.demoClient();
        return new Request(client, new HttpGet(url));
    }

    @Test
    public void test_rfc2822() {
        Calendar calendar = new GregorianCalendar(Request.UTC);
        calendar.set(1989, Calendar.NOVEMBER, 17, 0, 0, 0);

        String formattedDate = Request.rfc2822(calendar.getTime());
        assertEquals(formattedDate, formattedDate);
    }

    @Test
    public void test_makeSignature() throws InvalidKeyException, NoSuchAlgorithmException {
        String signature = request.makeSignature(FORMATTED_DATE);
        assertEquals("3daee4a1cd7349bacc3e396b5bfff9e3cfb7648a", signature);
    }

}
