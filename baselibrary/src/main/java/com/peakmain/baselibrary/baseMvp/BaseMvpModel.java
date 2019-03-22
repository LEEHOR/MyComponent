package com.peakmain.baselibrary.baseMvp;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/22
 * 描述：
 */
public class BaseMvpModel implements baseModel ,LifecycleObserver {

    private CompositeDisposable mCompositeDisposable;
    protected final String TAG = this.getClass().getSimpleName();

    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有的 Disposable 放入集中处理
    }
    @Override
    public void onDetach() {
        unDispose();
    }
    private void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
        mCompositeDisposable = null;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(this);
    }
}
