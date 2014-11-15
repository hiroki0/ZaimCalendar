package hm.orz.key0note.zaimcalendar;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import hm.orz.key0note.zaimcalendar.model.Category;
import hm.orz.key0note.zaimcalendar.model.ZaimItemData;
import hm.orz.key0note.zaimcalendar.model.ZaimMonthData;

public class ZaimApiHelper {

    private static final String ZAIM_DOMAIN = "https://api.zaim.net";

    private ZaimClient mZaimClient;

    public ZaimApiHelper(ZaimOAuthClient zaimOAuthClient) {
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
                        String categoryIdString = data.getString("category_id");
                        String amountString = data.getString("amount");

                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        format.parse(dateString);
                        Calendar calendar = format.getCalendar();

                        ZaimItemData itemData = new ZaimItemData();
                        itemData.setCategoryId(Integer.parseInt(categoryIdString));
                        itemData.setAmount(Integer.parseInt(amountString));
                        monthData.addItemData(calendar.get(Calendar.DAY_OF_MONTH), itemData);
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

    public interface GetCategoryListRequestCallback {
        public void onComplete();
    }

    public void getCategoryList(final GetCategoryListRequestCallback callback) {
        String urlString = ZAIM_DOMAIN + "/v2/home/category";

        ZaimRequest request = new ZaimRequest(urlString);
        request.addParam("mapping", "1");

        mZaimClient.sendRequest(request, new ZaimClient.RequestCallback() {
            public void onComplete(String response) {
                Log.v("zaim api get category list", "response = " + response);
                try {
                    HashMap<Integer, String> parentCategoryMap = new HashMap<Integer, String>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray categoriesArray = jsonObject.getJSONArray("categories");
                    for (int i = 0; i < categoriesArray.length(); i++) {
                        JSONObject data = categoriesArray.getJSONObject(i);
                        String idString = data.getString("id");
                        String nameString = data.getString("name");

                        parentCategoryMap.put(
                                Integer.parseInt(idString),
                                nameString);
                    }

                    JSONArray moneyArray = jsonObject.getJSONArray("money");
                    for (int i = 0; i < moneyArray.length(); i++) {
                        JSONObject data = moneyArray.getJSONObject(i);
                        String idString = data.getString("id");
                        String nameString = data.getString("name");
                        String parentCategoryId = data.getString("parent_category_id");

                        Category category = new Category();
                        category.setId(Integer.parseInt(idString));
                        category.setName(nameString);

                        String parentCategoryName = parentCategoryMap.get(Integer.parseInt(parentCategoryId));
                        if (parentCategoryName != null) {
                            category.setParentCategoryName(parentCategoryName);
                        }
                    }
                    callback.onComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
