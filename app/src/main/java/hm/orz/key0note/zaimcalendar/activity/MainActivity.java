package hm.orz.key0note.zaimcalendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.HashMap;

import hm.orz.key0note.zaimcalendar.R;
import hm.orz.key0note.zaimcalendar.SharedPreferenceUtils;
import hm.orz.key0note.zaimcalendar.ZaimApiHelper;
import hm.orz.key0note.zaimcalendar.ZaimOAuthClient;
import hm.orz.key0note.zaimcalendar.model.CategoryList;
import hm.orz.key0note.zaimcalendar.model.GenreList;
import hm.orz.key0note.zaimcalendar.model.ZaimDayData;
import hm.orz.key0note.zaimcalendar.model.ZaimMonthData;
import hm.orz.key0note.zaimcalendar.view.ZaimCalendarView;
import hm.orz.key0note.zaimcalendar.view.ZaimItemDataArrayAdapter;

public class MainActivity extends ActionBarActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CODE_LOGIN = 1;

    private ZaimMonthData mZaimMonthData;
    private CategoryList mCategoryList;
    private GenreList mGenreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView amountListView = (ListView) findViewById(R.id.amount_lit);

        ZaimCalendarView calendarView = (ZaimCalendarView) findViewById(R.id.calender);
        calendarView.set(2014, 10 - 1);
        calendarView.setOnDayLayoutClickListener(new ZaimCalendarView.OnDayLayoutClickListener() {
            public void onClick(int day) {
                Log.v(TAG, "onClick day = " + day);

                ZaimDayData dayData = mZaimMonthData.getDayData(day);

                ZaimItemDataArrayAdapter adapter = new ZaimItemDataArrayAdapter(
                        getApplicationContext(),
                        android.R.layout.simple_expandable_list_item_1,
                        dayData.getZaimItemDataList(),
                        mCategoryList,
                        mGenreList);
                amountListView.setAdapter(adapter);
            }
        });

        Intent intent = new Intent();
        intent.setClassName("hm.orz.key0note.zaimcalendar", "hm.orz.key0note.zaimcalendar.activity.LoginActivity");
        MainActivity.this.startActivity(intent);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_LOGIN) {
            ZaimOAuthClient authClient = new ZaimOAuthClient(
                    SharedPreferenceUtils.getAccessToken(getApplicationContext()),
                    SharedPreferenceUtils.getAccessTokenSecret(getApplicationContext())
            );
            ZaimApiHelper apiHelper = new ZaimApiHelper(authClient);
            apiHelper.userVerify(new ZaimApiHelper.UserVerifyRequestCallback() {
                @Override
                public void onComplete() {

                }
            });

            apiHelper.getCategoryList(new ZaimApiHelper.GetCategoryListRequestCallback() {
                @Override
                public void onComplete(CategoryList categoryList) {
                    mCategoryList = categoryList;
                }
            });

            apiHelper.getGenreList(new ZaimApiHelper.GetGenreListRequestCallback() {
                @Override
                public void onComplete(GenreList genreList) {
                    mGenreList = genreList;
                }
            });

            final int REQ_YEAR = 2014;
            final int REQ_MONTH = 10;
            apiHelper.getMoneyList(REQ_YEAR, REQ_MONTH, new ZaimApiHelper.GetMoneyListRequestCallback() {
                @Override
                public void onComplete(ZaimMonthData monthData) {
                    mZaimMonthData = monthData;

                    ZaimCalendarView calendarView = (ZaimCalendarView) findViewById(R.id.calender);

                    for (HashMap.Entry<Integer, ZaimDayData> e : monthData.getZaimDayDataMap().entrySet()) {
                        int day = e.getKey().intValue();
                        ZaimDayData dayData = e.getValue();

                        calendarView.setDataOfDay(
                                REQ_MONTH,
                                day,
                                dayData.getSummaryAmount());
                    }
                }
            });

        }
        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
                break;
            default:
                break;
        }
    }
}
