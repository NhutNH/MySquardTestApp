package com.mobile.nhut.firebase.manager;

import android.content.Context;

import com.firebase.client.Firebase;
import com.mobile.nhut.firebase.base.AppSettings;
import com.mobile.nhut.firebase.dagger.Injector;
import com.mobile.nhut.firebase.manager.response.ValueEventResponse;
import com.mobile.nhut.firebase.model.Message;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class FireBaseManager {

    private static FireBaseManager sInstance;

    @Inject
    Context mContext;

    @Inject
    AppSettings mAppSettings;

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

    public void loadMessage(FireBaseResponse fireBaseResponse) {
        this.mCommandType = FireBaseCommand.LOAD_MESSAGE;
        this.mFireBaseResponse = fireBaseResponse;
        mFirebaseHost.addValueEventListener(new ValueEventResponse(this.mFireBaseResponse, this.mCommandType));
    }

    public void addMessage(Message message, FireBaseResponse fireBaseResponse) {
        this.mFireBaseResponse = fireBaseResponse;
        Map<String, String> post = new HashMap<String, String>();
        post.put(FireBaseEnvironment.CHILD.Users, message.getAuthor());
        post.put(FireBaseEnvironment.CHILD.Message, message.getContent());
        mFirebaseHost.push().setValue(post);
    }
}
