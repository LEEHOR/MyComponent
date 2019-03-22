package com.peakmain.baselibrary.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.peakmain.baselibrary.baseMvp.BaseMvpPresenter;
import com.peakmain.baselibrary.baseMvp.baseView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/22
 * 描述：
 */
public  abstract  class BaseMvpFragment <P extends BaseMvpPresenter> extends BaseLazyFragment implements baseView {

    protected P mPresenter;

    protected abstract P createPresenter();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        this.mPresenter = null;
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.bindUntilEvent(FragmentEvent.DESTROY_VIEW);
    }
}
