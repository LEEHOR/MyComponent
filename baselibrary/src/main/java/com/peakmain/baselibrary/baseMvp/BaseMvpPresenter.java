package com.peakmain.baselibrary.baseMvp;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.util.Preconditions;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/22
 * 描述：
 */
public abstract class BaseMvpPresenter<M extends baseModel, V extends baseView> implements basePresenter<V>, LifecycleObserver {
    protected final String TAG = this.getClass().getSimpleName();
    protected CompositeDisposable mCompositeDisposable;
    private M mModel;
    private V mView;

    public BaseMvpPresenter() {
        this.mModel = createModel();
    }

    public V getView() {
      //  Preconditions.checkNotNull(mView, "%s cannot be null", baseView.class.getName());
        return mView;
    }

    public M getModel() {
       //  Preconditions.checkNotNull(mModel, "%s cannot be null", baseModel.class.getName());
        return mModel;
    }

    /**
     * 获取 Model
     *
     * @return
     */
    protected abstract M createModel();

    /**
     * 如果要使用 Eventbus 请将此方法返回 true
     */
    protected boolean useEventBus() {
        return false;
    }

    @Override
    public void attachView(V mView) {
        this.mView = mView;
        //将 LifecycleObserver 注册给 LifecycleOwner 后 @OnLifecycleEvent 才可以正常使用
        if (mView != null && mView instanceof LifecycleOwner) {
            ((LifecycleOwner) mView).getLifecycle().addObserver(this);
            if (mModel != null && mModel instanceof LifecycleObserver) {
                ((LifecycleOwner) mView).getLifecycle().addObserver((LifecycleObserver) mModel);
            }
        }
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void detachView() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        unDispose();// 解除订阅
        if (mModel != null)
            mModel.onDetach();
        this.mModel = null;
        this.mView = null;
        this.mCompositeDisposable = null;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(this);
    }

    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有的 Disposable 放入集中处理
    }

    /**
     * 停止正在进行的任务
     */
    public void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();//保证Activity结束时取消
        }
    }
}
