package com.mobile.nhut.firebase.manager.response;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mobile.nhut.firebase.manager.FireBaseCommand;
import com.mobile.nhut.firebase.manager.FireBaseResponse;

public class AuthResultResponse extends AbsFireBaseResponse implements Firebase.AuthResultHandler {

  public AuthResultResponse(FireBaseResponse fireBaseResponse, FireBaseCommand command) {
    super(fireBaseResponse, command);
  }

  @Override
  public void onAuthenticated(AuthData authData) {
    // Authenticated successfully with payload authData
    switch (mCommandType) {
      case AUTHENTICATION_BY_EMAIL:
        mFireBaseResponse.processFinish(authData.getUid());
        break;
      case AUTHENTICATION_BY_CUSTOM_TOKEN:
        mFireBaseResponse.processFinish(authData);
        break;
    }
  }

  @Override
  public void onAuthenticationError(FirebaseError firebaseError) {
    // Authenticated failed with error firebaseError
    mFireBaseResponse.processError(firebaseError);
  }
}
