package id.zenmorf.com.ppjhandheld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
	private static final int TAKE_PICTURE = 1;
	private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_image);

		RefreshData();
		Button btn_continue = (Button) findViewById(R.id.btn_ok);
		btn_continue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		Button btn_capture = (Button) findViewById(R.id.btn_capture);
		btn_capture.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				File dir = new File("/mnt/sdcard/CustomImageDir");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				if (CheckMaximumPicture()) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File photo = new File(dir, "Pic" + CacheManager.imageIndex + ".jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photo));
					imageUri = Uri.fromFile(photo);
					startActivityForResult(intent, TAKE_PICTURE);
				}
			}
		});

		Button btn_delete = (Button) findViewById(R.id.btn_delete);
		btn_delete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Spinner spinner_image = (Spinner) findViewById(R.id.spinner_images);
				if (spinner_image.getSelectedItemPosition() > 0) {
					RefreshData();
				}
			}
		});
	}

	private void RefreshData()
	{
		List<String> list = new ArrayList<String>();
		for(String item : CacheManager.SummonIssuanceInfo.ImageLocation) {
			list.add(item);
		}
		Spinner spinner_image = (Spinner) findViewById(R.id.spinner_images);
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
					Uri imgUri = Uri.parse(parent.getSelectedItem().toString());
					img.setImageURI(imgUri);
					img.setImageBitmap(decodeFile(parent.getSelectedItem().toString()));
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
	
	private boolean CheckMaximumPicture()
	{
		try {
			if (GetNumberOfImage() == 3) {
				CustomAlertDialog.Show(ImageActivity.this, "GAMBAR", "Hanya " + GetNumberOfImage() + " gambar dibenarkan. Sila padam yang tidak berkenan.", 0);
				return false;
			}
		}
		catch (Exception ex) {
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
			while(o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale*=2;

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(path), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}

	private int GetNumberOfImage()
	{
		return CacheManager.SummonIssuanceInfo.ImageLocation.size();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case TAKE_PICTURE:
				if (resultCode == Activity.RESULT_OK) {
					Uri selectedImage = imageUri;
					CacheManager.SummonIssuanceInfo.ImageLocation.add(selectedImage.toString());
					CacheManager.imageIndex++;
					RefreshData();
				}
		}
	}

	@Override
	protected void onDestroy()
	{
	    super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
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

	@Override
	public void onResume()
	{
		super.onResume();
	}
}
