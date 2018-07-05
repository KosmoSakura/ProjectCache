package mos.kos.cache.act;

import android.content.Intent;
import android.os.Handler;

import java.util.ArrayList;

import mos.kos.cache.R;
import mos.kos.cache.act.list.ListAlphaActivity;
import mos.kos.cache.act.list.ListToolbarActivity;
import mos.kos.cache.data.MainBean;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.logic.MainAdapter;
import x.rv.XRecyclerView;


/**
 * 主页
 */
public class MainActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private MainAdapter adapter;
    private ArrayList<MainBean> list;

    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected void basis() {
        list = new ArrayList<>();
        adapter = new MainAdapter(list);
        initXrv(adapter, R.id.main_list);
    }

    @Override
    protected void logic() {
//        startActivity(new Intent(MainActivity.this, CircleActivity.class));
        refreshData();
        adapter.setOnItemClickListener(bean -> {
            switch (bean.getIds()) {
                case 1://图形图片
                    startActivity(new Intent(MainActivity.this, CircleActivity.class));
                    break;
                case 2://桌面宠物
                    startActivity(new Intent(MainActivity.this, PetActivity.class));
                    break;
                case 3://Alpha刷新:
                    startActivity(new Intent(MainActivity.this, ListAlphaActivity.class));
//                    list.remove(0);
//                    xrv.notifyItemRemoved(list, 0);
                    break;
                case 4://ToolBar刷新:
//                    list.add(new MainBean("Alpha刷新", 5));
//                    xrv.notifyItemInserted(list, list.size());
                    startActivity(new Intent(MainActivity.this, ListToolbarActivity.class));
                    break;
            }

        });
        xrv.setLoadingListener(this);
    }

    private void refreshData() {
        list.clear();
        list.add(new MainBean("图形图片", 1));
        list.add(new MainBean("桌面宠物", 2));
        list.add(new MainBean("Alpha刷新", 3));
        list.add(new MainBean("ToolBar刷新", 4));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            refreshData();
            xrv.refreshComplete();
        }, 1000);

    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(() -> {
            xrv.loadMoreComplete();
//            xrv.setNoMore(list.size() > 10);
        }, 1000);
    }
}
