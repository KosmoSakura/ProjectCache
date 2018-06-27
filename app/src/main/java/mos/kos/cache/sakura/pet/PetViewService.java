package mos.kos.cache.sakura.pet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class PetViewService extends Service {

    private PetViewManager petViewManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        petViewManager = PetViewManager.getInstance(this);
        petViewManager.showPetView();
        updateTime();
        super.onCreate();
    }

    private void updateTime() {
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
}
