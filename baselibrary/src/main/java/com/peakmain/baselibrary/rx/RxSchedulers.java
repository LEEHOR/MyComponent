package com.peakmain.baselibrary.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/18
 * 描述：通用的Rx线程转换类
 */
public class RxSchedulers {
    static final ObservableTransformer schedulersTransformer= new ObservableTransformer()
    {
        @Override
        public ObservableSource apply(Observable upstream)
        {
            return (upstream).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };
    public static <T> ObservableTransformer<T, T> applySchedulers()
    {
        return  (ObservableTransformer<T, T>)schedulersTransformer;
    }

}
