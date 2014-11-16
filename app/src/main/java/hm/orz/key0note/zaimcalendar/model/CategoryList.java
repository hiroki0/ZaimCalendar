package hm.orz.key0note.zaimcalendar.model;

import java.util.HashMap;

public class CategoryList {

    private HashMap<Integer, Category> mCategoryMap = new HashMap<Integer, Category>();

    public void addCategory(int id, Category category) {
        mCategoryMap.put(id, category);
    }

    public Category getCategory(int id) {
        return mCategoryMap.get(id);
    }
}
