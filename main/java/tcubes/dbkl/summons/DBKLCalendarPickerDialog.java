package tcubes.dbkl.summons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tcubes.dbkl.summons.R;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class DBKLCalendarPickerDialog extends AlertDialog implements android.view.View.OnClickListener
{
	private Button selectedDayMonthYearButton;
	private Button currentMonth;
	// private Button currentYear;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private static int selectedMonth, selectedYear, selectedDate;
	private int month, year;
	private static final String dateTemplate = "MMM yyyy";

	public int getSelectMonth()
	{
		return selectedMonth;
	}

	public int getSelectedYear()
	{
		return selectedYear;
	}

	public int getSelectedDate()
	{
		return selectedDate;
	}

	public static String getMonthAsString(int i)
	{
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Augt", "Sep", "Oct", "Nov", "Dec" };
		return months[i];
	}

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_calendar_view);

		_calendar = Calendar.getInstance(Locale.getDefault());
		month = selectedMonth + 1;// _calendar.get(Calendar.MONTH) + 1;
		year = selectedYear;// _calendar.get(Calendar.YEAR);
		// Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " +
		// "Year: " + year);

		selectedDayMonthYearButton = (Button) this.findViewById(R.id.selectedDayMonthYear);

		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) this.findViewById(R.id.currentMonth);
		currentMonth.setText(DateFormat.format(dateTemplate, new
				Date(selectedYear - 1900, selectedMonth, selectedDate)));

		// currentMonth.setOnClickListener(this);

		// currentYear = (Button) this.findViewById(R.id.currentYear);
		// currentYear.setText(selectedYear + "");
		// currentYear.setOnClickListener(this);

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);

		// Initialised
		adapter = new GridCellAdapter(getContext(), R.id.calendar_day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
		// currentMonth.setText(adapter.getMonthAsString(selectedMonth));
		selectedDayMonthYearButton.setText(selectedDate + "-" + adapter.getMonthAsString(selectedMonth) + "-" + selectedYear);

	}
	/**
	 * 
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year)
	{
		adapter = new GridCellAdapter(getContext(), R.id.calendar_day_gridcell, month, year);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));

		// currentMonth.setText(getMonthAsString(month - 1));
		// currentYear.setText(year + "");
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v)
	{
		if (v == prevMonth)
		{
			if (month <= 1)
			{
				month = 12;
				year--;
			}
			else
			{
				month--;
			}
			// Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: "
			// + month + " Year: " + year);
			setGridCellAdapterToDate(month, year);
		}
		if (v == nextMonth)
		{
			if (month > 11)
			{
				month = 1;
				year++;
			}
			else
			{
				month++;
			}
			// Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: "
			// + month + " Year: " + year);
			setGridCellAdapterToDate(month, year);
		}

	}

	@Override
	public void onAttachedToWindow()
	{
		/*if (CacheManager.IsLock)
		{
			this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		}*/
		super.onAttachedToWindow();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.AlertDialog#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_SEARCH || (event.getFlags() == KeyEvent.FLAG_LONG_PRESS))
		{
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME)
		{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////
	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements android.view.View.OnClickListener
	{
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		// private static int selectedMonth, selectedYear, selectedDate;
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private TextView num_events_per_day;
		private final HashMap<?, ?> eventsPerMonthMap;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

		public GridCellAdapter(Context context, int textViewResourceId, int month, int year)
		{
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			// Log.d(tag, "==> Passed in Date FOR Month: " + month + " " +
			// "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			// Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
			// Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
			// Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

			// Print Month
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}

		public String getMonthAsString(int i)
		{
			return months[i];
		}

		public String getWeekDayAsString(int i)
		{
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i)
		{
			return daysOfMonth[i];
		}

		@Override
		public String getItem(int position)
		{
			return list.get(position);
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		/**
		 * Prints Month
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy)
		{
			// Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			// The number of days to leave blank at
			// the start of this month.
			int trailingSpaces = 0;
			// int leadSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			// String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			// Log.d(tag, "Current Month: " + " " + currentMonthName +
			// " having " + daysInMonth + " days.");

			// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			// Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

			if (currentMonth == 11)
			{
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
				// Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:" +
				// prevMonth + " NextMonth: " + nextMonth + " NextYear: " +
				// nextYear);
			}
			else if (currentMonth == 0)
			{
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				// Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:" +
				// prevMonth + " NextMonth: " + nextMonth + " NextYear: " +
				// nextYear);
			}
			else
			{
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				// Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:" +
				// prevMonth + " NextMonth: " + nextMonth + " NextYear: " +
				// nextYear);
			}

			// Compute how much to leave before before the first day of the
			// month.
			// getDay() returns 0 for Sunday.
			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			// Log.d(tag, "Week Day:" + currentWeekDay + " is " +
			// getWeekDayAsString(currentWeekDay));
			// Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
			// Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1)
			{
				++daysInMonth;
			}

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++)
			{
				// Log.d(tag, "PREV MONTH:= " + prevMonth + " => " +
				// getMonthAsString(prevMonth) + " " +
				// String.valueOf((daysInPrevMonth - trailingSpaces +
				// DAY_OFFSET) + i));
				list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++)
			{
				// Log.d(currentMonthName, String.valueOf(i) + " " +
				// getMonthAsString(currentMonth) + " " + yy);
				if (IsSelectedDate(i, currentMonth, yy))
				{
					list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
				}
				else
				{
					list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++)
			{
				// Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
			}
		}

		/**
		 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
		 * ALL entries from a SQLite database for that month. Iterate over the
		 * List of All entries, and get the dateCreated, which is converted into
		 * day.
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		private HashMap<String, Integer> findNumberOfEventsPerMonth(int year, int month)
		{
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			// DateFormat dateFormatter2 = new DateFormat();
			//
			// String day = dateFormatter2.format("dd", dateCreated).toString();
			//
			// if (map.containsKey(day))
			// {
			// Integer val = (Integer) map.get(day) + 1;
			// map.put(day, val);
			// }
			// else
			// {
			// map.put(day, 1);
			// }
			return map;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row = convertView;
			if (row == null)
			{
				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.element_calendar_day_gridcell, parent, false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING

			// Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];
			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null))
			{
				if (eventsPerMonthMap.containsKey(theday))
				{
					num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
					Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
					num_events_per_day.setText(numEvents.toString());
				}
			}

			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);
			// Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-" +
			// theyear);

			if (day_color[1].equals("GREY"))
			{
				gridcell.setTextColor(Color.LTGRAY);
			}
			if (day_color[1].equals("WHITE"))
			{
				gridcell.setTextColor(Color.WHITE);
			}
			if (day_color[1].equals("BLUE"))
			{
				gridcell.setTextColor(getContext().getResources().getColor(R.color.static_text_color));
			}
			return row;
		}

		@Override
		public void onClick(View view)
		{
			String date_month_year = (String) view.getTag();
			selectedDayMonthYearButton.setText(date_month_year);

			try
			{
				Date parsedDate = dateFormatter.parse(date_month_year);
				selectedDate = parsedDate.getDate();
				selectedMonth = parsedDate.getMonth();
				selectedYear = parsedDate.getYear() + 1900;
				dismiss();
				// Log.d(tag, "Parsed Date: " + parsedDate.toString());

			} catch (ParseException e)
			{
				//CacheManager.ErrorLog(e);
			}
		}

		public boolean IsSelectedDate(int date, int month, int year)
		{
			return (selectedDate == date) && (selectedMonth == month) && (selectedYear == year);
		}

		public int getCurrentDayOfMonth()
		{
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth)
		{
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay)
		{
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay()
		{
			return currentWeekDay;
		}

	}
	public DBKLCalendarPickerDialog(Context context, int theme)
	{
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	protected DBKLCalendarPickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
	{
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public DBKLCalendarPickerDialog(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DBKLCalendarPickerDialog(Context context, int selectedyear, int selectedmonth, int selectedday)
	{
		super(context);
		selectedMonth = selectedmonth;
		selectedYear = selectedyear;
		selectedDate = selectedday;
		// TODO Auto-generated constructor stub
	}
}
