package mos.kos.cache.act.iflytek;

import java.util.ArrayList;

/**
 * @Description: 语音对象封装
 * @Author: Kosmos
 * @Date: 2018年07月13日 15:43
 * @Email: KosmoSakura@foxmail.com
 */
public class VoiceBean {
    public ArrayList<WSBean> ws;

    public class WSBean {
        public ArrayList<CWBean> cw;
    }

    public class CWBean {
        public String w;
    }
}
