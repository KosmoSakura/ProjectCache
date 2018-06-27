package mos.kos.cache.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import mos.kos.cache.R;
import mos.kos.cache.sakura.pet.PetViewManager;
import mos.kos.cache.sakura.pet.PetViewService;


/**
 * widget
 */
public class MainActivity extends FragmentActivity {
    private PetViewManager petViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startActivity(new Intent(MainActivity.this, ShowActivity.class));
        findViewById(R.id.bbbb).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ShowActivity.class));
        });

        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petViewManager = PetViewManager.getInstance(MainActivity.this);
                petViewManager.showPetView();
            }
        });
        findViewById(R.id.btn_anim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                /*Intent intent = new Intent();
                intent.setAction("UPDATE_ACTION");
                sendBroadcast(intent);*/
                        petViewManager.showPetAnim();

                    }
                }, 0, 1000 * 4);
            }
        });
        findViewById(R.id.btn_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, PetViewService.class);
                        startService(intent);
//                        finish();
                    }
                }, 0, 1000 * 4);
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this, PetViewService.class));
        super.onDestroy();
    }
}
