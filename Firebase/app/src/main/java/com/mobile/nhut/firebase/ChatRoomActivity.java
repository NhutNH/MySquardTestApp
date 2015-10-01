package com.mobile.nhut.firebase;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.mobile.nhut.firebase.adapters.ChatRoomCursorAdapter;
import com.mobile.nhut.firebase.base.AppSettings;
import com.mobile.nhut.firebase.base.BaseActivity;
import com.mobile.nhut.firebase.manager.FireBaseResponse;
import com.mobile.nhut.firebase.model.Message;
import com.mobile.nhut.firebase.offline.OfflineManager;
import com.mobile.nhut.firebase.offline.OfflineProvider;
import com.mobile.nhut.firebase.offline.threading.ThreadExecutor;

import java.util.Random;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ChatRoomActivity extends BaseActivity implements FireBaseResponse, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static String mYourName;

    @Bind(R.id.my_recycler_view)
    RecyclerView vRecyclerView;
    @Bind(R.id.edt_message_content_firebase_chatroom)
    EditText mEdtMessage;
    @Bind(R.id.btnSend_firebase_chatroom)
    Button mBtnSend;
    @Inject
    OfflineManager mOfflineManager;
    @Inject
    AppSettings mAppSettings;

    private ChatRoomCursorAdapter mChatRoomCursorAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ButterKnife.bind(this);

        vRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        vRecyclerView.setLayoutManager(mLayoutManager);
        if(TextUtils.isEmpty(mAppSettings.getmUserName())){
            Random rand = new Random();
            mYourName = "Nhut" + (rand.nextInt(50) + 1);
            mAppSettings.setmUserName(mYourName);
        }else{
            mYourName = mAppSettings.getmUserName();
        }

        initData();
    }

    @Override
    @OnClick({R.id.btnSend_firebase_chatroom})
    public void onClick(View v) {
        Message message = new Message();
        message.setAuthor(mYourName);
        message.setContent(mEdtMessage.getText().toString());

        this.mFireBaseManager.addMessage(message, this);
        mEdtMessage.setText("");
    }

    @Override
    public void processFinish(Object response) {
        switch (this.mFireBaseManager.getFireBaseCommand()) {
            case LOAD_MESSAGE:
                if (response != null & response instanceof DataSnapshot) {
                    DataSnapshot snapshot = (DataSnapshot) response;
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        manageSubscription(mOfflineManager.checkExistsMessage(postSnapshot)
                                .subscribeOn(Schedulers.from(ThreadExecutor.getInstance()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Void>() {
                                    @Override
                                    public void call(Void result) {
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                    }
                                }));
                    }
                }
                break;
        }
    }

    @Override
    public void processError(FirebaseError firebaseError) {
    }

    private void initData() {
        mBtnSend.setEnabled(true);
        callLoader();
        this.mFireBaseManager.loadMessage(this);
    }

    private void callLoader() {
        LoaderManager lmanager = getLoaderManager();
        if (lmanager.getLoader(0) != null) {
            lmanager.restartLoader(0, null, this);
        } else {
            lmanager.initLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this, OfflineProvider.CONTENT_URI, null, null, null, null);
        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            mOfflineManager.setmOfflineCursor(cursor);
            mChatRoomCursorAdapter = new ChatRoomCursorAdapter(mOfflineManager.getOfflineCursor());
            vRecyclerView.setAdapter(mChatRoomCursorAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}
