package mos.kos.cache.act.list;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import mos.kos.cache.R;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.init.MyAdapter;
import mos.kos.cache.tool.ALog;
import x.rv.XRecyclerView;
import x.rv.logic.ProgressStyle;

public class ListAlphaActivity extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private ArrayList<String> listData;
    private int refreshTime = 0;
    private int times = 0;

    @Override
    protected int layout() {
        return R.layout.activity_list_alpha;
    }

    @Override
    protected void basis() {
        mRecyclerView = findViewById(R.id.recyclerview);
        LinearLayout alpha_title = findViewById(R.id.alpha_title);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.back_to_top).setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.drawable.ic_font);

        /** ----- alpha change listen test start ----- */
        mRecyclerView.setScrollAlphaChangeListener(new XRecyclerView.ScrollAlphaChangeListener() {
            @Override
            public void onAlphaChange(int alpha) {
                ALog.d("alpha:" + alpha);
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
    }


    @Override
    protected void logic() {
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refreshTime++;
                times = 0;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        listData.clear();
                        for (int i = 0; i < 15; i++) {
                            listData.add("item" + i + "after " + refreshTime + " times of refresh");
                        }
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    }

                }, 1000);            //refresh data here
            }

            @Override
            public void onLoadMore() {
                if (times < 2) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for (int i = 0; i < 15; i++) {
                                listData.add("item" + (1 + listData.size()));
                            }
                            mRecyclerView.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for (int i = 0; i < 9; i++) {
                                listData.add("item" + (1 + listData.size()));
                            }
                            mRecyclerView.setNoMore(true);
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                }
                times++;
            }
        });

        listData = new ArrayList<>();
        mAdapter = new MyAdapter(listData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.refresh();
    }

    @Override
    protected void action(int ids) {
        switch (ids) {
            case R.id.back:
                finish();
                break;
            case R.id.back_to_top:
                mRecyclerView.scrollToPosition(0);
                break;
            default:
                break;
        }
    }
}
