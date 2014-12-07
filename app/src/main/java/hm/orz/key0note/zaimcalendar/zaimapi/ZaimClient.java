package hm.orz.key0note.zaimcalendar.zaimapi;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ZaimClient {

    public interface RequestCallback {
        public void onComplete(String response);
    }

    private class RequsetAsyncTask extends AsyncTask<Void, Void, Void> {

        private HttpGet mHttpGet;
        private String mResponse;
        private RequestCallback mCallback;

        RequsetAsyncTask(HttpGet httpGet, RequestCallback callback) {
            mHttpGet = httpGet;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpClient httpClient = new DefaultHttpClient();

            try {
                mResponse = httpClient.execute(mHttpGet, new ResponseHandler<String>() {
                    @Override
                    public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                        // HttpStatus.SC_OK (HTTP200)
                        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                        }
                        return null;
                    }
                });
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mCallback.onComplete(mResponse);
        }
    }

    private ZaimOAuthClient mZaimOAuthClient;

    ZaimClient(ZaimOAuthClient zaimOAuthClient) {
        mZaimOAuthClient = zaimOAuthClient;
    }

    public void sendRequest(ZaimRequest zaimRequest, RequestCallback callback) {
        HttpGet httpGet = zaimRequest.getHttpGetRequest();
        mZaimOAuthClient.sign(httpGet);

        RequsetAsyncTask asyncTask = new RequsetAsyncTask(httpGet, callback);
        asyncTask.execute();
    }
}
