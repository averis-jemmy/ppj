package com.test.averis.testapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        checkExternalStoragePermissions();

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
                    try {
                        photo.createNewFile();
                        photo.setReadable(true);
                        photo.setWritable(true);
                    } catch (Exception e) {

                    }
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
                    try {
                        Uri imgUri = Uri.parse(spinner_image.getSelectedItem().toString());
                        File file = new File(imgUri.getPath());
                        if (file.exists()) {
                            if(file.getAbsoluteFile().delete()) {
                                if (!file.exists()) {
                                    CacheManager.ImageLocation.remove(spinner_image.getSelectedItem().toString());
                                    RefreshData();
                                    callBroadCast();
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    int REQUEST_STORAGE = 1;
    private void checkExternalStoragePermissions() {
        if (hasStoragePermissionGranted()) {
            //You can do what whatever you want to do as permission is granted
        } else {
            requestExternalStoragePermission();
        }
    }

    public boolean hasStoragePermissionGranted(){
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //User allow from permission dialog
                //You can do what whatever you want to do as permission is granted
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //User has deny from permission dialog
                Snackbar.make(mainLayout, "Please enable storage permission", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_STORAGE);
                            }
                        })
                        .show();
            } else {
                // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
                Snackbar.make(mainLayout, "Please enable permission from settings", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    private void RefreshData()
    {
        List<String> list = new ArrayList<String>();
        for(String item : CacheManager.ImageLocation) {
            list.add(item);
        }

        Spinner spinner_image = (Spinner) findViewById(R.id.spinner_images);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.insert("--Sila Pilih--", 0);
        spinner_image.setAdapter(adapter);
        spinner_image.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                // TODO Auto-generated method stub
                ImageView img = (ImageView) findViewById(R.id.img_view);
                if(pos > 0)
                {
                    Uri imgUri = Uri.parse(parent.getSelectedItem().toString());
                    File file = new File(imgUri.getPath());
                    if(file.exists()){
                        BitmapFactory.Options options;
                        Bitmap bitmap = null;
                        try {
                            options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                        } catch (Exception ex) {
                        }
                        img.setImageBitmap(bitmap);
                    }
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
            if (GetNumberOfImage() >= 3) {
                //AlertMessage(this, "GAMBAR", "Hanya " + GetNumberOfImage() + " gambar dibenarkan. Sila padam yang tidak berkenan.", 0);
                return false;
            }
        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }

    public void AlertMessage(final Context context, String title, String message, int type)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        if( type == 0)
        {
            builder.setPositiveButton("OK", null);
        }
        builder.show();
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
        int i =  CacheManager.ImageLocation.size();
        return i;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    File image = new File(imageUri.getPath());
                    if(image.exists() && image.length() > 0) {
                        CacheManager.ImageLocation.add(selectedImage.toString());
                        CacheManager.imageIndex++;
                        RefreshData();
                    }
                }
        }
    }
}
