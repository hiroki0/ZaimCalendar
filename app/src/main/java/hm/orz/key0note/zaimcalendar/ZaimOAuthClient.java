package hm.orz.key0note.zaimcalendar;

import android.os.AsyncTask;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class ZaimOAuthClient {
    private static final String CONSUMER_KEY = "ee23cafabb4b76471aa40a773849d72b4fda2aef";
    private static final String CONSUMER_SECRET = "e60b89aa662adffabdf4bafa802c81165df946cf";
    private static final String REQUEST_TOKEN_URL = "https://api.zaim.net/v2/auth/request";
    private static final String AUTHORIZE_URL = "https://auth.zaim.net/users/auth";
    private static final String ACCESS_TOKEN_URL = "https://api.zaim.net/v2/auth/access";

    public interface RequestCallback {
        public void onComplete(String authUrl);
    }

    public interface AccessCallback {
        public void onComplete();
    }

    private class OAuthRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        private RequestCallback mCallback;
        private String mCallbackUrl;
        private String mAuthUrl;

        OAuthRequestAsyncTask(String callbackUrl, RequestCallback callback) {
            mCallbackUrl = callbackUrl;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //OAuth認証
            try {
                mAuthUrl = mProvider.retrieveRequestToken(mConsumer, mCallbackUrl);
            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthNotAuthorizedException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mCallback.onComplete(mAuthUrl);
        }
    }

    private class OAuthAccessAsyncTask extends AsyncTask<Void, Void, Void> {

        private AccessCallback mCallback;
        private String mOAuthVerifier;

        OAuthAccessAsyncTask(String oauthVerifier, AccessCallback callback) {
            mOAuthVerifier = oauthVerifier;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                // AccessToken取得
                mProvider.retrieveAccessToken(mConsumer, mOAuthVerifier);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mCallback.onComplete();
        }
    }

    private OAuthConsumer mConsumer;
    private OAuthProvider mProvider;

    ZaimOAuthClient() {
        mConsumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        mProvider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZE_URL);
    }

    public String getToken() {
        return mConsumer.getToken();
    }

    public String getTokenSecret() {
        return mConsumer.getTokenSecret();
    }

    //public void sign(HttpGet httpGet) {
    //    mConsumer.sign(httpGet);
    //}

    public void request(String callbackUrl, RequestCallback callback) {
        //
        OAuthRequestAsyncTask asyncTask = new OAuthRequestAsyncTask(callbackUrl, callback);
        asyncTask.execute();
    }

    public void access(String oauthVerifier, AccessCallback callback) {

        // アクセストークン取得およびリクエスト処理
        OAuthAccessAsyncTask asyncTask = new OAuthAccessAsyncTask(oauthVerifier, callback);
        asyncTask.execute();
    }

}
