package hm.orz.key0note.zaimcalendar.zaim;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;

public class ZaimRequest {

    private String mUrlString;
    private ArrayList<NameValuePair> mParams;

    ZaimRequest(String urlString) {
        mUrlString = urlString;
        mParams = new ArrayList<NameValuePair>();
    }

    public void addParam(String key, String value) {
        mParams.add(new BasicNameValuePair(key, value));
    }

    public HttpGet getHttpGetRequest() {
        if (mParams.isEmpty()) {
            return new HttpGet(mUrlString);
        }
        else {
            String query = URLEncodedUtils.format(mParams, HTTP.UTF_8);
            return new HttpGet(mUrlString + "?" + query);
        }
    }
}
