package com.example.mediaplayer.application;

import android.app.Application;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.squareup.leakcanary.BuildConfig;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.xutils.x;

import java.lang.reflect.Field;


/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class mediaApplication extends Application {
    private RefWatcher refWatcher;
    private static Context context;
    public static RefWatcher getRefWatcher(Context context) {
        mediaApplication application = (mediaApplication) context
                .getApplicationContext();
        return application.refWatcher;
    }


    //解决InputManage导致的内存泄漏
    public static void fixInputMethodManagerLeak(Context context) {
        if (context == null) {
            return;
        }
        try {
            // 对 mCurRootView mServedView mNextServedView 进行置空...
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }

            Object obj_get = null;
            Field f_mCurRootView = imm.getClass().getDeclaredField("mCurRootView");
            Field f_mServedView = imm.getClass().getDeclaredField("mServedView");
            Field f_mNextServedView = imm.getClass().getDeclaredField("mNextServedView");

            if (f_mCurRootView.isAccessible() == false) {
                f_mCurRootView.setAccessible(true);
            }
            obj_get = f_mCurRootView.get(imm);
            if (obj_get != null) { // 不为null则置为空
                f_mCurRootView.set(imm, null);
            }

            if (f_mServedView.isAccessible() == false) {
                f_mServedView.setAccessible(true);
            }
            obj_get = f_mServedView.get(imm);
            if (obj_get != null) { // 不为null则置为空
                f_mServedView.set(imm, null);
            }

            if (f_mNextServedView.isAccessible() == false) {
                f_mNextServedView.setAccessible(true);
            }
            obj_get = f_mNextServedView.get(imm);
            if (obj_get != null) { // 不为null则置为空
                f_mNextServedView.set(imm, null);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);//初始化leakcanary

        context = getApplicationContext();

        x.Ext.init(this); //初始化xutils
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

    public static Context getContext(){
        return context;
    }

    @Override
    public Context createDeviceProtectedStorageContext() {
        return super.createDeviceProtectedStorageContext();
    }
}
