package mos.kos.cache.act.time;

import java.io.Serializable;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年07月23日 17:26
 * @Email: KosmoSakura@foxmail.com
 * @Event:
 */
public class MonthBean implements Serializable {

    private int month;
    private int year;
    private TimeLineBean[] daylist;

    public MonthBean(int year, int month) {
        this.month = month;
        this.year = year;
    }

    public MonthBean(int year, int month, TimeLineBean... day) {
        this.month = month;
        this.year = year;
        this.daylist = day;
    }


    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public TimeLineBean[] getDaylist() {
        return daylist;
    }

    public void setDaylist(TimeLineBean[] daylist) {
        this.daylist = daylist;
    }
}
