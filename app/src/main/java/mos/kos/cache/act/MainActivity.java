package mos.kos.cache.act;

import android.content.Intent;

import java.util.Timer;
import java.util.TimerTask;

import mos.kos.cache.R;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.sakura.pet.PetViewManager;
import mos.kos.cache.sakura.pet.PetViewService;


/**
 * 主页
 */
public class MainActivity extends BaseActivity {
    private PetViewManager petViewManager;

    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected void basis() {
        findViewById(R.id.bbbb).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_anim).setOnClickListener(this);
        findViewById(R.id.btn_all).setOnClickListener(this);
    }

    @Override
    protected void logic() {
//        startActivity(new Intent(MainActivity.this, ShowActivity.class));
    }

    @Override
    protected void action(int ids) {
        switch (ids) {
            case R.id.bbbb:
                startActivity(new Intent(MainActivity.this, ShowActivity.class));
                break;
            case R.id.btn_show:
                petViewManager = PetViewManager.getInstance(MainActivity.this);
                petViewManager.showPetView();
                break;
            case R.id.btn_anim:
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                /*Intent intent = new Intent();
                intent.setAction("UPDATE_ACTION");
                sendBroadcast(intent);*/
                        petViewManager.showPetAnim();

                    }
                }, 0, 1000 * 4);
                break;
            case R.id.btn_all:
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, PetViewService.class);
                        startService(intent);
//                        finish();
                    }
                }, 0, 1000 * 4);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this, PetViewService.class));
        super.onDestroy();
    }


}
