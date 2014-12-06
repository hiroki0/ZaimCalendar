package hm.orz.key0note.zaimcalendar.model;

public class ZaimItemData {

    private Mode mMode = Mode.UNKNOWN;
    private int mCategoryId;
    private int mGenreId;
    private int mAmount;
    private String mPlace;
    private String mComment;

    public enum Mode {
        UNKNOWN,
        INCOME,
        PAYMENT,
    }

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        this.mMode = mode;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        this.mCategoryId = categoryId;
    }

    public int getGenreId() {
        return mGenreId;
    }

    public void setGenreId(int genreId) {
        this.mGenreId = genreId;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int mAmount) {
        this.mAmount = mAmount;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        this.mPlace = place;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }
}
