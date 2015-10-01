package com.mobile.nhut.firebase.dagger;

import com.mobile.nhut.firebase.ChatRoomActivity;
import com.mobile.nhut.firebase.MainActivity;
import com.mobile.nhut.firebase.adapters.ChatRoomCursorAdapter;
import com.mobile.nhut.firebase.adapters.ListNoteAdapter;
import com.mobile.nhut.firebase.adapters.util.ImageLoader;
import com.mobile.nhut.firebase.base.AppSettings;
import com.mobile.nhut.firebase.manager.FireBaseManager;
import com.mobile.nhut.firebase.offline.OfflineManager;

import android.content.ContentResolver;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
    complete = false,
    library = true,
    injects = {
        MyApplication.class,
        AppSettings.class,
        FireBaseManager.class,
        MainActivity.class,
        ChatRoomActivity.class,
        //Adapter
        ListNoteAdapter.class,
        ChatRoomCursorAdapter.class,
        //Util
        ImageLoader.class,
        //Offline
        OfflineManager.class
    }
)
public class MyModule {

    @Singleton
    @Provides
    Context provideAppContext() {
        return MyApplication.getInstance();
    }

    @Provides
    @Singleton
    AppSettings provideAppSettings(Context context) {
        return new AppSettings(context);
    }

    @Provides
    @Singleton
    FireBaseManager provideFireBaseManager() {
        return FireBaseManager.getInstance();
    }

    @Provides
    @Singleton
    OfflineManager provideOfflineManager() {
        return OfflineManager.getInstance();
    }

    @Provides
    @Singleton
    ImageLoader provideImageLoader(Context context) {
        return ImageLoader.getInstance(context);
    }

    @Singleton
    @Provides
    ContentResolver provideContentResolver() {
        return MyApplication.getInstance().getContentResolver();
    }
}
