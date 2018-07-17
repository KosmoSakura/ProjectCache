package mos.kos.cache.act;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Window;

import mos.kos.cache.R;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.tool.UDialog;

/**
 * 过度页面
 */
public class SplashActivity extends BaseActivity {
    // Manifest.permission.READ_EXTERNAL_STORAGE,
    private static String[] ABOUT_IFLYTEK = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    private int refusedCount;//拒绝权限的次数

    @Override
    protected int layout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.activity_splash;
    }

    @Override
    protected void basis() {
        refusedCount = 0;
        getPermissions();
    }

    @Override
    protected void logic() {

    }

    private void getPermissions() {
        refusedCount++;
        boolean st = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            for (String permission : ABOUT_IFLYTEK) {
                int status = ActivityCompat.checkSelfPermission(this, permission);
                if (status == PackageManager.PERMISSION_GRANTED) {
                    st = true;
                } else {
                    st = false;
                    break;
                }
            }
        } else {
            st = true;
        }

        if (st) {
            init();
        } else {
            //判断是否拒绝过,true表示拒绝过
//            boolean hasrefuse = ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permission);
            // 申请未有权限
            ActivityCompat.requestPermissions(this, ABOUT_IFLYTEK, 10086);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10086) {
            boolean has = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    has = true;
                } else {
                    has = false;
                    break;
                }
            }

            if (has) {
                init();//成功
            } else {
                UDialog.getInstance(this, false, false)
                    .showNoticeConfirm("警告", "这些为必要的权限", "确定",
                        (result, dia) -> {
                            if (refusedCount > 2) {
                                //引导用户到设置中去进行设置
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivity(intent);
                                finish();
                            } else {
                                getPermissions();
                            }
                            dia.dismiss();
                        });
            }
        }
    }

    private void init() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
