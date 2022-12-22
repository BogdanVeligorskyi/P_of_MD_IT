package ua.cn.cpnu.pmp_rgr;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ProcessingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        String url = getIntent().getStringExtra("URL");
        String action = getIntent().getStringExtra("Action");

        Log.d("action ", action);
        Log.d("url ", url);
        ImageView iv = findViewById(R.id.image_content_processed);

        if (action.equalsIgnoreCase("Circle image")) {
            GlideApp.with(this)
                    .load(url)
                    .circleCrop()
                    .into(iv);
        } else if (action.equalsIgnoreCase("Crop image")) {
            GlideApp.with(this)
                    .load(url)
                    .centerCrop()
                    .into(iv);
        } else if (action.equalsIgnoreCase("Blur image")) {
            GlideApp.with(this)
                    .load(url)
                    //.transform(new BlurMaskFilter(25))
                    .into(iv);
        } else if (action.equalsIgnoreCase("Resize image")) {
            GlideApp.with(this)
                    .load(url)
                    .override(300, 200)
                    .into(iv);
        } else {
            //iv.setBackgroundColor(000000);
        }

        Button bBack = findViewById(R.id.back);
        bBack.setOnClickListener(view -> {
            finish();
        }

        );
    }
}
