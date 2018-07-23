package mos.kos.cache.act.time;

import java.io.Serializable;

/**
 * @Description: 时光轴
 * @Author: Kosmos
 * @Date: 2018年07月23日 17:22
 * @Email: KosmoSakura@foxmail.com
 */
public class TimeLineBean implements Serializable {
    private int day;
    private String desc;
    private int resId;

    public TimeLineBean(int day, String desc, int resId) {
        this.day = day;
        this.desc = desc;
        this.resId = resId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
