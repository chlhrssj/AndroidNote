package com.rssj.androidnote.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.rssj.androidnote.IMyAidlInterface;

public class MyAIDLService extends Service {

    public MyAIDLService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IAIDLImpl();
    }

    static class IAIDLImpl extends IMyAidlInterface.Stub {

        @Override
        public String getString() throws RemoteException {
            return "hello world";
        }

        @Override
        public int add(int a, int b) throws RemoteException {
            return a + b;
        }

        @Override
        public void seyHello() throws RemoteException {
            Log.i("Service", "sayHello world ---- " + android.os.Process.myPid());
        }

        @Override
        public void outBook(Book book) throws RemoteException {
            Log.i("Service", book.toString());
            book.setId(2);
            book.setName("金瓶梅");
            Log.i("Service", book.toString());
        }

        @Override
        public void inBook(Book book) throws RemoteException {
            Log.i("Service", book.toString());
            book.setId(2);
            book.setName("金瓶梅");
            Log.i("Service", book.toString());
        }

        @Override
        public void inoutBook(Book book) throws RemoteException {
            Log.i("Service", book.toString());
            book.setId(2);
            book.setName("金瓶梅");
            Log.i("Service", book.toString());
        }
    }

}