package mos.kos.cache.init;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import mos.kos.cache.R;
import mos.kos.cache.sakura.whorl.ProgressHUD;
import x.rv.XRecyclerView;
import x.rv.logic.ProgressStyle;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年07月04日 16:59
 * @Email: KosmoSakura@foxmail.com
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressHUD progressHUD;
    /**
     * @1.更新数据： 调用单元刷新记得调：XRecyclerView的函数
     * @ xrv.notifyItemRemoved(list, 0);
     * xrv.notifyItemInserted(list, list.size());
     * ************************************************
     * @2.下拉刷新-开关 true-可以下拉刷新
     * @ xrv.setPullRefreshEnabled(false);
     * ************************************************
     * @3.1.加载更多-开关 true-没有更多
     * @ xrv.setNoMore(true);
     * @3.2. 加载更多-动画
     * ProgressStyle.BallRotate//球旋转
     * xrv.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);//方形旋转
     * ************************************************
     * @4.设置占位页
     * @ xrv.setEmptyView(View);
     * ************************************************
     * @5.1.头部视图-自定义-头部可以添加多个
     * @ View header = LayoutInflater.from(this).inflate(R.layout.lay_head, findViewById(android.R.id.content), false);
     * xrv.addHeaderView(View);
     * @5.2.头部视图-启动默认记录刷新时间
     * @ xrv.getDefaultRefreshHeaderView()
     * .setRefreshTimeVisible(true);
     * ************************************************
     * @6.1.尾部视图-自定义
     * @ xrv.setFootView(View,CustomFooterViewCallBack)
     * @6.2.底部提示语-自定义
     * @ xrv.setFootViewText("自定义加载中提示","自定义加载完毕提示");
     * 分开设置
     * xrv.getDefaultFootView().setLoadingHint("自定义加载中提示 ");
     * xrv.getDefaultFootView().setNoMoreHint("自定义加载完毕提示");
     * ************************************************
     */
    protected XRecyclerView xrv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());
        basis();
        logic();
    }

    protected void initXrv(XAdapter adapter, int res) {
        xrv = findViewById(res);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xrv.setLayoutManager(layoutManager);

        xrv.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xrv.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);//方形旋转
//        xrv.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);//球旋转
        xrv.setArrowImageView(R.drawable.ic_font);
        xrv.setAdapter(adapter);
    }

    protected void initXrvGrid(XAdapter adapter, int res, int spanCount) {
        xrv = findViewById(res);
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        xrv.setLayoutManager(layoutManager);

        xrv.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xrv.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        xrv.setArrowImageView(R.drawable.ic_font);
        xrv.setAdapter(adapter);
    }

    /**
     * @return 返回布局
     */
    protected abstract int layout();

    /**
     * 初始化基础信息
     */
    protected abstract void basis();

    /**
     * 填充逻辑部分 include
     */
    protected abstract void logic();

    protected void action(int ids) {
    }

    @Override
    public final void onClick(View view) {
        action(view.getId());
    }

    protected void progressShow() {
        if (progressHUD == null) {
            progressHUD = new ProgressHUD(this, R.style.PopupWindowListDialog);
        }
        progressHUD.showDialog("加载中...");
    }

    /**
     * @param msg 显示进度条
     */
    protected void progressShow(String msg) {
        if (progressHUD == null) {
            progressHUD = new ProgressHUD(this, R.style.PopupWindowListDialog);
        }
        progressHUD.showDialog(msg);
    }

    protected void progressText(String msg) {
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.setMessage(msg);
        }
    }

    protected void progressHide() {
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.hideDialog();
        }
    }
}
