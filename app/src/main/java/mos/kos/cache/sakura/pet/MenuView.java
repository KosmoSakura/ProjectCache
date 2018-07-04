package mos.kos.cache.sakura.pet;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import mos.kos.cache.R;


public class MenuView extends LinearLayout {

    private LinearLayout linearLayout;
    private TranslateAnimation translateAnimation;


    public MenuView(final Context context) {
        super(context);
        View view = View.inflate(context, R.layout.layout_menu, null);
        linearLayout =  view.findViewById(R.id.menu_layout);
        translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        linearLayout.setAnimation(translateAnimation);
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                PetViewManager petViewManager = PetViewManager.getInstance(context);
                petViewManager.hideMenuView();
                return false;
            }
        });
        addView(view);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startAnimation() {
        translateAnimation.start();
    }
}
