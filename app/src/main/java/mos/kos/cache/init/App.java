package mos.kos.cache.init;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import mos.kos.cache.BuildConfig;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年06月23日 22:05
 * @Email: KosmoSakura@foxmail.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        looinit();//初始化日志
    }

    private void looinit() {
        //methodCount(int x)-中的数字：显示x行附属信息日志.
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)//方法栈打印的个数，默认是2
            .methodOffset(0)//设置调用堆栈的函数偏移值，0的话则从打印该Log的函数开始输出堆栈信息，默认是0
            .tag("Sakura")
            .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }
}
