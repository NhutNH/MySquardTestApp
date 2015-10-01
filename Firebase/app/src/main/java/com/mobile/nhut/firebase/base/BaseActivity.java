package com.mobile.nhut.firebase.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.mobile.nhut.firebase.R;
import com.mobile.nhut.firebase.dagger.Injector;
import com.mobile.nhut.firebase.manager.FireBaseManager;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


public abstract class BaseActivity extends AppCompatActivity {

    public static final String LOG_OUT_INTENT = "LOG_OUT_INTENT";

    protected
    @Inject
    FireBaseManager mFireBaseManager;

    protected
    @Inject
    AppSettings mAppSettings;

    private CompositeSubscription mSubscriptionList;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        //mFireBaseManager = FireBaseManager.getInstance();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSubscriptionList != null) {
            mSubscriptionList.unsubscribe();
            mSubscriptionList.clear();
            mSubscriptionList = null;
        }
    }

    public synchronized Subscription manageSubscription(Subscription subscription) {
        if (!isFinishing()) {
            if (mSubscriptionList == null || mSubscriptionList.isUnsubscribed()) {
                mSubscriptionList = new CompositeSubscription();
            }
            mSubscriptionList.add(subscription);
            return subscription;
        }
        return null;
    }

    public void showProgressDialog(String text, boolean cancelable) {
        dismissProgressDialog();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setMessage(TextUtils.isEmpty(text) ? getString(R.string.loading) : text);
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
