package com.peakmain.baselibrary.baseMvp;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/18
 * 描述：
 */
public interface basePresenter<V extends  baseView>{


    /**
     * 绑定 View
     */
    void attachView(V mView);

    /**
     * 解绑 View
     */
    void detachView();
}
