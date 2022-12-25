package ua.cn.cpnu.pmp_rgr;

import androidx.annotation.NonNull;
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

// main activity class, where current image is shown
public class MainActivity extends AppCompatActivity {

    private final Activity activity = MainActivity.this;
    private Button bCrop;
    private Button bCircle;
    private Button bResize;
    private Button bBlur;
    private ImageView iv;
    private TextView tv;
    private final String absPath = Environment.
            getExternalStorageDirectory().getAbsolutePath();
    private int current_photo;
    public boolean isOrientationChanged;

    // all directories where images may be located
    private final String[] nameArray = {
            absPath + "/Pictures",
            absPath + "/DCIM/Camera",
            absPath + "/DCIM/Screenshots"
            };
    // path to images directory and array of images
    private String path;
    private String[] uriArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if activity is started for the first time
        if (savedInstanceState != null) {
            current_photo = savedInstanceState.getInt("CURRENT_PHOTO");
            path = savedInstanceState.getString("PATH");
            isOrientationChanged = true;

            // if activity is resumed
        } else {
            current_photo = 0;
            isOrientationChanged = false;
            path = nameArray[0];
        }
        setupSpinner();
        uriArr = readAllImages();
        setupViewSwitcher();
        setupImage();
        loadPhoto(current_photo);
        setupButtons();

    }

    // disable or enable buttons
    private void areEnabledButtons(boolean flag) {
        if (bCrop != null && bResize != null &&
                bBlur != null && bCircle != null) {
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

    // setup slider opportunity
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
    private void loadPhoto(int current_photo) {
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

        // check for permission
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // find images among other files
        File directory = new File(path);
        File[] files = directory.listFiles(file ->
                file.getPath().endsWith(".jpg") ||
                file.getPath().endsWith(".png") ||
                file.getPath().endsWith(".jpeg"));

        if (files == null) {
            return null;
        }

        // load images
        Log.d("Files", "Size: "+ files.length);
        String[] imagesArr = new String[files.length];
        for (int i = 0; i < files.length; i++) {
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
        loadPhoto(current_photo);
    }

    // to previous photo
    private void prevPhoto(String[] arr) {
        current_photo--;
        if (current_photo < 0) {
            current_photo = arr.length-1;
        }
        loadPhoto(current_photo);
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // if directory from expanded list was chosen
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int position, long l) {
                Toast.makeText(getApplicationContext(), nameArray[position],
                        Toast.LENGTH_LONG).show();
                path = nameArray[position];
                if (!isOrientationChanged) {
                    Log.d("state changed", "no");
                    uriArr = readAllImages();
                    current_photo = 0;
                }
                if (uriArr.length > 0) {
                    loadPhoto(current_photo);
                } else {
                    tv.setText("No images in this directory!");
                    tv.setTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(),
                            "No images in this directory!",
                            Toast.LENGTH_LONG).show();
                    areEnabledButtons(false);
                }
                isOrientationChanged = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });

    }

    // saving current state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENT_PHOTO", current_photo);
        outState.putString("PATH", path);
    }

}