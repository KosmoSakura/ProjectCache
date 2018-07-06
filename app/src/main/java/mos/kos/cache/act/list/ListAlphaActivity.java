package mos.kos.cache.act.list;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import mos.kos.cache.R;
import mos.kos.cache.data.AlphaBean;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.logic.AlphaAdapter;
import x.rv.XRecyclerView;

public class ListAlphaActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private AlphaAdapter mAdapter;
    private ArrayList<AlphaBean> listData;
    private int refreshTime = 0;
    private int page = 0;

    @Override
    protected int layout() {
        return R.layout.activity_list_alpha;
    }

    @Override
    protected void basis() {
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.back_to_top).setOnClickListener(this);

        listData = new ArrayList<>();
        mAdapter = new AlphaAdapter(listData);
        boolean stytle = getIntent().getBooleanExtra("list_style", true);
        if (stytle) {
            initXrv(mAdapter, R.id.recyclerview);
        } else {
            initXrvGrid(mAdapter, R.id.recyclerview, 3);
        }
    }


    @Override
    protected void logic() {
        LinearLayout alpha_title = findViewById(R.id.alpha_title);
        xrv.setScrollAlphaChangeListener(new XRecyclerView.ScrollAlphaChangeListener() {
            @Override
            public void onAlphaChange(int alpha) {
//                ULog.d("alpha:" + alpha);
                if (alpha < 10) {
                    alpha_title.setVisibility(View.GONE);
                } else {
                    alpha_title.setVisibility(View.VISIBLE);
                    alpha_title.getBackground().setAlpha(alpha);
                }
            }

            @Override
            public int setLimitHeight() {
                return 1300;
            }
        });
        xrv.setLoadingListener(this);
        xrv.refresh();
    }

    @Override
    protected void action(int ids) {
        switch (ids) {
            case R.id.back:
                finish();
                break;
            case R.id.back_to_top:
                xrv.scrollToPosition(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        refreshTime++;
        page = 0;
        new Handler().postDelayed(() -> {
            listData.clear();
            for (int i = 0; i < 15; i++) {
                listData.add(new AlphaBean("序号：" + i + "在 " + refreshTime + " 次后刷新"));
            }
            mAdapter.notifyDataSetChanged();
            xrv.refreshComplete();
        }, 1000);            //refresh data here
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
