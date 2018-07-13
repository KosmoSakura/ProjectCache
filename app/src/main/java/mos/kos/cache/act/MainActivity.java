package mos.kos.cache.act;

import android.content.Intent;
import android.os.Handler;

import java.util.ArrayList;

import mos.kos.cache.R;
import mos.kos.cache.act.iflytek.IflytekActivity;
import mos.kos.cache.act.list.ListAlphaActivity;
import mos.kos.cache.act.list.ListToolbarActivity;
import mos.kos.cache.act.list.RefreshActivity;
import mos.kos.cache.act.list.StickyActivity;
import mos.kos.cache.act.singel.CircleActivity;
import mos.kos.cache.act.singel.KeyboardActivity;
import mos.kos.cache.act.singel.PetActivity;
import mos.kos.cache.data.MainBean;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.logic.MainAdapter;
import mos.kos.cache.tool.UDialog;
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
        xrv.setLimitNumberToCallLoadMore(3);
        xrv.getDefaultRefreshHeaderView()
            .setRefreshTimeVisible(true);
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
                case 3://定制键盘
                    startActivity(new Intent(MainActivity.this, KeyboardActivity.class));
                    break;
                case 4://Alpha刷新:
                    lisToGo(ListAlphaActivity.class);
                    break;
                case 5://ToolBar刷新:
                    lisToGo(ListToolbarActivity.class);
                    break;
                case 6://头部添加，头尾刷新:
                    lisToGo(RefreshActivity.class);
                    break;
                case 7://头部添加，头尾刷新:
                    lisToGo(StickyActivity.class);
                    break;
                case 8://讯飞语音输入
                    startActivity(new Intent(MainActivity.this, IflytekActivity.class));
                    break;
            }

        });
        xrv.setLoadingListener(this);
    }

    private void lisToGo(Class cls) {
        UDialog.getInstance(this, true, true)
            .showSelect("选择列表类型", "列表类型", "表格类型", 0
                , (result, dia) -> {
                    Intent intent = new Intent(MainActivity.this, cls);
                    intent.putExtra("list_style", true);
                    startActivity(intent);
                    dia.dismiss();
                }
                , dia -> {
                    Intent intent = new Intent(MainActivity.this, cls);
                    intent.putExtra("list_style", false);
                    startActivity(intent);
                    dia.dismiss();
                });
    }

    private void refreshData() {
        list.clear();
        list.add(new MainBean("图形图片", 1));
        list.add(new MainBean("桌面宠物", 2));
        list.add(new MainBean("定制键盘", 3));
        list.add(new MainBean("列表:Alpha", 4));
        list.add(new MainBean("列表:ToolBar", 5));
        list.add(new MainBean("列表:头部添加，头尾刷新", 6));
        list.add(new MainBean("LinearStickyScrollActivity", 7));
        list.add(new MainBean("讯飞语音输入", 8));
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
        }, 1000);
    }
}
