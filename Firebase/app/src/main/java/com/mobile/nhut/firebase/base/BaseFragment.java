package com.mobile.nhut.firebase.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.mobile.nhut.firebase.R;
import com.mobile.nhut.firebase.dagger.Injector;
import com.mobile.nhut.firebase.manager.FireBaseManager;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment extends Fragment {

    protected BaseActivity mHost;

    protected FireBaseManager mFireBaseManager;

    private CompositeSubscription mCompositeSubscription;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        mFireBaseManager = FireBaseManager.getInstance();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHost = (BaseActivity) activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHost = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription.clear();
            mCompositeSubscription = null;
        }
    }

    public synchronized Subscription manageSubscription(Subscription subscription) {
        if (!mHost.isFinishing()) {
            if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed()) {
                mCompositeSubscription = new CompositeSubscription();
            }
            mCompositeSubscription.add(subscription);
            return subscription;
        }
        return null;
    }

    public void showProgressDialog(String text, boolean cancelable) {
        dismissProgressDialog();

        mProgressDialog = new ProgressDialog(mHost);
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
