package hm.orz.key0note.zaimcalendar.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.Calendar;
import java.util.HashMap;

import hm.orz.key0note.zaimcalendar.R;
import hm.orz.key0note.zaimcalendar.model.CategoryList;
import hm.orz.key0note.zaimcalendar.model.GenreList;
import hm.orz.key0note.zaimcalendar.model.ZaimDayData;
import hm.orz.key0note.zaimcalendar.model.ZaimMonthData;
import hm.orz.key0note.zaimcalendar.service.ObservingAuthStatusService;
import hm.orz.key0note.zaimcalendar.util.SharedPreferenceUtils;
import hm.orz.key0note.zaimcalendar.view.ZaimCalendarView;
import hm.orz.key0note.zaimcalendar.view.ZaimItemDataArrayAdapter;
import hm.orz.key0note.zaimcalendar.zaim.ZaimApiHelper;
import hm.orz.key0note.zaimcalendar.zaim.ZaimOAuthClient;

public class MainActivity extends ActionBarActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CODE_LOGIN = 1;

    private ZaimMonthData mZaimMonthData;

    private CategoryList mCategoryList;
    private GenreList mGenreList;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // write that the authentication is disable
            SharedPreferenceUtils.setLoginState(getApplicationContext(), false);
            // stat LoginActivity to in order to redo authentication
            startLoginActivity();
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView amountListView = (ListView) findViewById(R.id.amount_lit);

        ZaimCalendarView calendarView = (ZaimCalendarView) findViewById(R.id.calender);

        // display this month data
        Calendar today = Calendar.getInstance();
        calendarView.displayCalendar(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH) + 1);
        calendarView.setOnDayLayoutClickListener(new ZaimCalendarView.OnDayLayoutClickListener() {
            public void onClick(int day) {
                Log.v(TAG, "onClick day = " + day);

                if (mZaimMonthData == null) {
                    return;
                }

                ZaimDayData dayData = mZaimMonthData.getDayData(day);
                if (dayData == null) {
                    clearZaimItemListView(amountListView);
                    return;
                }

                ZaimItemDataArrayAdapter adapter = new ZaimItemDataArrayAdapter(
                        getApplicationContext(),
                        android.R.layout.simple_expandable_list_item_1,
                        dayData.getZaimItemDataList(),
                        mCategoryList,
                        mGenreList);
                amountListView.setAdapter(adapter);
            }
        });
        calendarView.setOnChangeDisplayMonthListener(new ZaimCalendarView.OnChangeDisplayMonthListener() {
            public void onChanged(int year, int month) {
                // update zaim month data
                updateMonthData(year, month);

                clearZaimItemListView(amountListView);
            }
        });

        // regist login activity dispatcher
        IntentFilter filter = new IntentFilter();
        filter.addAction(ObservingAuthStatusService.ACTION_LOGIN_FAILED);
        registerReceiver(mBroadcastReceiver, filter);

        // get data from zaim, and update display
        updateCategoryList();
        updateGenreList();
        updateMonthData(
                calendarView.getDisplayingYear(),
                calendarView.getDisplayingMonth());
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!SharedPreferenceUtils.isLogin(getApplicationContext())) {
            startLoginActivity();
        }

        //
        Intent intent = new Intent(getApplicationContext(), ObservingAuthStatusService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();

        unbindService(mServiceConnection);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_LOGIN) {
            updateCategoryList();
            updateGenreList();

            ZaimCalendarView calendarView = (ZaimCalendarView) findViewById(R.id.calender);
            updateMonthData(
                    calendarView.getDisplayingYear(),
                    calendarView.getDisplayingMonth());
        }
    }

    private void startLoginActivity() {
        Intent i = new Intent();
        i.setClassName("hm.orz.key0note.zaimcalendar", "hm.orz.key0note.zaimcalendar.activity.LoginActivity");
        startActivityForResult(i, REQUEST_CODE_LOGIN);
    }

    private void updateGenreList() {
        ZaimApiHelper apiHelper = getZaimApiHelper();

        apiHelper.getGenreList(new ZaimApiHelper.GetGenreListRequestCallback() {
            @Override
            public void onComplete(GenreList genreList) {
                mGenreList = genreList;
            }
        });
    }

    private void updateCategoryList() {
        ZaimApiHelper apiHelper = getZaimApiHelper();
        apiHelper.getCategoryList(new ZaimApiHelper.GetCategoryListRequestCallback() {
            @Override
            public void onComplete(CategoryList categoryList) {
                mCategoryList = categoryList;
            }
        });
    }

    public void updateMonthData(final int year, final int month) {
        ZaimApiHelper apiHelper = getZaimApiHelper();
        apiHelper.getMoneyList(year, month, new ZaimApiHelper.GetMoneyListRequestCallback() {
            @Override
            public void onComplete(ZaimMonthData monthData) {
                mZaimMonthData = monthData;

                ZaimCalendarView calendarView = (ZaimCalendarView) findViewById(R.id.calender);

                for (HashMap.Entry<Integer, ZaimDayData> e : monthData.getZaimDayDataMap().entrySet()) {
                    int day = e.getKey().intValue();
                    ZaimDayData dayData = e.getValue();

                    calendarView.setDataOfDay(
                            day,
                            dayData.getSummaryAmount());
                }
            }
        });
    }

    private ZaimApiHelper getZaimApiHelper() {
        ZaimOAuthClient authClient = new ZaimOAuthClient(
                SharedPreferenceUtils.getAccessToken(getApplicationContext()),
                SharedPreferenceUtils.getAccessTokenSecret(getApplicationContext())
        );
        return new ZaimApiHelper(authClient);
    }

    private void clearZaimItemListView(ListView amountListView) {
        ZaimItemDataArrayAdapter adapter = (ZaimItemDataArrayAdapter) amountListView.getAdapter();
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
    }

}
