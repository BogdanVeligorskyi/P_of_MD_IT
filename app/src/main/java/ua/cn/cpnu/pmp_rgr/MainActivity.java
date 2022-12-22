package ua.cn.cpnu.pmp_rgr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private int current_photo = 0;
    private final Activity activity = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] uriArr = readAllImages();
        ViewSwitcher vs = findViewById(R.id.view_switcher);
        vs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                current_photo++;
                ImageView iv = findViewById(R.id.image_content);
                GlideApp.with(activity)
                        .load(uriArr[current_photo])
                        .into(iv);
            }
        });

        ImageView iv = findViewById(R.id.image_content);
        GlideApp.with(this)
                .load(uriArr[current_photo])
                .into(iv);

        Button bCrop = (Button) findViewById(R.id.crop_image);
        bCrop.setOnClickListener((View.OnClickListener) view -> {
            Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bCrop.getText());
            startActivity(intent);
        });
        Button bCircle = (Button) findViewById(R.id.circle_image);

        bCircle.setOnClickListener((View.OnClickListener) view -> {
            Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bCircle.getText());
            startActivity(intent);
        });

        Button bResize = (Button) findViewById(R.id.resize_image);
        bResize.setOnClickListener((View.OnClickListener) view -> {
            Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bResize.getText());
            startActivity(intent);
        });

        Button bBlur = (Button) findViewById(R.id.blur_image);
        bBlur.setOnClickListener((View.OnClickListener) view -> {
            Intent intent = new Intent(MainActivity.this, ProcessingActivity.class);
            intent.putExtra("URL", uriArr[current_photo]);
            intent.putExtra("Action", bBlur.getText());
            startActivity(intent);
        });


        /*final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);*/
        //Total number of images
        /*int count = cursor.getCount();

        //Create an array to store path to all the images
        String[] arrPath = new String[count];

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath[i]= cursor.getString(dataColumnIndex);
            Log.d("JJJJJJJJJJJJJJJJJ", arrPath[i]);
        }
        // The cursor should be freed up after use with close()
        cursor.close();*/




    }

    private String[] readAllImages() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        String path = "/storage/emulated/0" + "/Pictures";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        String[] imagesArr = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            imagesArr[i] = path + "/" + files[i].getName();
        }
        return imagesArr;
    }

}