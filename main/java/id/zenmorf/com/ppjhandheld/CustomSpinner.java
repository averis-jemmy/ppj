package id.zenmorf.com.ppjhandheld;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class CustomSpinner extends Spinner
{

	private AlertDialog mPopup;

	public CustomSpinner(Context context)
	{
		super(context);
	}

	public CustomSpinner(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public CustomSpinner(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();

	}
	@Override
	public void setAdapter(SpinnerAdapter adapter)
	{
		//TODO Auto-generated method stub
		super.setAdapter(adapter);
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();

		if (mPopup != null && mPopup.isShowing())
		{
			mPopup.dismiss();
			mPopup = null;
		}
	}

	// when clicked alertDialog is made
	@Override
	public boolean performClick()
	{
		Context context = getContext();

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		CharSequence prompt = getPrompt();
		if (prompt != null)
		{
			builder.setTitle(prompt);
		}
		mPopup =
				builder.setSingleChoiceItems(new DropDownAdapter(getAdapter()),
						getSelectedItemPosition(), this).show();

		mPopup.setOnKeyListener(new MyOnKeyListener());
		/*if (CacheManager.IsLock)
		{
			mPopup.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		}*/

		return true;
	}

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

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		setSelection(which);
		dialog.dismiss();
		mPopup = null;
	}

	@Override
	public void setSelection(int position)
	{
		// TODO Auto-generated method stub
		super.setSelection(position);
	}

	public void SetSelection(String code)
	{

		SpinnerAdapter obj = getAdapter();
		int count = obj.getCount();
		for (int i = count - 1; i >= 0; i--)
		{
			BaseEntity item = (BaseEntity) obj.getItem(i);
			if (item.Code.equalsIgnoreCase(code))
			{
				setSelection(i);
				break;
			}
			if (item.Text.equalsIgnoreCase(code))
			{
				setSelection(i);
				break;
			}
		}
	}

	/*
	 * <p>Wrapper class for an Adapter. Transforms the embedded Adapter instance
	 * into a ListAdapter.</p>
	 */
	private static class DropDownAdapter implements ListAdapter, SpinnerAdapter
	{
		private SpinnerAdapter mAdapter;

		/**
		 * <p>
		 * Creates a new ListAddapter wrapper for the specified adapter.
		 * </p>
		 * 
		 * @param adapter
		 *            the Adapter to transform into a ListAdapter
		 */
		public DropDownAdapter(SpinnerAdapter adapter)
		{
			this.mAdapter = adapter;
		}

		@Override
		public int getCount()
		{
			return mAdapter == null ? 0 : mAdapter.getCount();
		}

		@Override
		public Object getItem(int position)
		{
			return mAdapter == null ? null : mAdapter.getItem(position);
		}

		@Override
		public long getItemId(int position)
		{
			return mAdapter == null ? -1 : mAdapter.getItemId(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			return getDropDownView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup
				parent)
		{
			return mAdapter == null ? null :
					mAdapter.getDropDownView(position, convertView, parent);
		}

		@Override
		public boolean hasStableIds()
		{
			return mAdapter != null && mAdapter.hasStableIds();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer)
		{
			if (mAdapter != null)
			{
				mAdapter.registerDataSetObserver(observer);
			}
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer)
		{
			if (mAdapter != null)
			{
				mAdapter.unregisterDataSetObserver(observer);
			}
		}

		/**
		 * <p>
		 * Always returns false.
		 * </p>
		 * 
		 * @return false
		 */
		@Override
		public boolean areAllItemsEnabled()
		{
			return true;
		}

		/**
		 * <p>
		 * Always returns false.
		 * </p>
		 * 
		 * @return false
		 */
		@Override
		public boolean isEnabled(int position)
		{
			return true;
		}

		@Override
		public int getItemViewType(int position)
		{
			return 0;
		}

		@Override
		public int getViewTypeCount()
		{
			return 1;
		}

		@Override
		public boolean isEmpty()
		{
			return getCount() == 0;
		}

	}

	class MyOnKeyListener implements android.content.DialogInterface.OnKeyListener
	{
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode,
				KeyEvent event)
		{
			if (keyCode == KeyEvent.KEYCODE_SEARCH || (event.getFlags() == KeyEvent.FLAG_LONG_PRESS))
			{
				return true;
			}
			return false;
		}
	}
}
