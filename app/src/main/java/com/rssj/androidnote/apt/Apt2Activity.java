package com.rssj.androidnote.apt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chlhrssj.annotation.BindClick;
import com.chlhrssj.annotation.BindExtra;
import com.chlhrssj.annotation.BindView;
import com.rssj.androidnote.R;

public class Apt2Activity extends AppCompatActivity {

    @BindView(R.id.tv_desc)
    public TextView tvDesc;

    @BindExtra("desc")
    public String desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt2);

        BindUtil.bind(this);

        tvDesc.setText(desc);
    }

    @BindClick({R.id.btn_back, R.id.tv1, R.id.tv2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}