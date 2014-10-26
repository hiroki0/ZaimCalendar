package hm.orz.key0note.zaimcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CODE_LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZaimCalendarView calendarView = (ZaimCalendarView) findViewById(R.id.calender);
        calendarView.set(2014, 10 - 1);

        Intent intent = new Intent();
        intent.setClassName("hm.orz.key0note.zaimcalendar", "hm.orz.key0note.zaimcalendar.LoginActivity");
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
            ZaimClient client = new ZaimClient(authClient);
            client.userVerify(new ZaimClient.RequestCallback() {
                @Override
                public void onComplete(String response) {
                    Log.v("zaim api user verify", "response = " + response);
                }
            });
            client.getMoneyList(new ZaimClient.RequestCallback() {
                @Override
                public void onComplete(String response) {
                    Log.v("zaim api get money list", "response = " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray array = jsonObject.getJSONArray("money");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject data = array.getJSONObject(i);
                            String dateString = data.getString("date");
                            String amountString = data.getString("amount");

                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            format.parse(dateString);
                            Calendar calendar = format.getCalendar();

                            if (calendar.get(Calendar.MONTH) == 9) {
                                ZaimCalendarView calendarView = (ZaimCalendarView) findViewById(R.id.calender);
                                calendarView.setDataOfDay(
                                        calendar.get(Calendar.MONTH) + 1,
                                        calendar.get(Calendar.DAY_OF_MONTH),
                                        Integer.parseInt(amountString));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
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
