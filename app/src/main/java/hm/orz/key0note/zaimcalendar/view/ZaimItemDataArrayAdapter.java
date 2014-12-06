package hm.orz.key0note.zaimcalendar.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import hm.orz.key0note.zaimcalendar.R;
import hm.orz.key0note.zaimcalendar.model.Category;
import hm.orz.key0note.zaimcalendar.model.CategoryList;
import hm.orz.key0note.zaimcalendar.model.Genre;
import hm.orz.key0note.zaimcalendar.model.GenreList;
import hm.orz.key0note.zaimcalendar.model.ZaimItemData;

public class ZaimItemDataArrayAdapter extends ArrayAdapter<ZaimItemData> {

    private LayoutInflater mLayoutInflater;
    private CategoryList mCategoryList;
    private GenreList mGenreList;

    public ZaimItemDataArrayAdapter(
            Context context,
            int textViewResourceId,
            List<ZaimItemData> objects,
            CategoryList categoryList,
            GenreList genreList) {
        super(context, textViewResourceId, objects);

        mCategoryList = categoryList;
        mGenreList = genreList;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ZaimItemData itemData = getItem(position);

        if (convertView == null) {
            // create new view
            convertView = mLayoutInflater.inflate(R.layout.zaim_item, null);
        }

        TextView categoryTextView = (TextView) convertView.findViewById(R.id.category);
        setCategoryTextView(categoryTextView, itemData);

        TextView amountTextView = (TextView) convertView.findViewById(R.id.amount);
        setAmountTextView(amountTextView, itemData);

        TextView placeTextView = (TextView) convertView.findViewById(R.id.place);
        setPlaceTextView(placeTextView, itemData);

        TextView commentTextView = (TextView) convertView.findViewById(R.id.comment);
        commentTextView.setText(itemData.getComment());

        return convertView;
    }

    private void setCategoryTextView(TextView categoryTextView, ZaimItemData itemData) {
        if (mCategoryList == null || mGenreList == null) {
            categoryTextView.setText("unknown");
            return;
        }

        int categoryId = itemData.getCategoryId();
        int genreId = itemData.getGenreId();
        Category category = mCategoryList.getCategory(categoryId);
        Genre genre = mGenreList.getGenre(genreId);
        if (category == null || genre == null) {
            categoryTextView.setText("unknown");
            return;
        }

        categoryTextView.setText(String.format("%s > %s", category.getName(), genre.getName()));
    }

    private void setAmountTextView(TextView amountTextView, ZaimItemData itemData) {
        amountTextView.setText(String.format("%1$,3d", itemData.getAmount()));

        // set amount text color
        if (itemData.getMode() == ZaimItemData.Mode.INCOME) {
            int color = getContext().getResources().getColor(R.color.income);
            amountTextView.setTextColor(color);

        } else if (itemData.getMode() == ZaimItemData.Mode.PAYMENT) {
            int color = getContext().getResources().getColor(R.color.payment);
            amountTextView.setTextColor(color);
        }
    }

    private void setPlaceTextView(TextView placeTextView, ZaimItemData itemData) {
        String place = itemData.getPlace();
        if (place.equals("")) {
            placeTextView.setText("");
        } else {
            placeTextView.setText(String.format("@%s", itemData.getPlace()));
        }
    }
}
