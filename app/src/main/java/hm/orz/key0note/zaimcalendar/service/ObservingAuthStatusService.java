package hm.orz.key0note.zaimcalendar.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import hm.orz.key0note.zaimcalendar.util.SharedPreferenceUtils;
import hm.orz.key0note.zaimcalendar.zaimapi.ZaimApiHelper;
import hm.orz.key0note.zaimcalendar.zaimapi.ZaimOAuthClient;

public class ObservingAuthStatusService extends Service {

    public static final String ACTION_LOGIN_FAILED = "ObservingAuthStatusService Action Login Failed";

    private final String TAG = ObservingAuthStatusService.class.getSimpleName();

    private Timer mTimer = new Timer();
    private ObservingAuthStatusServiceBinder mBinder = new ObservingAuthStatusServiceBinder();

    private class ObservingAuthStatusServiceBinder extends Binder {

    }

    public ObservingAuthStatusService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.v(TAG, "run user verify task");

                ZaimOAuthClient authClient = new ZaimOAuthClient(
                        SharedPreferenceUtils.getAccessToken(getApplicationContext()),
                        SharedPreferenceUtils.getAccessTokenSecret(getApplicationContext())
                );
                ZaimApiHelper apiHelper = new ZaimApiHelper(authClient);
                apiHelper.userVerify(new ZaimApiHelper.UserVerifyRequestCallback() {
                    @Override
                    public void onComplete(boolean isLogin) {
                        if (!isLogin) {
                            Log.v(TAG, "user verify failed");

                            Intent intent = new Intent(ACTION_LOGIN_FAILED);
                            sendBroadcast(intent);
                        }
                    }
                });
            }
        }

                , TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(60));

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind");

        mTimer.cancel();
        return true;
    }
}
