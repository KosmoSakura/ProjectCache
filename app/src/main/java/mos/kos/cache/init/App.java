package mos.kos.cache.init;

import android.app.Application;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import mos.kos.cache.BuildConfig;
import mos.kos.cache.tool.ULog;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年06月23日 22:05
 * @Email: KosmoSakura@foxmail.com
 */
public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initLog();//初始化日志
        initIflytek();//初始化讯飞语音
    }

    private void initIflytek() {
        // 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用半角“,”分隔。
        // 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符
        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=");
        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        Setting.setShowLog(false);
    }

    public static App getInstance() {
        return instance;
    }

    private void initLog() {
        ULog.init(this)
            .setLogSwitch(BuildConfig.DEBUG)// 设置 log 总开关，包括输出到控制台和文件，默认开
            .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
            .setGlobalTag("Sakura")// 设置 log 全局标签，默认为空
            // 当全局标签不为空时，我们输出的 log 全部为该 tag，
            // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
            .setLogHeadSwitch(true)// 设置 log 头信息开关，默认为开
            .setLog2FileSwitch(false)// 打印 log 时是否存到文件的开关，默认关
            .setDir("")// 当自定义路径为空时，写入应用的 /cache/log/ 目录中
            .setFilePrefix("Sakura")// 当文件前缀为空时，默认为 "alog"，即写入文件为 "alog-MM-dd.txt"
            .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
            .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
            .setConsoleFilter(ULog.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
            .setFileFilter(ULog.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
            .setStackDeep(1)// log 栈深度，默认为 1
            .setStackOffset(0);// 设置栈偏移，比如二次封装的话就需要设置，默认为 0
    }
}
