package com.rssj.androidnote.aidl;

import android.content.ComponentName;
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

import static android.content.Context.BIND_AUTO_CREATE;

public class SecondFragment extends Fragment {

    IMyAidlInterface myAidlInterface;

    boolean isConn = false;
    boolean isCoon1 = false;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 根据实际情况返回 IBinder 的本地对象或其代理对象
            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            Log.i("AIDL", "服务连接成功 ---- " + android.os.Process.myPid());
            try {
//                Log.i("AIDL", myAidlInterface.getString());
//                Log.i("AIDL", myAidlInterface.add(1, 2) + "");
//                myAidlInterface.seyHello();
                Book book = new Book();
                book.setId(1);
                book.setName("红楼梦");
                Log.i("AIDL", book.toString());
                myAidlInterface.inBook(book);
                Log.i("AIDL", book.toString());

                book.setId(1);
                book.setName("红楼梦");
                Log.i("AIDL", book.toString());
                myAidlInterface.outBook(book);
                Log.i("AIDL", book.toString());

                book.setId(1);
                book.setName("红楼梦");
                Log.i("AIDL", book.toString());
                myAidlInterface.inoutBook(book);
                Log.i("AIDL", book.toString());

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Service 意外中断时调用
            Log.i("AIDL", "断开链接 ---- " + android.os.Process.myPid());
        }
    };

    private GetRelyHandler mGetRelyHandler = new GetRelyHandler();

    private Messenger mRelyMessenger = new Messenger(mGetRelyHandler);

    private ServiceConnection conn1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取服务端关联的Messenger对象
            Messenger mService = new Messenger(service);
            //创建Message对象
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("client", "今天出去玩吗？");
            message.setData(bundle);
            //在message中添加一个回复mRelyMessenger对象
            message.replyTo = mRelyMessenger;
            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigateUp();
            }
        });

        view.findViewById(R.id.textview_second).setOnClickListener(l -> {
            Intent intent = new Intent(requireContext(), MyMessagerService.class);
            requireActivity().bindService(intent, conn1, BIND_AUTO_CREATE);
            isCoon1 = true;

//            Log.i("AIDL", "准备链接 ---- " + android.os.Process.myPid());
//            Intent intent = new Intent("com.rssj.aidl.TEST");
//            String packName = requireContext().getPackageName();
//            Log.i("AIDL", "packName ---- " + packName);
//            intent.setPackage("com.rssj.androidnote");
////                Intent intent = new Intent(requireContext(), MyAIDLService.class);
//            requireActivity().bindService(intent, conn, BIND_AUTO_CREATE);
//            isConn = true;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isConn) {
            requireActivity().unbindService(conn);
        }
        if (isCoon1) {
            requireActivity().unbindService(conn1);
        }
    }

    public static class GetRelyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String serviceMsg = bundle.getString("service");
            Log.i("AIDL", "来自服务端的回复：" + serviceMsg);

        }
    }
}