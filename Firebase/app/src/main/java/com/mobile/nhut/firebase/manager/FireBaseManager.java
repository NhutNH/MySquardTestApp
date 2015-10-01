package com.mobile.nhut.firebase.manager;

import com.firebase.client.Firebase;
import com.mobile.nhut.firebase.base.AppSettings;
import com.mobile.nhut.firebase.dagger.Injector;
import com.mobile.nhut.firebase.manager.response.ValueEventResponse;
import com.mobile.nhut.firebase.model.Message;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class FireBaseManager {

  private static FireBaseManager sInstance;

  @Inject Context mContext;

  @Inject AppSettings mAppSettings;

  private Firebase mFirebaseHost;

  private FireBaseCommand mCommandType;

  private FireBaseResponse mFireBaseResponse;

  private FireBaseManager() {
    //Inject from MyModule to use Context
    Injector.inject(this);
    this.mFirebaseHost = new Firebase(FireBaseEnvironment.HOST.Nhutfirebaseio);
  }

  public static FireBaseManager getInstance() {
    if (sInstance == null) {
      synchronized (FireBaseManager.class) {
        if (sInstance == null) {
          sInstance = new FireBaseManager();
        }
      }
    }
    return sInstance;
  }

  public void clear() {
    sInstance = null;
  }

  public FireBaseCommand getFireBaseCommand() {
    return this.mCommandType;
  }

  public void createUser() {
    mFirebaseHost.push().child(FireBaseEnvironment.CHILD.Users).setValue("Nhut");
    mFirebaseHost.push().child(FireBaseEnvironment.CHILD.Users).setValue("Chien");
    mFirebaseHost.push().child(FireBaseEnvironment.CHILD.Users).setValue("Hung");
  }

  public void getFriendList(FireBaseResponse fireBaseResponse) {
    this.mCommandType = FireBaseCommand.SHOW_FRIEND_LIST;
    this.mFireBaseResponse = fireBaseResponse;

    mFirebaseHost.addValueEventListener(new ValueEventResponse(this.mFireBaseResponse, this.mCommandType));
  }

  public void checkExistsRoom(FireBaseResponse fireBaseResponse) {
    this.mCommandType = FireBaseCommand.CHECK_EXISTS_ROOM;
    this.mFireBaseResponse = fireBaseResponse;
    mFirebaseHost.child(FireBaseEnvironment.CHILD.ChatRooms)
        .addValueEventListener(new ValueEventResponse(this.mFireBaseResponse, this.mCommandType));
  }

  public void loadMessage(String roomName, FireBaseResponse fireBaseResponse) {
    this.mCommandType = FireBaseCommand.LOAD_MESSAGE;
    this.mFireBaseResponse = fireBaseResponse;
    mFirebaseHost.child(FireBaseEnvironment.CHILD.ChatRooms).child(roomName)
        .addValueEventListener(new ValueEventResponse(this.mFireBaseResponse, this.mCommandType));
  }

  public void removeCheckExistRoomEvent() {
    mFirebaseHost.child(FireBaseEnvironment.CHILD.ChatRooms)
        .removeEventListener(new ValueEventResponse(this.mFireBaseResponse, this.mCommandType));
  }

  public void addMessage(String roomName, Message message, FireBaseResponse fireBaseResponse) {
    this.mFireBaseResponse = fireBaseResponse;
    Map<String, String> post = new HashMap<String, String>();
    post.put("author", message.getAuthor());
    post.put("content", message.getContent());
    mFirebaseHost.child(FireBaseEnvironment.CHILD.ChatRooms).child(roomName).push().child(FireBaseEnvironment.CHILD.Message).setValue(post);
  }
}
