package tcubes.dbkl.summons;

import java.util.Calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class DBKLDatePickerField extends TextView 
{
	public DBKLDatePickerField(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}

	public DBKLDatePickerField(Context context)
	{
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	public DBKLDatePickerField(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}
	private void init()
	{
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// new EmmsDatePickerDialog(getContext(),
				// mDateSetListener,
				// mYear, mMonth, mDay).show();
				if(v.isEnabled())
				{
					DBKLCalendarPickerDialog obj = new DBKLCalendarPickerDialog(getContext(), mYear, mMonth, mDay);
					obj.setOnDismissListener(new OnDismissListener() {
	
						@Override
						public void onDismiss(DialogInterface dialog)
						{
							// TODO Auto-generated method stub
							DBKLCalendarPickerDialog obj = (DBKLCalendarPickerDialog) dialog;
							mYear = obj.getSelectedYear();
							mMonth = obj.getSelectMonth();
							mDay = obj.getSelectedDate();
							updateDisplay();
						}
					});
					obj.show();
				}
			}
		});
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// display the current date (this method is below)
		updateDisplay();
	}

	@SuppressWarnings("unused")
	private DBKLDatePickerDialog.OnDateSetListener mDateSetListener =
			new DBKLDatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth)
				{
					mYear = year;
					mMonth = monthOfYear;
					mDay = dayOfMonth;
					updateDisplay();
				}
			};

	private void updateDisplay()
	{
		setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mDay).append("/").append(mMonth + 1).append("/")

				.append(mYear).append(" "));
	}

	public void updateDisplay(int year, int month, int date)
	{
		mYear = year;
		mMonth = month;
		mDay = date;
		updateDisplay();
	}

	public int mYear;
	public int mMonth;
	public int mDay;
}
