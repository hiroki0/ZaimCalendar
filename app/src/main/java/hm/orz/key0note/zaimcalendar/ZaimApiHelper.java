package hm.orz.key0note.zaimcalendar;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ZaimApiHelper {

    private static final String ZAIM_DOMAIN = "https://api.zaim.net";

    private ZaimClient mZaimClient;

    ZaimApiHelper(ZaimOAuthClient zaimOAuthClient) {
        mZaimClient = new ZaimClient(zaimOAuthClient);
    }

    public interface UserVerifyRequestCallback {
        public void onComplete();
    }

    public void userVerify(final UserVerifyRequestCallback callback) {
        String urlString = ZAIM_DOMAIN + "/v2/home/user/verify";

        ZaimRequest request = new ZaimRequest(urlString);
        mZaimClient.sendRequest(request, new ZaimClient.RequestCallback() {
            public void onComplete(String response) {
                Log.v("zaim api user verify", "response = " + response);
                callback.onComplete();
            }
        });
    }

    public interface GetMoneyListRequestCallback {
        public void onComplete(ZaimMonthData zaimMonthData);
    }

    public void getMoneyList(int requestYear, int requestMonth, final GetMoneyListRequestCallback callback) {
        String urlString = ZAIM_DOMAIN + "/v2/home/money";

        Calendar startDate = Calendar.getInstance();
        startDate.set(requestYear, requestMonth - 1, 1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(requestYear, requestMonth - 1, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        ZaimRequest request = new ZaimRequest(urlString);
        request.addParam("mapping", "1");
        request.addParam("start_date", format.format(startDate.getTime()));
        request.addParam("end_date", format.format(endDate.getTime()));

        mZaimClient.sendRequest(request, new ZaimClient.RequestCallback() {
            public void onComplete(String response) {
                Log.v("zaim api get money list", "response = " + response);
                try {
                    ZaimMonthData monthData = new ZaimMonthData();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("money");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject data = array.getJSONObject(i);
                        String dateString = data.getString("date");
                        String amountString = data.getString("amount");

                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        format.parse(dateString);
                        Calendar calendar = format.getCalendar();

                        ZaimItemData itemData = new ZaimItemData();
                        itemData.setAmount(Integer.parseInt(amountString));
                        monthData.setDataOfDay(calendar.get(Calendar.DAY_OF_MONTH), itemData);
                    }
                    callback.onComplete(monthData);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
