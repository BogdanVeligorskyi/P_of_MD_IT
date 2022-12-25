package ua.cn.cpnu.pmp_rgr;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// activity that shows processed image by Glide library
public class ProcessingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        // read image and selected operation from MainActivity
        String url = getIntent().getStringExtra("URL");
        String action = getIntent().getStringExtra("Action");

        Log.d("action ", action);
        Log.d("url ", url);
        ImageView iv = findViewById(R.id.image_content_processed);

        // "Circle image" operation
        if (action.equalsIgnoreCase("Circle image")) {
            GlideApp.with(this)
                    .load(url)
                    .circleCrop()
                    .into(iv);

            // "Crop image" operation
        } else if (action.equalsIgnoreCase("Crop image")) {
            GlideApp.with(this)
                    .load(url)
                    .centerCrop()
                    .into(iv);

            // "Blur image" operation
        } else if (action.equalsIgnoreCase("Blur image")) {
            GlideApp.with(this)
                    .load(url)
                    .transform(new GlideBlurTransformation
                            (getApplicationContext()))
                    .into(iv);

            // "Resize image" operation
        } else if (action.equalsIgnoreCase("Resize image")) {
            GlideApp.with(this)
                    .load(url)
                    .override(300, 200)
                    .into(iv);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Operation was not selected",
                    Toast.LENGTH_LONG).show();
        }
    }
}
