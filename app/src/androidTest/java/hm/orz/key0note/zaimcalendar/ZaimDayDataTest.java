package hm.orz.key0note.zaimcalendar;

import android.test.AndroidTestCase;

import java.util.ArrayList;

import hm.orz.key0note.zaimcalendar.model.ZaimDayData;
import hm.orz.key0note.zaimcalendar.model.ZaimItemData;

public class ZaimDayDataTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddItemData() throws Exception {
        ZaimDayData dayData = new ZaimDayData();
        ZaimItemData itemData = new ZaimItemData();

        dayData.addItemData(itemData);
    }

    public void testGetZaimItemDataList() {
        ZaimDayData dayData = new ZaimDayData();
        ZaimItemData itemData = new ZaimItemData();

        dayData.addItemData(itemData);
        ArrayList<ZaimItemData> itemList = dayData.getZaimItemDataList();

        //check itemList
        assertEquals(1, itemList.size());
        assertSame(itemData, itemList.get(0));
    }

    public void testGetSummaryAmount() {
        final int ITEM1_AMOUNT = 100;
        final int ITEM2_AMOUNT = 200;

        ZaimDayData dayData = new ZaimDayData();
        ZaimItemData itemData1 = new ZaimItemData();
        ZaimItemData itemData2 = new ZaimItemData();

        itemData1.setAmount(ITEM1_AMOUNT);
        itemData2.setAmount(ITEM2_AMOUNT);

        dayData.addItemData(itemData1);
        dayData.addItemData(itemData2);

        assertEquals(
                ITEM1_AMOUNT + ITEM2_AMOUNT,
                dayData.getSummaryAmount());
    }

    public void testGetSummaryAmountOnEmpty() {
        ZaimDayData dayData = new ZaimDayData();
        assertEquals(0, dayData.getSummaryAmount());
    }
}
