package com.rssj.androidnote.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rssj.androidnote.R;

public class RoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate delegate = AppCompatDelegate.create(this, this);
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(this), new LayoutInflater.Factory2() {

            @Nullable
            @Override
            public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                View view =  delegate.createView(parent, name, context, attrs);
                if (view instanceof TextView) {
                    ImageView iv = new ImageView(context);
                    iv.setBackgroundResource(R.drawable.ic_launcher_background);
                    return iv;
                }
                return view;
            }

            @Nullable
            @Override
            public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                View view =  delegate.createView(null, name, context, attrs);
                return view;
            }

        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);
    }
}