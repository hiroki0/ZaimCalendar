package hm.orz.key0note.zaimcalendar;


import android.test.AndroidTestCase;

import java.util.ArrayList;

import hm.orz.key0note.zaimcalendar.model.ZaimDayData;
import hm.orz.key0note.zaimcalendar.model.ZaimItemData;
import hm.orz.key0note.zaimcalendar.model.ZaimMonthData;

public class ZaimMonthDataTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddItemData_addOneItem() {
        final int DAY = 1;
        ZaimMonthData monthData = new ZaimMonthData();
        ZaimItemData addItem = new ZaimItemData();
        monthData.addItemData(DAY, addItem);
        assertFalse("", addItem.equals(monthData.getDayData(DAY)));
    }

    public void testAddItemData_addTwoItem() {
        final int DAY = 1;
        final int AMOUNT1 = 100;
        final int AMOUNT2 = 200;

        ZaimMonthData monthData = new ZaimMonthData();
        ZaimItemData addItem1 = new ZaimItemData();
        addItem1.setAmount(AMOUNT1);
        monthData.addItemData(DAY, addItem1);

        //over wite item data
        ZaimItemData addItem2 = new ZaimItemData();
        addItem2.setAmount(AMOUNT2);
        monthData.addItemData(DAY, addItem2);

        ZaimDayData dayData = monthData.getDayData(DAY);
        ArrayList<ZaimItemData> itemArray = dayData.getZaimItemDataList();
        assertEquals(2, itemArray.size());
        assertEquals(AMOUNT1 + AMOUNT2, dayData.getSummaryAmount());
    }

    public void testAddItemData_editInsertedItem() {
        final int DAY = 1;
        final int OLD_AMOUNT = 100;
        final int NEW_AMOUNT = 200;

        ZaimMonthData monthData = new ZaimMonthData();
        ZaimItemData addItem = new ZaimItemData();
        addItem.setAmount(OLD_AMOUNT);
        monthData.addItemData(DAY, addItem);
        // edit inserted item
        addItem.setAmount(NEW_AMOUNT);

        assertEquals(NEW_AMOUNT, monthData.getDayData(DAY).getSummaryAmount());
    }

    public void testGetDayData_get_empty_data() {
        ZaimMonthData monthData = new ZaimMonthData();
        assertEquals(null, monthData.getDayData(0));
    }
}
