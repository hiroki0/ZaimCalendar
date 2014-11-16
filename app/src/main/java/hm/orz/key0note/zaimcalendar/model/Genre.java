package hm.orz.key0note.zaimcalendar.model;

public class Genre {
    int mId;
    int mCategoryId;
    String mName;


    public int getId() {

        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        this.mCategoryId = categoryId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }
}
