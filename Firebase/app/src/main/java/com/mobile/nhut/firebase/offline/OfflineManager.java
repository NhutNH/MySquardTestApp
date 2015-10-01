package com.mobile.nhut.firebase.offline;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.firebase.client.DataSnapshot;
import com.mobile.nhut.firebase.dagger.Injector;
import com.mobile.nhut.firebase.manager.FireBaseEnvironment;
import com.mobile.nhut.firebase.model.Message;

import java.util.HashMap;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

public class OfflineManager {

  @Inject
  ContentResolver mContentResolver;

  private boolean mIsDataAvailable;

  private Cursor mOfflineCursor;

  private static OfflineManager sInstance;

  public static OfflineManager getInstance() {
    if (sInstance == null) {
      synchronized (OfflineManager.class) {
        if (sInstance == null) {
          sInstance = new OfflineManager();
        }
      }
    }
    return sInstance;
  }

  private OfflineManager() {
    Injector.inject(this);
//    mOfflineCursor = fetchData();
//    if (mOfflineCursor != null && mOfflineCursor.getCount() > 0) {
//      mIsDataAvailable = true;
//    }
  }

  public Observable<Cursor> loadChatRoomData(final String roomName) {
    return Observable.create(new Observable.OnSubscribe<Cursor>() {
      @Override
      public void call(Subscriber<? super Cursor> subscriber) {
        try {
          if (!subscriber.isUnsubscribed()) {
            fetchDatabyRoom(roomName);
            subscriber.onNext(mOfflineCursor);
            //Done thread
            subscriber.onCompleted();
          }
        } catch (Exception e) {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onError(e);
            subscriber.onCompleted();
          }
        }
      }
    });
  }

  public Observable<Void> checkExistsMessage(final DataSnapshot dataSnapshot) {
    return Observable.create(new Observable.OnSubscribe<Void>() {
      @Override
      public void call(Subscriber<? super Void> subscriber) {
        try {
          if (!subscriber.isUnsubscribed()) {
            boolean isHasData = fetchDatabyMessageId(dataSnapshot.getKey());
            if(!isHasData){
              HashMap data = (HashMap) dataSnapshot.getValue();

              if (data != null) {
                insertToDatabase(data.get(FireBaseEnvironment.CHILD.Users).toString(), dataSnapshot.getKey(), data.get(FireBaseEnvironment.CHILD.Message).toString(), true);
              }

            }
            subscriber.onNext(null);
            //Done thread
            subscriber.onCompleted();
          }
        } catch (Exception e) {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onError(e);
            subscriber.onCompleted();
          }
        }
      }
    });
  }

  public void setmOfflineCursor(Cursor cursor) {
    if(cursor != null && cursor.getCount() > 0){
      this.mIsDataAvailable = true;
      this.mOfflineCursor = cursor;
    }else {
      this.mIsDataAvailable = false;
      this.mOfflineCursor = null;
    }
  }

  private Cursor fetchData() {
    return mContentResolver.query(OfflineProvider.CONTENT_URI, null, null, null, null);
  }

  private Cursor fetchDatabyRoom(String groupname) {
    Cursor cursor = mContentResolver.query(OfflineProvider.CONTENT_URI, null, null, null, null);

    if(cursor != null && cursor.getCount() > 0){
      this.mIsDataAvailable = true;
      return cursor;
    }
    this.mIsDataAvailable = false;
    return null;
  }

  private boolean fetchDatabyMessageId(String idMessage) {
    String selection = OfflineProvider.ID_MESSAGE + "=?";
    Cursor cursor = mContentResolver.query(OfflineProvider.CONTENT_URI, null, selection, new String[]{idMessage}, null);

    if(cursor != null && cursor.getCount() > 0){
      return true;
    }
    return false;
  }

  public boolean isDataAvailable() {
    return mIsDataAvailable;
  }

  public Cursor getOfflineCursor() {
    return mOfflineCursor;
  }

  private void insertToDatabase(String author, String id_message, String message, boolean isOffline) throws Exception {
    ContentValues values = new ContentValues();
    values.put(OfflineProvider.AUTHOR, author);
    values.put(OfflineProvider.ID_MESSAGE, id_message);
    values.put(OfflineProvider.MESSAGE, message);
    values.put(OfflineProvider.STATE_OFFLINE, isOffline);
    mContentResolver.insert(OfflineProvider.CONTENT_URI, values);
  }

  private void updateToDatabase(String idMessage){
//    ContentValues values = new ContentValues();
//    String selection = OfflineProvider.ID_MESSAGE + "=?";
//    values.put(OfflineProvider.STATE_OFFLINE, true);
//    mContentResolver.update(OfflineProvider.CONTENT_URI, values, selection, new String[]{idMessage});
  }

  public void destroy() {
    if (mOfflineCursor != null && !mOfflineCursor.isClosed()) {
      mOfflineCursor.close();
    }
    mIsDataAvailable = false;
    sInstance = null;
  }
}
