package ua.cn.cpnu.pmp_rgr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private int current_photo = 0;
    private final Activity activity = MainActivity.this;
    private ImageView iv;
    private TextView tv;
    private final String absPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Button bCrop;
    private Button bCircle;
    private Button bResize;
    private Button bBlur;

    private final String[] nameArray = {
            absPath + "/Pictures",
            absPath + "/DCIM/Camera",
            absPath + "/DCIM/Screenshots"
            };
    private String path = nameArray[0];
    private String[] uriArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSpinner();
        uriArr = readAllImages();
        setupViewSwitcher();
        setupImage();
        loadPhoto();
        setupButtons();

    }

    // disable or enable buttons
    private void areEnabledButtons(boolean flag) {
        if (bCrop != null && bResize != null && bBlur != null && bCircle != null) {
            bCrop.setEnabled(flag);
            bResize.setEnabled(flag);
            bBlur.setEnabled(flag);
            bCircle.setEnabled(flag);
        }
    }

    // setup TextView and ImageView
    private void setupImage() {
        iv = findViewById(R.id.image_content);
        tv = findViewById(R.id.name_of_file);
    }

    // setup buttons
    private void setupButtons() {
        bCrop = findViewById(R.id.crop_image);
        bCrop.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,
                    ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bCrop.getText());
            startActivity(intent);
        });

        bCircle = findViewById(R.id.circle_image);
        bCircle.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,
                    ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bCircle.getText());
            startActivity(intent);
        });

        bResize = findViewById(R.id.resize_image);
        bResize.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,
                    ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bResize.getText());
            startActivity(intent);
        });

        bBlur = findViewById(R.id.blur_image);
        bBlur.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,
                    ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bBlur.getText());
            startActivity(intent);
        });
    }

    // setup slider possibility
    private void setupViewSwitcher() {
        ViewSwitcher vs = findViewById(R.id.view_switcher);
        vs.setOnTouchListener(new OnSwipeTouchListener(activity) {
            @Override
            public void onSwipeLeft() {
                nextPhoto(uriArr);
            }

            @Override
            public void onSwipeRight() {
                prevPhoto(uriArr);
            }
        });
    }

    // load photo into ImageView component
    private void loadPhoto() {
        int lastIndex = uriArr[current_photo].lastIndexOf("/");
        tv.setText(uriArr[current_photo].substring(lastIndex+1));
        tv.setTextColor(Color.WHITE);

        GlideApp.with(activity)
                .load(uriArr[current_photo])
                .into(iv);
        areEnabledButtons(true);
    }

    // read all photos from directory
    private String[] readAllImages() {
        /*Uri contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                cursor.getLong(Integer.parseInt(BaseColumns._ID)));
        String fileOpenMode = "r";
        ParcelFileDescriptor parcelFd = resolver.openFileDescriptor(uri, fileOpenMode);
        if (parcelFd != null) {
            int fd = parcelFd.detachFd();
            // Pass the integer value "fd" into your native code. Remember to call
            // close(2) on the file descriptor when you're done using it.
        }*/
        // check for permission
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        boolean isExternalStorageReadable = android.os.Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED_READ_ONLY);
        Log.d("ExternalStorageReadable", Boolean.toString(isExternalStorageReadable));

        /*final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        //Total number of images
        int count = cursor.getCount();

        //Create an array to store path to all the images
        String[] arrPath = new String[count];

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath[i]= cursor.getString(dataColumnIndex);
            Log.d("arrPath", arrPath[i]);
        }
        // The cursor should be freed up after use with close()
        cursor.close();
        return arrPath;*/

        //path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures";
        //String[] str = Environment.getExternalStorageDirectory().getAbsolutePath()

        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles(file -> file.getPath().endsWith(".jpg") ||
                file.getPath().endsWith(".png") ||
                file.getPath().endsWith(".jpeg"));

        if (files == null) {
            return null;
        }

        Log.d("Files", "Size: "+ files.length);
        String[] imagesArr = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            imagesArr[i] = path + "/" + files[i].getName();
        }
        return imagesArr;
    }

    // to next photo
    private void nextPhoto(String[] arr) {
        current_photo++;
        if (current_photo >= arr.length) {
            current_photo = 0;
        }
        loadPhoto();
    }

    // to previous photo
    private void prevPhoto(String[] arr) {
        current_photo--;
        if (current_photo < 0) {
            current_photo = arr.length-1;
        }
        loadPhoto();
    }

    // setup spinner with number of questions
    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(
                this,
                R.layout.listview_row,
                nameArray
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_dropdown_item_1line);
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }
        assert spinner != null;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // if directory from expanded list was chosen
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getApplicationContext(), nameArray[position],
                        Toast.LENGTH_LONG).show();
                path = nameArray[position];
                uriArr = readAllImages();
                current_photo = 0;
                assert uriArr != null;
                if (uriArr.length > 0) {
                    loadPhoto();
                } else {
                    tv.setText("No images in this directory!");
                    tv.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "No images in this directory!",
                            Toast.LENGTH_LONG).show();
                    areEnabledButtons(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });
    }


}