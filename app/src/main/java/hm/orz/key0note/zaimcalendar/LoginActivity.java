package hm.orz.key0note.zaimcalendar;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class LoginActivity extends ActionBarActivity {

    private static final String CONSUMER_KEY = "ee23cafabb4b76471aa40a773849d72b4fda2aef";
    private static final String CONSUMER_SECRET = "e60b89aa662adffabdf4bafa802c81165df946cf";
    private static final String REQUEST_TOKEN_URL = "https://api.zaim.net/v2/auth/request";
    private static final String AUTHORIZE_URL = "https://auth.zaim.net/users/auth";
    private static final String ACCESS_TOKEN_URL = "https://api.zaim.net/v2/auth/access";
    private static final String CALLBACK = "myapp://callback";

    private OAuthConsumer mConsumer;
    private OAuthProvider mProvider;

    private class OAuthRequestAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void...arg0) {
            //OAuth認証
            try {
                mConsumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
                mProvider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZE_URL);

                String authUrl = mProvider.retrieveRequestToken(mConsumer, CALLBACK);

                // ブラウザに認証ページを開かせる
                LoginActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
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
    }

    private class OAuthAccessAsyncTask extends AsyncTask<Uri, Void, Void> {
        @Override
        protected Void doInBackground(Uri...uris) {
            Uri uri = uris[0];
            final String oauthVerifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
            try {
                // AccessToken取得
                mProvider.retrieveAccessToken(mConsumer, oauthVerifier);
                Log.d(this.getClass().getName(), "ACCESS_TOKEN : " + mConsumer.getToken());
                Log.d(this.getClass().getName(), "ACCESS_TOKEN_SECRET : " + mConsumer.getTokenSecret());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //
        OAuthRequestAsyncTask asyncTask = new OAuthRequestAsyncTask();
        asyncTask.execute();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        // ブラウザ認証からのコールバック
        if (uri != null && uri.toString().startsWith(CALLBACK)) {
            // アクセストークン取得およびリクエスト処理
            OAuthAccessAsyncTask asyncTask = new OAuthAccessAsyncTask();
            asyncTask.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
