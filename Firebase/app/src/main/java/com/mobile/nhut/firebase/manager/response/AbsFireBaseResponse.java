package com.mobile.nhut.firebase.manager.response;

import com.mobile.nhut.firebase.manager.FireBaseCommand;
import com.mobile.nhut.firebase.manager.FireBaseResponse;

public abstract class AbsFireBaseResponse {

  protected FireBaseResponse mFireBaseResponse;

  protected FireBaseCommand mCommandType;

  public AbsFireBaseResponse(FireBaseResponse fireBaseResponse, FireBaseCommand command) {
    this.mFireBaseResponse = fireBaseResponse;
    this.mCommandType = command;
  }
}
