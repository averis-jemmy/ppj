package tcubes.dbkl.summons;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class DBKLSpinnerAdapter  implements SpinnerAdapter, ListAdapter
{

	/**
	 * The internal data (the ArrayList with the Objects).
	 */
	ArrayList<BaseEntity> m_data = new ArrayList<BaseEntity>();
	boolean codeDisplay = false;
	
	
	/**
	 * Instantiates a new DBKL spiner adapter.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param data
	 *            the data
	 */
	public <T> DBKLSpinnerAdapter(ArrayList<T> data)
	{
		m_data.add(new BaseEntity("", "Pilih"));

		for (T t : data)
		{
			m_data.add((BaseEntity) t);
		}
	}
	
	public <T> DBKLSpinnerAdapter(ArrayList<T> data, int emptyBox)
	{
		if(emptyBox == 1)
			m_data.add(new BaseEntity("", "Pilih"));

		for (T t : data)
		{
			m_data.add((BaseEntity) t);
		}
	}

	public DBKLSpinnerAdapter(ArrayList<BaseEntity> data, boolean isCodeDisplay)
	{
		m_data = data;
		codeDisplay = isCodeDisplay;
	}

	public DBKLSpinnerAdapter(ArrayList<BaseEntity> arrayList, String abc)
	{
		m_data = arrayList;
	}	

	/**
	 * Returns the Size of the ArrayList.
	 * 
	 * @return the count
	 */
	@Override
	public int getCount()
	{
		return m_data.size();

	}

	/**
	 * The Views which are shown in when the arrow is clicked (In this case, I
	 * used the same as for the "getView"-method.
	 * 
	 * @param position
	 *            the position
	 * @param convertView
	 *            the convert view
	 * @param parent
	 *            the parent
	 * @return the drop down view
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			LayoutInflater vi = (LayoutInflater) CacheManager.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);
			convertView.setBackgroundColor(Color.rgb(178, 235, 254));
			
		}

		// TextView textView = (TextView)
		// convertView.findViewById(R.id.tvSpinner2);
		CheckedTextView checktextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
		checktextView.setTextColor(Color.BLACK);

		if (!codeDisplay)
		{
			checktextView.setText(m_data.get(position).Text);
		} else
		{
			checktextView.setText(m_data.get(position).Code);
		}
		return convertView;
	}

	/**
	 * Returns one Element of the ArrayList at the specified position.
	 * 
	 * @param position
	 *            the position
	 * @return the item
	 */
	@Override
	public Object getItem(int position)
	{
		return m_data.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position)
	{
		return android.R.layout.simple_spinner_item;
	}

	/**
	 * Returns the View that is shown when a element was selected.
	 * 
	 * @param position
	 *            the position
	 * @param convertView
	 *            the convert view
	 * @param parent
	 *            the parent
	 * @return the view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			LayoutInflater vi = (LayoutInflater)CacheManager.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(android.R.layout.simple_spinner_item, null);
		}
		TextView textView = (TextView)
				convertView.findViewById(android.R.id.text1);
		textView.setTextColor(Color.BLACK);		
		textView.setTextSize(15f);
		textView.setSingleLine(true);
		if (!codeDisplay)
		{
			textView.setText(m_data.get(position).Text);
		} else
		{
			textView.setText(m_data.get(position).Code);
		}
		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer)
	{
		// TODO Auto-generated method stu
		return;
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer)
	{
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public boolean hasStableIds()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEmpty()
	{
		// TODO Auto-generated method stub
		return m_data.size() == 0;
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled(int position)
	{
		// TODO Auto-generated method stub
		return true;
	}
}
