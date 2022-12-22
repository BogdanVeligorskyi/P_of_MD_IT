package ua.cn.cpnu.pmp_rgr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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

    private final String[] nameArray = {"/storage/emulated/0/Pictures",
    "/storage/emulated/0/DCIM/Camera",
            "/storage/emulated/0/DCIM/Screenshots"};
    private Spinner spinner;
    private String path = nameArray[0];
    private String[] uriArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        setupSpinner();

        uriArr = readAllImages();
        ViewSwitcher vs = findViewById(R.id.view_switcher);

        iv = findViewById(R.id.image_content);

        tv = findViewById(R.id.name_of_file);
        int lastIndex = uriArr[current_photo].lastIndexOf("/");
        tv.setText(uriArr[current_photo].substring(lastIndex+1));

        /*Animation inAnim = new AlphaAnimation(0, 1);
        inAnim.setDuration(2000);
        Animation outAnim = new AlphaAnimation(1, 0);
        outAnim.setDuration(2000);

        vs.setInAnimation(inAnim);
        vs.setOutAnimation(outAnim);*/

        GlideApp.with(this)
                .load(uriArr[current_photo])
                .into(iv);

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

        Button bCrop = findViewById(R.id.crop_image);
        bCrop.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bCrop.getText());
            startActivity(intent);
        });

        Button bCircle = findViewById(R.id.circle_image);
        bCircle.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bCircle.getText());
            startActivity(intent);
        });

        Button bResize = findViewById(R.id.resize_image);
        bResize.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bResize.getText());
            startActivity(intent);
        });

        Button bBlur = findViewById(R.id.blur_image);
        bBlur.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bBlur.getText());
            startActivity(intent);
        });

    }

    // read all photos from directory
    private String[] readAllImages() {
        // check for permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

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
            Log.d("", arrPath[i]);
        }
        // The cursor should be freed up after use with close()
        cursor.close();*/

        //String path = "/storage/emulated/0/Pictures";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);

        File[] files = directory.listFiles(file -> file.getPath().endsWith(".jpg") ||
                file.getPath().endsWith(".png") ||
                file.getPath().endsWith(".jpeg"));

        if (files == null) {
            tv.setText("No images in this directory!");
            tv.setTextColor(255);
            return null;
        }
        assert files != null;
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
        GlideApp.with(activity)
                .load(arr[current_photo])
                .into(iv);
        int lastIndex = arr[current_photo].lastIndexOf("/");
        tv.setText(arr[current_photo].substring(lastIndex+1));
    }

    // to previous photo
    private void prevPhoto(String[] arr) {
        current_photo--;
        if (current_photo < 0) {
            current_photo = arr.length-1;
        }
        GlideApp.with(activity)
                .load(arr[current_photo])
                .into(iv);
        int lastIndex = arr[current_photo].lastIndexOf("/");
        tv.setText(arr[current_photo].substring(lastIndex+1));
    }

    // setup spinner with number of questions
    private void setupSpinner() {
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

            // if number of questions was selected
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getApplicationContext(), nameArray[position], Toast.LENGTH_LONG).show();
                path = nameArray[position];
                uriArr = readAllImages();
                current_photo = 0;
                if (uriArr.length > 0) {
                    GlideApp.with(activity)
                            .load(uriArr[current_photo])
                            .into(iv);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });
    }


}