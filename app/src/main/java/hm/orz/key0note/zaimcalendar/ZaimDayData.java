package hm.orz.key0note.zaimcalendar;

import java.util.ArrayList;


public class ZaimDayData {

    private ArrayList<ZaimItemData> mZaimItemDataArrayList;

    ZaimDayData() {
        mZaimItemDataArrayList = new ArrayList<ZaimItemData>();
    }

    public void addItemData(ZaimItemData itemData) {
        mZaimItemDataArrayList.add(itemData);
    }

    public int getSummaryAmount() {
        int summary = 0;
        for (ZaimItemData itemData : mZaimItemDataArrayList) {
            summary += itemData.getAmount();
        }
        return summary;
    }

    public ArrayList<ZaimItemData> getZaimItemDataList() {
        return mZaimItemDataArrayList;
    }
}
