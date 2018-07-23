package mos.kos.cache.sakura.rain;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Description:
 * @Author: Kosmos
 * @Time: 2017/11/28 002814:27
 * @Email:ZeroProject@foxmail.com
 */
public abstract class BaseView extends View {


    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context) {
        super(context);
    }


    protected abstract void init();

    protected abstract void logic();

    protected abstract void drawSub(Canvas canvas);

    @Override
      protected void onDetachedFromWindow() {

        super.onDetachedFromWindow();
    }




}
