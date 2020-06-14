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

public class RequestHelperTest {

    private static final String FILE_ID = "27c7846b-a019-4516-a5e4-de635f822161";
    private static final String FORMATTED_DATE = "Fri, 17 Nov 1989 00:00:00 +0000";
    private RequestHelper requestHelper;

    @Before
    public void setUp() {
        Client client = Client.demoClient();
        requestHelper = new RequestHelper(client);
    }

    @Test
    public void test_rfc2822() {
        Calendar calendar = new GregorianCalendar(RequestHelper.UTC);
        calendar.set(1989, Calendar.NOVEMBER, 17, 0, 0, 0);

        String formattedDate = RequestHelper.rfc2822(calendar.getTime());
        assertEquals(formattedDate, formattedDate);
    }

    @Test
    public void test_makeSignature() throws InvalidKeyException, NoSuchAlgorithmException {
        URI url = Urls.apiFile(FILE_ID);
        String signature = requestHelper.makeSignature(new HttpGet(url), FORMATTED_DATE, null);
        assertEquals("535e263808dd38599343f04aab3c9f34bb15573c", signature);
    }

}
