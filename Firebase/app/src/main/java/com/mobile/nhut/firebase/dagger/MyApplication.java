package com.mobile.nhut.firebase.dagger;


import com.firebase.client.Firebase;

import android.app.Application;

public class MyApplication extends Application {

  private static MyApplication sInstance;

  public MyApplication() {
    sInstance = this;
  }

  public static MyApplication getInstance() {
    return sInstance;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    initObjectGraph();
    Firebase.setAndroidContext(this);
  }

  private void initObjectGraph() {
    Injector.init(getRootModule(), this);
  }

  protected Object getRootModule() {
    return new MyModule();
  }
}
