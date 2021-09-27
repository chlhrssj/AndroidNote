// IMyAidlInterface.aidl
package com.rssj.androidnote;
import com.rssj.androidnote.aidl.Book;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    String getString();
    int add(in int a, in int b);
    oneway void seyHello();
    void outBook(out Book book);
    void inBook(in Book book);
    void inoutBook(inout Book book);
}