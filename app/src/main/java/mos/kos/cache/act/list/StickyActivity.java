package mos.kos.cache.act.list;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;

import mos.kos.cache.R;
import mos.kos.cache.data.AlphaBean;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.logic.AlphaAdapter;
import x.rv.StickyScrollLinearLayout;
import x.rv.XRecyclerView;
import x.rv.logic.ProgressStyle;

public class StickyActivity extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private AlphaAdapter mAdapter;
    private ArrayList<AlphaBean> listData;
    private int times = 0;

    @Override
    protected int layout() {
        return R.layout.activity_sticky;
    }

    @Override
    protected void basis() {
        initXR();

        final View topView = findViewById(R.id.topView);
        final View tabView = findViewById(R.id.tabView);
        final View content = findViewById(R.id.contentView);
        final StickyScrollLinearLayout s = findViewById(R.id.StickyScrollLinearLayout);
        s.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                if (s.getContentView() != null)
                    return;
                // 放在这里是为了等初始化结束后再添加，防止 height 获取 =0
                // add from here just in cause they height==0
                s.setInitInterface(new StickyScrollLinearLayout.StickyScrollInitInterface() {
                                       @Override
                                       public View setTopView() {
                                           return topView;
                                       }

                                       @Override
                                       public View setTabView() {
                                           return tabView;
                                       }

                                       @Override
                                       public View setContentView() {
                                           return content;
                                       }
                                   }
                );
            }
        );
    }

    private void initXR() {
        mRecyclerView = (XRecyclerView) findViewById(R.id.XRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // i suggest you do not open pull refresh in StickyScroll
        // it maybe case some new problems
        mRecyclerView.setPullRefreshEnabled(false);

        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                if (times < 2) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for (int i = 0; i < 10; i++) {
                                listData.add(new AlphaBean("item" + (1 + listData.size())));
                            }
                            mRecyclerView.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for (int i = 0; i < 10; i++) {
                                listData.add(new AlphaBean("item" + (1 + listData.size())));
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
        for (int i = 0; i < 20; i++) {
            listData.add(new AlphaBean(" data -- " + i));
        }
        mAdapter = new AlphaAdapter(listData);
        mAdapter.setOnItemClickListener(new AlphaAdapter.ItemClickListener() {
            @Override
            public void onItemClick(AlphaBean bean, int position) {
                // a demo for notifyItemRemoved
                listData.remove(position);
                mRecyclerView.notifyItemRemoved(listData, position);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void logic() {

    }
}
