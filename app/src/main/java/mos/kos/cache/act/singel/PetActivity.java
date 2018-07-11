package mos.kos.cache.act.singel;

import android.content.Intent;

import java.util.Timer;
import java.util.TimerTask;

import mos.kos.cache.R;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.sakura.pet.PetViewManager;
import mos.kos.cache.sakura.pet.PetViewService;

/**
 * @Description:
 * @Author: Kosmos
 * @Email: KosmoSakura@foxmail.com
 * @eg: <p>
 */
public class PetActivity extends BaseActivity {

    private PetViewManager petViewManager;

    @Override
    protected int layout() {
        return R.layout.activity_pet;
    }

    @Override
    protected void basis() {
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_anim).setOnClickListener(this);
        findViewById(R.id.btn_all).setOnClickListener(this);
    }

    @Override
    protected void logic() {

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(PetActivity.this, PetViewService.class));
        super.onDestroy();
    }

    @Override
    protected void action(int ids) {
        switch (ids) {
            case R.id.btn_show:
                petViewManager = PetViewManager.getInstance(this);
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
                        Intent intent = new Intent(PetActivity.this, PetViewService.class);
                        startService(intent);
//                        finish();
                    }
                }, 0, 1000 * 4);
                break;
        }
    }
}
