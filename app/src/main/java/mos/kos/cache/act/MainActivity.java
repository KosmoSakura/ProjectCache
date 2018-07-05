package mos.kos.cache.act;

import android.content.Intent;

import mos.kos.cache.R;
import mos.kos.cache.init.BaseActivity;


/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected void basis() {
        findViewById(R.id.to_list_page).setOnClickListener(this);
        findViewById(R.id.to_image_page).setOnClickListener(this);
        findViewById(R.id.to_pet_page).setOnClickListener(this);
    }

    @Override
    protected void logic() {
//        startActivity(new Intent(MainActivity.this, CircleActivity.class));
    }

    @Override
    protected void action(int ids) {
        switch (ids) {
            case R.id.to_image_page:
                startActivity(new Intent(MainActivity.this, CircleActivity.class));
                break;
            case R.id.to_pet_page:
                startActivity(new Intent(MainActivity.this, PetActivity.class));
                break;
            case R.id.to_list_page:
                showProgress();
                break;
            default:
                break;
        }
    }

}
