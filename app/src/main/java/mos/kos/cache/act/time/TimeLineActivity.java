package mos.kos.cache.act.time;

import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import mos.kos.cache.R;
import mos.kos.cache.act.time.adapter.TimeLine;
import mos.kos.cache.act.time.adapter.TimeLineAdapter;
import mos.kos.cache.init.BaseActivity;

public class TimeLineActivity extends BaseActivity {
    private ExpandableListView elv_Main;
    private TimeLineAdapter timeLineAdapter;
    private TimeLine timeLine;
    private List<MonthBean> list;

    @Override
    protected int layout() {
        return R.layout.activity_time_line;
    }

    @Override
    protected void basis() {
        elv_Main = findViewById(R.id.main_elv);
    }

    @Override
    protected void logic() {
        list = new ArrayList<>();

        TimeLineBean day1 = new TimeLineBean(25, "测试标题-->1", R.drawable.image);
        list.add(new MonthBean(2016, 3, day1));
        TimeLineBean day2 = new TimeLineBean(9, "测试标题-->2", R.drawable.image);
        list.add(new MonthBean(2016, 4, day2));
        TimeLineBean day3 = new TimeLineBean(1, "测试标题-->3", R.drawable.image);
        TimeLineBean day4 = new TimeLineBean(17, "测试标题-->4", R.drawable.image);
        list.add(new MonthBean(2016, 5, day3, day4));
        TimeLineBean day5 = new TimeLineBean(10, "测试标题-->5", R.drawable.image);
        TimeLineBean day52 = new TimeLineBean(10, "测试标题-->6", R.drawable.image);
        TimeLineBean day51 = new TimeLineBean(19, "测试标题-->7", R.drawable.image);
        list.add(new MonthBean(2016, 9, day5, day52, day51));
        TimeLineBean day6 = new TimeLineBean(29, "测试标题-->8", R.drawable.image);
        list.add(new MonthBean(2016, 10, day6));
        TimeLineBean day7 = new TimeLineBean(29, "测试标题-->9", R.drawable.image);
        list.add(new MonthBean(2017, 10, day7));
        TimeLineBean day8 = new TimeLineBean(25, "测试标题-->10", R.drawable.image);
        list.add(new MonthBean(2017, 3, day8));
        timeLineAdapter = new TimeLineAdapter(this, list);
        elv_Main.setAdapter(timeLineAdapter);
        elv_Main.setDivider(null);
        elv_Main.setGroupIndicator(null);

        for (int i = 0; i < timeLineAdapter.getGroupCount(); i++) {
            elv_Main.expandGroup(i);
        }
    }
}
