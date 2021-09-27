package com.rssj.androidnote.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MyMessagerService extends Service {

    private MessengerHandler mHandler = new MessengerHandler();

    private Messenger mMessenger = new Messenger(mHandler);

    public MyMessagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private static class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //取出客户端的消息内容
            Bundle bundle = msg.getData();
            String clientMsg = bundle.getString("client");
            Log.i("service", "来自客户端的消息：" + clientMsg);
            //新建一个Message对象，作为回复客户端的对象
            Message message = Message.obtain();
            Bundle bundle1 = new Bundle();
            bundle1.putString("service", "今天就不去了，还有很多东西要学！！");
            message.setData(bundle1);
            try {
                msg.replyTo.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}