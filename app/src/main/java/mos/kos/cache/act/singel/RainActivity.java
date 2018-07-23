package mos.kos.cache.act.singel;

import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.SeekBar;

import mos.kos.cache.R;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.sakura.rain.RainView;

/**
 * @Description: <p>
 * @Author: Kosmos
 * @Date: 2018年07月23日 16:56
 * @Email: KosmoSakura@foxmail.com
 */
public class RainActivity extends BaseActivity {
    private SeekBar sbRain;
    private RainView rain;

    private SoundPool soundPool;
    private int sountId;

    @Override
    protected int layout() {
        return R.layout.activity_rain;
    }

    @Override
    protected void basis() {
        sbRain = findViewById(R.id.sb_rain);
        rain = findViewById(R.id.rain_more);

        soundPool = new SoundPool(1,//允许同时存在的声音数量
            AudioManager.STREAM_SYSTEM,//声音流的类型，有STREAM_RING、STREAM_MUSIC,一般都是使用后者
            0);//质量

//        soundPool.load(this, R.raw.voice_rain_brush_earth, 1);//--：1，雨水冲刷大地
        sountId = soundPool.load(this, R.raw.voice_rain_spring, 1);//--2.春雨
//        soundPool.load(this, R.raw.voice_rain_river, 1);//--3,河水
    }

    @Override
    protected void logic() {
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            soundPool.play(sountId,
                0.8f,//左声道音量大小:0.0f - 1.0f,大音量的百分比
                1f,//右声道音量大小
                0,//优先级，值越大优先级越高
                -1,//是否需要循环播放，取值不限,负数表示无穷循环(官方建议，如果无穷循环，用-1，当然-2、-3等也行),
                // 播放次数=循环次数+1。比如0表示循环0次
                1);//播放速率(倍数)，取值0.5f - 2.0f，1表示正常速率播放
        });
        sbRain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rain.setRainNum(progress);
                if (progress % 10 == 0) {
                    switch (progress % 3) {
                        case 0:
                            soundPool.play(1, 1, 1, 0, 2, 1);
                            break;
                        case 1:
                            soundPool.play(2, 1, 1, 0, 2, 1);
                            break;
                        case 2:
                            soundPool.play(3, 1, 1, 0, 2, 1);
                            break;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        soundPool.pause(sountId);
        super.onDestroy();
    }
}
