package hm.orz.key0note.zaimcalendar.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import hm.orz.key0note.zaimcalendar.R;

public class ZaimCalendarView extends LinearLayout {

    /**
     *
     */
    public interface OnDayLayoutClickListener {
        public void onClick(int day);
    }

    public interface OnChangeDisplayMonthListener {
        public void onChanged(int year, int month);
    }

    private static final String TAG = ZaimCalendarView.class.getSimpleName();

    private static final int WEEKDAYS = 7;
    private static final int MAX_WEEK = 6;

    private static final int UNKNOWN = -1;

    // 週の始まりの曜日を保持する
    private static final int BIGINNING_DAY_OF_WEEK = Calendar.SUNDAY;
    // 今週の背景色
    private static final int TODAY_BACKGROUND_COLOR = Color.LTGRAY;
    // 通常の背景色
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;

    private int mDisplayingYear;
    private int mDisplayingMonth;

    // 年月表示部分
    private TextView mTitleView;

    // 週のレイアウト
    private LinearLayout mWeekLayout;
    private LinearLayout mDayOfMonthLayout;

    private OnDayLayoutClickListener mOnDayLayoutClickListener;
    private OnChangeDisplayMonthListener mOnChangeDisplayMonthListener;

    /**
     * Constractor
     *
     * @param context context
     */
    public ZaimCalendarView(Context context) {
        this(context, null);
    }

    /**
     * Constractor
     *
     * @param context context
     * @param attrs   attributeset
     */
    public ZaimCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View layout = LayoutInflater.from(context).inflate(R.layout.view_zaim_calendar, this);

        mTitleView = (TextView) layout.findViewById(R.id.title);
        mWeekLayout = (LinearLayout) layout.findViewById(R.id.week_view);
        mDayOfMonthLayout = (LinearLayout) layout.findViewById(R.id.day_of_month_layout);

        setOnClickListenerToDayLayout();

        TextView movePrevMonth = (TextView) layout.findViewById(R.id.move_previous_month);
        movePrevMonth.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Calendar calendar = new GregorianCalendar(mDisplayingYear, convertToCalendarMonth(mDisplayingMonth), 1);
                calendar.add(Calendar.MONTH, -1);

                int newDisplayYear = calendar.get(Calendar.YEAR);
                int newDisplayMonth = convertToRegularMonth(calendar.get(Calendar.MONTH));

