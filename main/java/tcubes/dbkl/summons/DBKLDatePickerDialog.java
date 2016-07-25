package tcubes.dbkl.summons;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.WindowManager;

public class DBKLDatePickerDialog extends DatePickerDialog
{
	public DBKLDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth)
	{
		super(context, callBack, year, monthOfYear, dayOfMonth);
		// TODO Auto-generated constructor stub
	}

	public DBKLDatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth)
	{
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
		// TODO Auto-generated constructor stub
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#onAttachedToWindow()
	 */
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

}
