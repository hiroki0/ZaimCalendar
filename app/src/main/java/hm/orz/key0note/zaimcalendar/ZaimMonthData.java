package hm.orz.key0note.zaimcalendar;

import java.util.HashMap;

public class ZaimMonthData {

    private HashMap<Integer, ZaimDayData> mZaimDayDataMap;

    ZaimMonthData() {
        mZaimDayDataMap = new HashMap<Integer, ZaimDayData>();
    }

    public void addItemData(int day, ZaimItemData itemData) {
        if (mZaimDayDataMap.containsKey(day)) {
            ZaimDayData dayData = mZaimDayDataMap.get(day);
            dayData.addItemData(itemData);
        } else {
            ZaimDayData dayData = new ZaimDayData();
            dayData.addItemData(itemData);
            mZaimDayDataMap.put(day, dayData);
        }
    }

    /**
     *
     * @param day assign day you want to get day data
     * @return if not exist DayData, return null.
     */
    public ZaimDayData getDayData(int day) {
        return mZaimDayDataMap.get(day);
    }

    public HashMap<Integer, ZaimDayData> getZaimDayDataMap() {
        return mZaimDayDataMap;
    }
}
