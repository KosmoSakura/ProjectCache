package mos.kos.cache.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import mos.kos.cache.R;


/**
 * widget
 */
public class MainActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(MainActivity.this, ShowActivity.class));
        findViewById(R.id.bbbb).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ShowActivity.class));
        });
    }


}
