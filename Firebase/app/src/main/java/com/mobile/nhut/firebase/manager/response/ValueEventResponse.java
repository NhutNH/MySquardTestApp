package com.mobile.nhut.firebase.manager.response;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mobile.nhut.firebase.manager.FireBaseCommand;
import com.mobile.nhut.firebase.manager.FireBaseResponse;

public class ValueEventResponse<T> extends AbsFireBaseResponse implements ValueEventListener {

  private T mData;

  public ValueEventResponse(FireBaseResponse fireBaseResponse, FireBaseCommand command) {
    super(fireBaseResponse, command);
  }

  @Override
  public void onDataChange(DataSnapshot snapshot) {
    switch (mCommandType) {
      case SHOW_FRIEND_LIST:
        mFireBaseResponse.processFinish(snapshot);
        break;
      case LOAD_MESSAGE:
        mFireBaseResponse.processFinish(snapshot);
        break;
      case CHECK_EXISTS_ROOM:
        mFireBaseResponse.processFinish(snapshot);
        break;
    }
  }

  @Override
  public void onCancelled(FirebaseError firebaseError) {
    mFireBaseResponse.processError(firebaseError);
  }
}
