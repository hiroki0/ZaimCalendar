package hm.orz.key0note.zaimcalendar.model;

public class Category {

    int mId;
    String mName;
    String mParentCategoryName;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getParentCategoryName() {
        return mParentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.mParentCategoryName = parentCategoryName;
    }

}
