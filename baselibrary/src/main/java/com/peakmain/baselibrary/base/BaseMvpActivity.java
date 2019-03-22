package com.peakmain.baselibrary.base;

import com.peakmain.baselibrary.baseMvp.BaseMvpPresenter;
import com.peakmain.baselibrary.baseMvp.baseView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/22
 * 描述：
 */
public abstract class BaseMvpActivity <P extends BaseMvpPresenter> extends BaseActivity implements baseView {

    protected P mPresenter;

    protected abstract P createPresenter();

    @Override
    protected void initView() {
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showDefaultMsg(String msg) {
    }

    @Override
    public void showErrorMsg(String errorMsg) {

    }

    @Override
    public void showMsg(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        this.mPresenter = null;
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return bindUntilEvent(ActivityEvent.DESTROY);
    }
}
