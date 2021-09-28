package com.rssj.androidnote.apt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

//    @BindView(R.id.toolbar)
//    public Toolbar toolbar;
    @BindView(R.id.tv1)
    public TextView tv1;
    @BindView(R.id.tv2)
    public TextView tv2;

    @BindExtra("title")
    public String title;
    @BindExtra("int")
    public int int1;
    @BindExtra("double")
    public double double1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt);

        BindUtil.bind(this);

        tv1.setText("" + int1);
        tv2.setText("" + double1);
    }

    @BindClick({R.id.tv1, R.id.tv2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv1:
                Toast.makeText(this, "TV1被点击!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv2:
                Intent intent = new Intent(this, Apt2Activity.class);
                intent.putExtra("desc", "测试测试戛然而止");
                startActivity(intent);
                break;
        }
    }

}