                displayCalendar(newDisplayYear, newDisplayMonth);
                mOnChangeDisplayMonthListener.onChanged(newDisplayYear, newDisplayMonth);

            }
        });

        TextView moveNextMonth = (TextView) layout.findViewById(R.id.move_next_month);
        moveNextMonth.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Calendar calendar = new GregorianCalendar(mDisplayingYear, convertToCalendarMonth(mDisplayingMonth), 1);
                calendar.add(Calendar.MONTH, 1);

                int newDisplayYear = calendar.get(Calendar.YEAR);
                int newDisplayMonth = convertToRegularMonth(calendar.get(Calendar.MONTH));

                displayCalendar(newDisplayYear, newDisplayMonth);
                mOnChangeDisplayMonthListener.onChanged(newDisplayYear, newDisplayMonth);

            }
        });
    }

    public int getDisplayingYear() {
        return mDisplayingYear;
    }

    public int getDisplayingMonth() {
        return mDisplayingMonth;
    }

    public void setOnDayLayoutClickListener(OnDayLayoutClickListener l) {
        mOnDayLayoutClickListener = l;
    }

    public void removeOnDayLayoutClickListener() {
        mOnDayLayoutClickListener = null;
    }

    public void setOnChangeDisplayMonthListener(OnChangeDisplayMonthListener l) {
        mOnChangeDisplayMonthListener = l;
    }

    public void removeOnChangeDisplayMonthListener() {
        mOnChangeDisplayMonthListener = null;
    }

    /**
     * 年と月を指定して、カレンダーの表示を初期化する
     *
     * @param year  年の指定
     * @param month 月の指定
     */
    public void displayCalendar(int year, int month) {
        mDisplayingYear = year;
        mDisplayingMonth = month;

        initializeTitle(year, month);
        initializeWeekTitle();
        clearDayLayout();
        initalizeAllDayView(year, month);
    }

    /**
     * set money data to day view
     *
     * @param day    input day
     * @param amount amount of money
     */
    public void setDataOfDay(int day, int amount) {
        Log.v(TAG, "setDataOfDay day = " + day + " amount = " + amount);

        Calendar targetCalendar = getTargetCalendar(mDisplayingYear, convertToCalendarMonth(mDisplayingMonth));
        int skipCount = getSkipCount(targetCalendar);
        int count = day + skipCount - 1;
        int row = count / WEEKDAYS;
        int col = count - (WEEKDAYS * row);
        TextView moneyTextView = getDayOfMonthMoneyTextView(row, col);
        moneyTextView.setText(String.format("%1$,3d", amount));
    }

    /**
     * initialize calendar title
     *
     * @param year  displaying year
     * @param month displaying month
     */
    private void initializeTitle(int year, int month) {
        String formatString = getContext().getString(R.string.calendar_title_format);
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);

        Calendar targetCalendar = getTargetCalendar(year, convertToCalendarMonth(month));
        mTitleView.setText(formatter.format(targetCalendar.getTime()));
    }

    /**
     * initialize week title
     */
    private void initializeWeekTitle() {
        Calendar week = Calendar.getInstance();
        week.set(Calendar.DAY_OF_WEEK, BIGINNING_DAY_OF_WEEK);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("E");
        for (int i = 0; i < WEEKDAYS; i++) {
            TextView textView = (TextView) mWeekLayout.getChildAt(i);
            textView.setText(weekFormatter.format(week.getTime()));
            week.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void clearDayLayout() {
        for (int i = 0; i < MAX_WEEK; i++) {
            for (int j = 0; j < WEEKDAYS; j++) {
                TextView dayTextView = getDayOfMonthTextView(i, j);
                dayTextView.setText("");

                TextView moneyTextView = getDayOfMonthMoneyTextView(i, j);
                moneyTextView.setText("");
            }
        }
    }

    /**
     * initialize all day view in calendar
     *
     * @param year  displaying year
     * @param month displaying month
     */
    private void initalizeAllDayView(int year, int month) {
        Calendar targetCalendar = getTargetCalendar(year, convertToCalendarMonth(month));

        for (int i = 0; i < MAX_WEEK; i++) {
            for (int j = 0; j < WEEKDAYS; j++) {
                if (isEmptyDayView(targetCalendar, i, j)) {
                    continue;
                }

                TextView dayTextView = getDayOfMonthTextView(i, j);
                // set day number
                int day = getDayNumber(targetCalendar, i, j);
                dayTextView.setText(String.valueOf(day));
            }
        }
    }

    /**
     * set onClickLister To Layout of day
     */
    private void setOnClickListenerToDayLayout() {
        for (int i = 0; i < MAX_WEEK; i++) {
            for (int j = 0; j < WEEKDAYS; j++) {
                LinearLayout layout = getDayOfMonthLinearLayout(i, j);
                layout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        int dayNumber = findDayNumberFormDayLayout((LinearLayout) v);
                        if (dayNumber != UNKNOWN) {
                            if (mOnDayLayoutClickListener != null) {
                                mOnDayLayoutClickListener.onClick(dayNumber);
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * カレンダーの最初の空白の個数を求める
     *
     * @param targetCalendar 指定した月のCalendarのInstance
     * @return skipCount
     */
    private int getSkipCount(Calendar targetCalendar) {
        int skipCount; // 空白の個数
        int firstDayOfWeekOfMonth = targetCalendar.get(Calendar.DAY_OF_WEEK); // 1日の曜日
        if (BIGINNING_DAY_OF_WEEK > firstDayOfWeekOfMonth) {
            skipCount = firstDayOfWeekOfMonth - BIGINNING_DAY_OF_WEEK + WEEKDAYS;
        } else {
            skipCount = firstDayOfWeekOfMonth - BIGINNING_DAY_OF_WEEK;
        }
        return skipCount;
    }

    private int findDayNumberFormDayLayout(LinearLayout dayLayout) {
        TextView dayTextView = (TextView) dayLayout.findViewById(R.id.day_text);
        String dayString = dayTextView.getText().toString();
        try {
            if (!"".equals(dayString)) {
                return Integer.parseInt(dayString);
            }
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
        return UNKNOWN;
    }

    private boolean isEmptyDayView(Calendar targetCalendar, int row, int col) {
        return (getDayNumber(targetCalendar, row, col) == UNKNOWN);
    }

    private LinearLayout getDayOfMonthLinearLayout(int row, int col) {
        LinearLayout weekLayout = (LinearLayout) mDayOfMonthLayout.getChildAt(row);
        return (LinearLayout) weekLayout.getChildAt(col);
    }

    private TextView getDayOfMonthTextView(int row, int col) {
        return (TextView) getDayOfMonthLinearLayout(row, col).findViewById(R.id.day_text);
    }

    private TextView getDayOfMonthMoneyTextView(int row, int col) {
        LinearLayout dayView = getDayOfMonthLinearLayout(row, col);
        return (TextView) dayView.findViewById(R.id.money_text);
    }

    private int getDayNumber(Calendar targetCalendar, int row, int col) {
        int skipCount = getSkipCount(targetCalendar);
        int count = row * WEEKDAYS + col;
        int day = count - skipCount + 1;

        int lastDay = targetCalendar.getActualMaximum(Calendar.DATE);
        if (day < 1 || lastDay < day) {
            return UNKNOWN;
        }

        return day;
    }

    private Calendar getTargetCalendar(int year, int month) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.clear(); // カレンダー情報の初期化
        targetCalendar.set(Calendar.YEAR, year);
        targetCalendar.set(Calendar.MONTH, month);
        targetCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return targetCalendar;
    }

    /**
     * convert from regular use month to java.utils.Calendar month
     *
     * @param commonMonth you want to convert regular use month
     * @return converted java.utils.Calendar month
     */
    private int convertToCalendarMonth(int commonMonth) {
        return commonMonth - 1;
    }

    /**
     * convert from java.utils.Calendar month to regular use month
     *
     * @param regularMonth you want to convert java.utils.Calendar month
     * @return converted regular use month
     */
    private int convertToRegularMonth(int regularMonth) {
        return regularMonth + 1;
    }
}
