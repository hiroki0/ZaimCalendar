package hm.orz.key0note.zaimcalendar;


import android.test.AndroidTestCase;

import org.apache.http.client.methods.HttpGet;

import hm.orz.key0note.zaimcalendar.zaim.ZaimRequest;

public class ZaimRequestTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEmptyParam() {
        final String TEST_URL = "http://hogehoge";
        ZaimRequest request = new ZaimRequest(TEST_URL);
        HttpGet httpGet = request.getHttpGetRequest();

        assertEquals(TEST_URL, httpGet.getURI().toString());
    }

    public void testTwoParams() {
        final String TEST_URL = "http://hogehoge";
        final String PARAM1_KEY = "one";
        final String PARAM1_VALUE = "1";
        final String PARAM2_KEY = "two";
        final String PARAM2_VALUE = "2";

        ZaimRequest request = new ZaimRequest(TEST_URL);
        request.addParam(PARAM1_KEY, PARAM1_VALUE);
        request.addParam(PARAM2_KEY, PARAM2_VALUE);
        HttpGet httpGet = request.getHttpGetRequest();

        assertEquals(
                TEST_URL + "?" + PARAM1_KEY + "=" + PARAM1_VALUE + "&" + PARAM2_KEY + "=" + PARAM2_VALUE,
                httpGet.getURI().toString());
    }
}
