package com.rssj.androidnote.ui.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.rssj.androidnote.IMyAidlInterface;
import com.rssj.androidnote.R;
import com.rssj.androidnote.aidl.Book;
import com.rssj.androidnote.aidl.MyAIDLService;
import com.rssj.androidnote.aidl.MyMessagerService;
import com.rssj.androidnote.aop.AopActivity;
import com.rssj.androidnote.apt.AptActivity;

import static android.content.Context.BIND_AUTO_CREATE;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_aidl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        view.findViewById(R.id.btn_apt).setOnClickListener(l -> {
            Intent intent = new Intent(getContext(), AptActivity.class);
            intent.putExtra("title", "你好APT");
            intent.putExtra("int", 20);
            intent.putExtra("double", 1.5f);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_aop).setOnClickListener(l -> {
            Intent intent = new Intent(getContext(), AopActivity.class);
            startActivity(intent);
        });
    }
}