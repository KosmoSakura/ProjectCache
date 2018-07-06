package mos.kos.cache.act.list;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import mos.kos.cache.R;
import mos.kos.cache.data.AlphaBean;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.logic.AlphaAdapter;
import x.rv.XRecyclerView;

public class RefreshActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private AlphaAdapter mAdapter;
    private ArrayList<AlphaBean> listData;
    private View mEmptyView;
    private int page = 0;

    @Override
    protected int layout() {
        return R.layout.activity_refresh;
    }

    @Override
    protected void basis() {
        xrv = findViewById(R.id.recyclerview);
        mEmptyView = findViewById(R.id.text_empty);

        //没有数据，触发emptyView
        listData = new ArrayList<>();
        mAdapter = new AlphaAdapter(listData);

        xrv.getDefaultRefreshHeaderView()
            .setRefreshTimeVisible(true);
        xrv.getDefaultFootView().setLoadingHint("自定义加载中提示");
        xrv.getDefaultFootView().setNoMoreHint("自定义加载完毕提示");
        View header = LayoutInflater.from(this).inflate(R.layout.lay_head, findViewById(android.R.id.content), false);
        xrv.addHeaderView(header);
        View header2 = LayoutInflater.from(this).inflate(R.layout.lay_head, findViewById(android.R.id.content), false);
        xrv.addHeaderView(header2);
        header2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        View header3 = LayoutInflater.from(this).inflate(R.layout.lay_head, findViewById(android.R.id.content), false);
        xrv.addHeaderView(header3);
        header3.setBackgroundColor(ContextCompat.getColor(this, R.color.red));

        if (getIntent().getBooleanExtra("list_style", true)) {
            initXrv(mAdapter, R.id.recyclerview);
        } else {
            initXrvGrid(mAdapter, R.id.recyclerview, 3);
        }
    }

    @Override
    protected void logic() {
        xrv.setLoadingListener(this);
        xrv.refresh();
    }

    @Override
    public void onRefresh() {
        page = 0;
        new Handler().postDelayed(() -> {
            listData.clear();
            for (int i = 0; i < 15; i++) {
                listData.add(new AlphaBean("序号：" + i));
            }
            mAdapter.notifyDataSetChanged();
            xrv.refreshComplete();
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        if (page < 2) {
            new Handler().postDelayed(() -> {
                for (int i = 0; i < 15; i++) {
                    listData.add(new AlphaBean("序号：" + (1 + listData.size())));
                }
                xrv.loadMoreComplete();
                mAdapter.notifyDataSetChanged();
            }, 1000);
        } else {
            new Handler().postDelayed(() -> {
                for (int i = 0; i < 9; i++) {
                    listData.add(new AlphaBean("序号：" + (1 + listData.size())));
                }
                xrv.setNoMore(true);
                mAdapter.notifyDataSetChanged();
            }, 1000);
        }
        page++;
    }
}
