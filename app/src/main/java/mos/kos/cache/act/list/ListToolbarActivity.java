package mos.kos.cache.act.list;

import android.os.Handler;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import mos.kos.cache.R;
import mos.kos.cache.data.AlphaBean;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.logic.AlphaAdapter;
import x.rv.XRecyclerView;

public class ListToolbarActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private AlphaAdapter mAdapter;
    private ArrayList<AlphaBean> listData;
    private int refreshTime = 0;
    private int page = 0;

    @Override
    protected int layout() {
        return R.layout.activity_list_toolbar;
    }

    @Override
    protected void basis() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listData = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            listData.add(new AlphaBean("item" + i));
        }
        mAdapter = new AlphaAdapter(listData);
        initXrv(mAdapter, R.id.recyclerview);
    }

    @Override
    protected void logic() {
        xrv.setLoadingListener(this);
        xrv.refresh();
    }

    @Override
    public void onRefresh() {
        refreshTime++;
        page = 0;
        new Handler().postDelayed(() -> {
            listData.clear();
            for (int i = 0; i < 15; i++) {
                listData.add(new AlphaBean("item" + i + "after " + refreshTime + " times of refresh"));
            }
            mAdapter.notifyDataSetChanged();
            xrv.refreshComplete();
        }, 1000);            //refresh data here
    }

    @Override
    public void onLoadMore() {
        if (page < 2) {
            new Handler().postDelayed(() -> {
                xrv.loadMoreComplete();
                for (int i = 0; i < 15; i++) {
                    listData.add(new AlphaBean("item" + (i + listData.size())));
                }
                xrv.loadMoreComplete();
                mAdapter.notifyDataSetChanged();
            }, 1000);
        } else {
            new Handler().postDelayed(() -> {
                for (int i = 0; i < 9; i++) {
                    listData.add(new AlphaBean("item" + (1 + listData.size())));
                }
                xrv.setNoMore(true);
                mAdapter.notifyDataSetChanged();
            }, 1000);
        }
        page++;
    }
}
