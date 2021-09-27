package com.rssj.androidnote.apt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.chlhrssj.annotation.BindClick;
import com.chlhrssj.annotation.BindExtra;
import com.chlhrssj.annotation.BindView;
import com.rssj.androidnote.R;

public class AptActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;

    @BindExtra("title")
    String title;
    @BindExtra("int")
    int int1;
    @BindExtra("double")
    double double1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt);

        
    }

    @BindClick({R.id.toolbar, R.id.tv1, R.id.tv2})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                finish();
                break;
            case R.id.tv1:
                Toast.makeText(this, "TV1被点击!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv2:
                Toast.makeText(this, "TV2被点击!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}