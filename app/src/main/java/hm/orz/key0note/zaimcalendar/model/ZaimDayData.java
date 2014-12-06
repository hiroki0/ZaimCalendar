package hm.orz.key0note.zaimcalendar.model;

import java.util.ArrayList;


public class ZaimDayData {

    private ArrayList<ZaimItemData> mZaimItemDataArrayList;

    public ZaimDayData() {
        mZaimItemDataArrayList = new ArrayList<ZaimItemData>();
    }

    public void addItemData(ZaimItemData itemData) {
        mZaimItemDataArrayList.add(itemData);
    }

    public int getSummaryAmount() {
        int summary = 0;
        for (ZaimItemData itemData : mZaimItemDataArrayList) {
            if (itemData.getMode() == ZaimItemData.Mode.INCOME) {
                summary += itemData.getAmount();
            } else if (itemData.getMode() == ZaimItemData.Mode.PAYMENT) {
                summary -= itemData.getAmount();
            }
        }
        return summary;
    }

    public ArrayList<ZaimItemData> getZaimItemDataList() {
        return mZaimItemDataArrayList;
    }
}
