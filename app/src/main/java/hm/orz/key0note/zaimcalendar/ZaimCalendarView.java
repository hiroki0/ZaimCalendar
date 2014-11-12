package hm.orz.key0note.zaimcalendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ZaimCalendarView extends LinearLayout implements View.OnClickListener {

    /**
     *
     */
    public interface OnDayLayoutClickListener {
        public void onClick(int day);
    }

    private static final String TAG = ZaimCalendarView.class.getSimpleName();

    private static final int WEEKDAYS = 7;
    private static final int MAX_WEEK = 6;

    private static final int UNKNOWN = -1;

    // 週の始まりの曜日を保持する
    private static final int BIGINNING_DAY_OF_WEEK = Calendar.SUNDAY;
    // 今日のフォント色
    private static final int TODAY_COLOR = Color.RED;
    // 通常のフォント色
    private static final int DEFAULT_COLOR = Color.DKGRAY;
    // 今週の背景色
    private static final int TODAY_BACKGROUND_COLOR = Color.LTGRAY;
    // 通常の背景色
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;

    // 年月表示部分
    private TextView mTitleView;

    // 週のレイアウト
    private LinearLayout mWeekLayout;
    private LinearLayout mDayOfMonthLayout;

    //
    private OnDayLayoutClickListener mOnDayLayoutClickListener;

    /**
     * コンストラクタ
     *
     * @param context context
     */
    public ZaimCalendarView(Context context) {
        this(context, null);
    }

    /**
     * コンストラクタ
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

        setOnClickListenerToDayLayout(this);
    }

    public void setOnDayLayoutClickListener(OnDayLayoutClickListener l) {
        mOnDayLayoutClickListener = l;
    }

    public void removeOnDayLayoutClickListener() {
        mOnDayLayoutClickListener = null;
    }

    /**
     * 年と月を指定して、カレンダーの表示を初期化する
     *
     * @param year  年の指定
     * @param month 月の指定
     */
    public void set(int year, int month) {
        setTitle(year, month);
        setWeeks();
        setDays(year, month);
    }

    /**
     * 日にち蘭にデータを入力する
     * @param month 入力先の月
     * @param day 入力先の日
     * @param amount 金額
     */
    public void setDataOfDay(int month, int day, int amount) {
        Log.v(TAG, "month = " + String.valueOf(month) + " day = " + String.valueOf(day) + " amount = " + String.valueOf(amount));
        Calendar targetCalendar = getTargetCalendar(2014, month - 1);
        int skipCount = getSkipCount(targetCalendar);
        int count = day + skipCount - 1;
        int row = count / WEEKDAYS;
        int col = count - (WEEKDAYS * row);
        TextView moneyTextView = getDayOfMonthMoneyTextView(row, col);
        moneyTextView.setText(String.valueOf(amount));
    }

    /**
     * 指定した年月日をタイトルに設定する
     *
     * @param year  年の指定
     * @param month 月の指定
     */
    private void setTitle(int year, int month) {
        Calendar targetCalendar = getTargetCalendar(year, month);
        // 年月フォーマット文字列
        //String formatString = mTitleView.getContext().getString(R.string.format_month_year);
        String formatString = "y年M月";
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        mTitleView.setText(formatter.format(targetCalendar.getTime()));
    }

    /**
     * 曜日を設定する
     */
    private void setWeeks() {
        Calendar week = Calendar.getInstance();
        week.set(Calendar.DAY_OF_WEEK, BIGINNING_DAY_OF_WEEK); // 週の頭をセット
        SimpleDateFormat weekFormatter = new SimpleDateFormat("E"); // 曜日を取得するフォーマッタ
        for (int i = 0; i < WEEKDAYS; i++) {
            TextView textView = (TextView) mWeekLayout.getChildAt(i);
            textView.setText(weekFormatter.format(week.getTime())); // テキストに曜日を表示
            week.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    /**
     * 日付を設定していくメソッド
     *
     * @param year  年の指定
     * @param month 月の指定
     */
    private void setDays(int year, int month) {
        Calendar targetCalendar = getTargetCalendar(year, month);

        int skipCount = getSkipCount(targetCalendar);
        int lastDay = targetCalendar.getActualMaximum(Calendar.DATE);
        int dayCounter = 1;

        Calendar todayCalendar = Calendar.getInstance();
        int todayYear = todayCalendar.get(Calendar.YEAR);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < MAX_WEEK; i++) {
            for (int j = 0; j < WEEKDAYS; j++) {
                TextView dayTextView = getDayOfMonthTextView(i, j);

                // 第一週かつskipCountが残っていれば
                if (i == 0 && skipCount > 0) {
                    dayTextView.setText("");
                    skipCount--;
                    continue;
                }

                // 最終日より大きければ
                if (lastDay < dayCounter) {
                    dayTextView.setText(" ");
                    continue;
                }

                // 日付を設定
                dayTextView.setText(String.valueOf(dayCounter));

                boolean isToday = todayYear == year &&
                        todayMonth == month &&
                        todayDay == dayCounter;

                if (isToday) {
                    dayTextView.setTextColor(TODAY_COLOR); // 赤文字
                    dayTextView.setTypeface(null, Typeface.BOLD); // 太字
                    //weekLayout.setBackgroundColor(TODAY_BACKGROUND_COLOR); // 週の背景グレー
                } else {
                    dayTextView.setTextColor(DEFAULT_COLOR);
                    dayTextView.setTypeface(null, Typeface.NORMAL);
                }
                dayCounter++;
            }
        }
    }

    /**
     * 日にちのLayoutにOnClickListerをセットする
     *
     * @param clickListener セットするOnClickListener
     */
    private void setOnClickListenerToDayLayout(OnClickListener clickListener) {
        for (int i = 0; i < MAX_WEEK; i++) {
            for (int j = 0; j < WEEKDAYS; j++) {
                LinearLayout layout = getDayOfMonthLinearLayout(i, j);
                layout.setOnClickListener(clickListener);
            }
        }
    }

    public void onClick(View v) {
        int dayNumber = findDayNumberFormDayLayout((LinearLayout)v);
        if (dayNumber != UNKNOWN) {
            if (mOnDayLayoutClickListener != null) {
                mOnDayLayoutClickListener.onClick(dayNumber);
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
        for (int i = 0; i < MAX_WEEK; i++) {
            for (int j = 0; j < WEEKDAYS; j++) {
                if (dayLayout == getDayOfMonthLinearLayout(i, j)) {
                    return getDayNumber(i, j);
                }
            }
        }
        return UNKNOWN;
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

    private int getDayNumber(int row, int col) {
        TextView view = (TextView) getDayOfMonthLinearLayout(row, col).findViewById(R.id.day_text);
        String dayText = view.getText().toString();
        try {
            if (!dayText.equals("")) {
                return Integer.parseInt(dayText);
            }
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
        return UNKNOWN;
    }

    private Calendar getTargetCalendar(int year, int month) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.clear(); // カレンダー情報の初期化
        targetCalendar.set(Calendar.YEAR, year);
        targetCalendar.set(Calendar.MONTH, month);
        targetCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return targetCalendar;
    }
}
