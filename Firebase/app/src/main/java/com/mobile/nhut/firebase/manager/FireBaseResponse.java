package com.mobile.nhut.firebase.manager;

import com.firebase.client.FirebaseError;

public interface FireBaseResponse {

  public void processFinish(Object response);

  public void processError(FirebaseError firebaseError);
}
