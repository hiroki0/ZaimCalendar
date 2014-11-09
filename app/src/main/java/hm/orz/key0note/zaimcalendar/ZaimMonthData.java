package hm.orz.key0note.zaimcalendar;

import java.util.HashMap;

public class ZaimMonthData {

    private HashMap<Integer, ZaimDayData> mZaimDayDataMap;

    ZaimMonthData() {
        mZaimDayDataMap = new HashMap<Integer, ZaimDayData>();
    }

    public void setDataOfDay(int day, ZaimItemData itemData) {
        if (mZaimDayDataMap.containsKey(day)) {
            ZaimDayData dayData = mZaimDayDataMap.get(day);
            dayData.addItemData(itemData);
        } else {
            ZaimDayData dayData = new ZaimDayData();
            dayData.addItemData(itemData);
            mZaimDayDataMap.put(day, dayData);
        }
    }

    public HashMap<Integer, ZaimDayData> getZaimDayDataMap() {
        return mZaimDayDataMap;
    }
}
