package com.mobile.nhut.firebase;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.mobile.nhut.firebase.adapters.ListNoteAdapter;
import com.mobile.nhut.firebase.base.BaseActivity;
import com.mobile.nhut.firebase.manager.FireBaseEnvironment;
import com.mobile.nhut.firebase.manager.FireBaseResponse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements FireBaseResponse {

  public static final String ROOM_NAME = "ROOM_NAME";

  @Bind(R.id.txt_Username)
  TextView mTxtUsername;

  @Bind(R.id.recycler_view)
  RecyclerView vRecyclerView;

  private ListNoteAdapter mAdapter;

  private RecyclerView.LayoutManager mLayoutManager;

  private ArrayList<String> mFriendList = new ArrayList<>();

  private ListNoteAdapter.ItemClickListener mItemClickListener = new ListNoteAdapter.ItemClickListener() {
    @Override
    public void onClick(View view, int position) {
      if (mFriendList != null && mFriendList.size() > 0) {
        Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
        intent.putExtra(ROOM_NAME, mTxtUsername.getText() + "-" + mFriendList.get(position));
        startActivity(intent);
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    vRecyclerView.setHasFixedSize(true);
    mLayoutManager = new LinearLayoutManager(this);
    vRecyclerView.setLayoutManager(mLayoutManager);

    mAdapter = new ListNoteAdapter(mFriendList);
    mAdapter.setOnItemClickListener(mItemClickListener);

    vRecyclerView.setAdapter(mAdapter);
    initData();
  }

  private void initData() {
//    initUser();
    this.mFireBaseManager.getFriendList(this);
  }

  @Override
  public void processFinish(Object response) {
    switch (this.mFireBaseManager.getFireBaseCommand()) {
      case SHOW_FRIEND_LIST:
        if (response != null && response instanceof DataSnapshot) {
          DataSnapshot snapshot = (DataSnapshot) response;
          this.mFriendList = new ArrayList<>();
          for (DataSnapshot postSnapshot : snapshot.getChildren()) {

            HashMap data = (HashMap) postSnapshot.getValue();
            if(data != null) {
              this.mFriendList.add(data.get(FireBaseEnvironment.CHILD.Users).toString());
            }
          }

          mAdapter = new ListNoteAdapter(mFriendList);
          mAdapter.setOnItemClickListener(mItemClickListener);
          vRecyclerView.setAdapter(mAdapter);
          break;
        }
    }
  }

  @Override
  public void processError(FirebaseError firebaseError) {
  }

//  private void initUser(){
//    this.mFireBaseManager.createUser();
//  }
}