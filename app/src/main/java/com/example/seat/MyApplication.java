package com.example.seat;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class MyApplication extends Application {      //辅助的工具类，用于实现扫描二维码功能，源自网上代码，https://blog.csdn.net/weixin_43117800/article/details/83830664
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
    }
}
