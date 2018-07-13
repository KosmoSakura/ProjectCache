package mos.kos.cache.act.iflytek;

import android.content.Context;

import com.google.gson.Gson;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;

import mos.kos.cache.tool.ULog;

/**
 * @Description: 语音识别实体
 * @Author: Kosmos
 * @Date: 2018年07月13日 15:42
 * @Email: KosmoSakura@foxmail.com
 */
public class USpeech implements InitListener, RecognizerDialogListener {
    private RecognizerDialog mDialog;

    private USpeech() {
    }

    /**
     * 初始化语音识别
     */
    public void init(Context context) {
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5b45cf88");
        //1.创建RecognizerDialog对象
        mDialog = new RecognizerDialog(context, this);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        mDialog.setListener(this);
    }

    /**
     * 显示dialog，接收语音输入
     */
    public void show() {
        mDialog.show();
    }

    /**
     * 解析语音json
     */
    private String parseVoice(String resultString) {
        Gson gson = new Gson();
        VoiceBean voiceBean = gson.fromJson(resultString, VoiceBean.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<VoiceBean.WSBean> ws = voiceBean.ws;
        for (VoiceBean.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }

    @Override
    public void onInit(int i) {
        ULog.d("讯飞语音初始化完毕");
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean isLast) {
        if (!isLast) {
            //解析语音
            String result = parseVoice(recognizerResult.getResultString());
            ULog.d("语音转换：" + result);
        }
    }

    @Override
    public void onError(SpeechError speechError) {
        ULog.d("语音转换失败");
    }
}
