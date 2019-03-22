package com.peakmain.baselibrary.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/22
 * 描述：
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 绑定 ButterKnife 时返回的 Unbinder ，用于 ButterKnife 解绑
     */
    private Unbinder mUnbinder;

    @LayoutRes
    protected abstract int attachLayoutRes();

    /**
     * 初始化View
     */
    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    protected boolean useEventBus() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutResId = attachLayoutRes();
        if (layoutResId != 0) {
            setContentView(layoutResId);
            mUnbinder = ButterKnife.bind(this);
            if (useEventBus()) {
                EventBus.getDefault().register(this);
            }
            initView();
            initData();
            initListener();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            mUnbinder.unbind();
        }
        this.mUnbinder = null;
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }
}
