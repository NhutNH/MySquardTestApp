package com.mobile.nhut.firebase.dagger;

import android.content.ContentResolver;
import android.content.Context;

import com.mobile.nhut.firebase.ChatRoomActivity;
import com.mobile.nhut.firebase.adapters.ChatRoomCursorAdapter;
import com.mobile.nhut.firebase.base.AppSettings;
import com.mobile.nhut.firebase.manager.FireBaseManager;
import com.mobile.nhut.firebase.offline.OfflineManager;

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
                ChatRoomActivity.class,
                AppSettings.class,
                //Adapter
                ChatRoomCursorAdapter.class,
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
        return AppSettings.getInstance(context);
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

    @Singleton
    @Provides
    ContentResolver provideContentResolver() {
        return MyApplication.getInstance().getContentResolver();
    }
}
