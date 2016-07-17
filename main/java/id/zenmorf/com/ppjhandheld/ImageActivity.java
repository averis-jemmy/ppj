package id.zenmorf.com.ppjhandheld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class ImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_image);
		

		Button btn_continue = (Button) findViewById(R.id.btn_ok);
		btn_continue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	CacheManager.SummonIssuanceInfo.ImageLocation = new String[5];
            	
            	Spinner spinner_image = (Spinner) findViewById(R.id.spiner_images);
            	if(spinner_image.getCount() > 1)
            	{
            		for(int i = 1; i < spinner_image.getCount() ; i++)
            		{
	            		if(i<5 && spinner_image.getItemAtPosition(i) != null)
	            			CacheManager.SummonIssuanceInfo.ImageLocation[i] = getImageData(spinner_image.getItemAtPosition(i).toString());
            		}
            	}
            	
            	Intent i = new Intent(ImageActivity.this, NoticesActivity.class);
        		startActivity(i);
        		finish();
        		
            }
		});
		
		Button btn_capture = (Button) findViewById(R.id.btn_capture);
		btn_capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(CheckMaximumPicture())
            	{
	            	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	                intent.putExtra(MediaStore.EXTRA_OUTPUT, "");
	                //intent.putExtra(MediaStore.Images.Media.WIDTH, "480");
	                //intent.putExtra(MediaStore.Images.Media.HEIGHT, "640");
	                //CacheManager.onAppPause(getApplicationContext());
					//CacheManager.InstallWaitingTime = 20;
	                startActivityForResult(intent, 0);
            	}
            }
		});
	    
	    Button btn_delete = (Button) findViewById(R.id.btn_delete);
	    btn_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Spinner spinner_image = (Spinner) findViewById(R.id.spiner_images);
            	if(spinner_image.getSelectedItemPosition() > 0)
            	{
	            	Uri imgUri = Uri.parse(getImageData(spinner_image.getSelectedItem().toString()));
	            	File file = new File(imgUri.toString());
	            	file.delete();
	            	getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.DATA + "=?", new String[] {getImageData(spinner_image.getSelectedItem().toString())});
	            	RefreshData();
            	}
            }
		});
		
	}
	
	private boolean CheckMaximumPicture()
	{
		if(GetNumberOfImage() == 3)
			{
				CustomAlertDialog.Show(ImageActivity.this, "GAMBAR", "Hanya " + GetNumberOfImage() + " gambar dibenarkan. Sila padam yang tidak berkenan.", 0);
				return false;
			}
		return true;
	}

	//decodes image and scales it to reduce memory consumption
		private Bitmap decodeFile(String path){
		    try {
		        //Decode image size
		        BitmapFactory.Options o = new BitmapFactory.Options();
		        o.inJustDecodeBounds = true;
		        BitmapFactory.decodeStream(new FileInputStream(path),null,o);

		        //The new size we want to scale to
		        final int REQUIRED_SIZE=120;

		        //Find the correct scale value. It should be the power of 2.
		        int scale=1;
		        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
		            scale*=2;

		        //Decode with inSampleSize
		        BitmapFactory.Options o2 = new BitmapFactory.Options();
		        o2.inSampleSize=scale;
		        return BitmapFactory.decodeStream(new FileInputStream(path), null, o2);
		    } catch (FileNotFoundException e) {}
		    return null;
		}
				
		@SuppressWarnings("deprecation")
		private void ClearData()
		{
			if(CacheManager.IsNewSummonsCamera)
			{
				String[] projection = new String[]{
			            MediaStore.Images.Media._ID,
			            MediaStore.Images.Media.DATA,
			            MediaStore.Images.Media.DISPLAY_NAME
			    };
		
			    // Get the base URI for the People table in the Contacts content provider.
			    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		
			    // Make the query.
			    Cursor cur = managedQuery(images,
			            projection, // Which columns to return
			            "",         // Which rows to return (all rows)
			            null,       // Selection arguments (none)
			            ""          // Ordering
			            );
		
				if (cur != null)
				{
				    if (cur.moveToFirst()) {
				        int dataColumn = cur.getColumnIndex(
				                MediaStore.Images.Media.DATA);
				        do {
				        	Uri imgUri = Uri.parse(cur.getString(dataColumn));
				        	//if(!imgUri.toString().contains("Photo"))
				        	//{
				            	File file = new File(imgUri.toString());
				            	file.delete();
				            	getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.DATA + "=?", new String[] {cur.getString(dataColumn)});
				        	//}
				        } while (cur.moveToNext());
				    }
				}
				CacheManager.IsNewSummonsCamera = false;
			}
		}
		
		private String getImageData(String DisplayName)
		{
			String[] projection = new String[]{
		            MediaStore.Images.Media._ID,
		            MediaStore.Images.Media.DATA,
		            MediaStore.Images.Media.DISPLAY_NAME
		    };

		    // Get the base URI for the People table in the Contacts content provider.
		    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		    // Make the query.
		    Cursor cur = managedQuery(images,
		            projection, // Which columns to return
		            "",         // Which rows to return (all rows)
		            null,       // Selection arguments (none)
		            ""          // Ordering
		            );

			String data = "";

			if (cur != null)
			{
			    if (cur.moveToFirst()) {
			        int nameColumn = cur.getColumnIndex(
			            MediaStore.Images.Media.DISPLAY_NAME);
			        int dataColumn = cur.getColumnIndex(
				            MediaStore.Images.Media.DATA);
			        do {
			        	if(cur.getString(nameColumn).equalsIgnoreCase(DisplayName))
			        		data = cur.getString(dataColumn);
			        } while (cur.moveToNext());
			    }
			}
			
			return data;
		}
		
		private int GetNumberOfImage()
		{
			String[] projection = new String[]{
		            MediaStore.Images.Media._ID,
		            MediaStore.Images.Media.DATA,
		            MediaStore.Images.Media.DISPLAY_NAME
		    };

		    // Get the base URI for the People table in the Contacts content provider.
		    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		    // Make the query.
		    Cursor cur = managedQuery(images,
		            projection, // Which columns to return
		            "",         // Which rows to return (all rows)
		            null,       // Selection arguments (none)
		            ""          // Ordering
		            );

			List<String> list = new ArrayList<String>();

			if (cur != null)
			{
			    if (cur.moveToFirst()) {
			        int nameColumn = cur.getColumnIndex(
			            MediaStore.Images.Media.DISPLAY_NAME);
			        int dataColumn = cur.getColumnIndex(
			                MediaStore.Images.Media.DATA);
			        do {
			        	//if(!cur.getString(dataColumn).contains("Photo"))
			        	//{
			        		list.add(cur.getString(nameColumn));
			        	//}
			        } while (cur.moveToNext());
			    }
			}
			
			return list.size();
		}
		
		@SuppressWarnings("deprecation")
		private void RefreshData()
		{
			String[] projection = new String[]{
		            MediaStore.Images.Media._ID,
		            MediaStore.Images.Media.DATA,
		            MediaStore.Images.Media.DISPLAY_NAME
		    };

		    // Get the base URI for the People table in the Contacts content provider.
		    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		    // Make the query.
		    Cursor cur = managedQuery(images,
		            projection, // Which columns to return
		            "",         // Which rows to return (all rows)
		            null,       // Selection arguments (none)
		            ""          // Ordering
		            );

			List<String> list = new ArrayList<String>();

			if (cur != null)
			{
			    if (cur.moveToFirst()) {
			        int nameColumn = cur.getColumnIndex(
			            MediaStore.Images.Media.DISPLAY_NAME);
			        int dataColumn = cur.getColumnIndex(
			                MediaStore.Images.Media.DATA);
			        do {
			        	//if(!cur.getString(dataColumn).contains("Photo"))
			        	//{
			        		list.add(cur.getString(nameColumn));
			        	//}
			        } while (cur.moveToNext());
			    }
			}
			//cur.close();
			
			Spinner spinner_image = (Spinner) findViewById(R.id.spiner_images);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, list);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 	adapter.insert("--Sila Pilih--", 0);
		    spinner_image.setAdapter(adapter);
		    spinner_image.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
					// TODO Auto-generated method stub
					ImageView img = (ImageView) findViewById(R.id.img_view);
					try
					{
						if(img.getDrawable() != null)
						{
							((BitmapDrawable)img.getDrawable()).getBitmap().recycle();
						}
					}
					catch(Exception e)
					{
						
					}
					if(pos > 0)
					{
						Uri imgUri = Uri.parse(getImageData(parent.getSelectedItem().toString()));
						//img.setImageURI(imgUri);
						img.setImageBitmap(decodeFile(getImageData(parent.getSelectedItem().toString())));
					}
					else
						img.setImageBitmap(null);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0)
				{
					// TODO Auto-generated method stub

				}
			});
		}
	@Override
	protected void onDestroy()
	{
	    super.onDestroy();
	    ImageView img = (ImageView) findViewById(R.id.img_view);
	    img.setImageBitmap(null);
	}		
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart()
	{
		ClearData();
		RefreshData();
		// TODO Auto-generated method stub
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		super.onStart();
	}
	@Override
	public void onBackPressed()
	{
		return;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_image, menu);
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

	private Window w;
	@Override
	public void onResume()
	{
		w = this.getWindow();
	    w.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
	    w.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	    w.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
	    
		CacheManager.LockKeygaurd(getApplicationContext());
		CacheManager.IsAppOnRunning = true;
		super.onResume();
	}
}
