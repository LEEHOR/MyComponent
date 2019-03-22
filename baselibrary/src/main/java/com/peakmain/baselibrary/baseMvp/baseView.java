package com.peakmain.baselibrary.baseMvp;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/22
 * 描述：
 */
public interface baseView {
    /**
     * 显示加载
     */
    void showLoading();

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 显示信息
     *
     * @param message
     */
    void showMsg(String message);

    /**
     * 使用默认的样式显示信息
     *
     * @param msg
     */
    void showDefaultMsg(String msg);

    /**
     * 显示错误信息
     *
     * @param errorMsg
     */
    void showErrorMsg(String errorMsg);

    /**
     * 绑定生命周期
     */
    <T> LifecycleTransformer<T> bindToLife();
}
