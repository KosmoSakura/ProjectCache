package mos.kos.cache.init;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import mos.kos.cache.R;
import mos.kos.cache.sakura.whorl.ProgressHUD;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年07月04日 16:59
 * @Email: KosmoSakura@foxmail.com
 * @Event:
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    private ProgressHUD progressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());
        basis();
        logic();
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

    protected void showProgress() {
        if (progressHUD == null) {
            progressHUD = new ProgressHUD(this, R.style.PopupWindowListDialog);
        }
        progressHUD.showDialog("加载中...");
    }

    protected void showProgress(String msg) {
        if (progressHUD == null) {
            progressHUD = new ProgressHUD(this, R.style.PopupWindowListDialog);
        }
        progressHUD.showDialog(msg);
    }

    protected void hideProgress() {
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.hideDialog();
        }
    }
}
