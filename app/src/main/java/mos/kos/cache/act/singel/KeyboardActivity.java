package mos.kos.cache.act.singel;

import android.text.InputType;
import android.widget.EditText;

import mos.kos.cache.R;
import mos.kos.cache.init.BaseActivity;
import mos.kos.cache.sakura.KeyboardHelper;

public class KeyboardActivity extends BaseActivity {
    private EditText edit;

    @Override
    protected int layout() {
        return R.layout.activity_keyboard;
    }

    @Override
    protected void basis() {
        edit = this.findViewById(R.id.edit);

        edit.setOnClickListener(this);
        edit.setInputType(InputType.TYPE_NULL);
    }

    @Override
    protected void action(int ids) {
        KeyboardHelper.shared(KeyboardActivity.this, edit).showKeyboard();
    }


    @Override
    protected void logic() {
    }

}
