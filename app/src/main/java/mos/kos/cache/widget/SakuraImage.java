package mos.kos.cache.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.orhanobut.logger.Logger;

import mos.kos.cache.R;

/**
 * @Description: 圆角&比例尺寸
 * @Author: Kosmos
 * @Date: 2018年06月23日 22:21
 * @Email: KosmoSakura@foxmail.com
 * @Event:
 */
public class SakuraImage extends android.support.v7.widget.AppCompatImageView {
    /**
     * 长宽比例:
     * 默认0，（小于0）不按比例绘制
     * 大于0，根据参考边按照比例设置长宽
     */
    private float ratio = 0;//长宽比例
    private int referEdge = 1;//比例参考边：1-width，2-height,默认：1
    private float radius = 0;//圆角弧度
    private int ratioW, ratioH;
    private BitmapShader mBitmapShader;
    private final Matrix mShaderMatrix = new Matrix();
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Path mPath = new Path();

    public SakuraImage(Context context) {
        super(context);
        initViews();
    }

    public SakuraImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SakuraImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SakuraImage, defStyleAttr, 0);
        ratio = typedArray.getFloat(R.styleable.SakuraImage_ratio, 1);
        referEdge = typedArray.getInt(R.styleable.SakuraImage_refer, 1);
        radius = typedArray.getFloat(R.styleable.SakuraImage_radius, 0);
        typedArray.recycle();
        initViews();
    }

    private void initViews() {
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBitmapPaint.setStyle(Paint.Style.FILL);
        mBitmapPaint.setAntiAlias(true);

        Logger.d("比例：" + ratio + ",参考边：" + (referEdge == 1 ? "宽" : "高")
            + "，弧度：" + radius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio > 0) {
            //参考边为：宽
            if (referEdge == 1) {
                ratioW = MeasureSpec.getSize(widthMeasureSpec);
                ratioH = (int) (ratioW * ratio);

            } else {
                ratioH = MeasureSpec.getSize(heightMeasureSpec);
                ratioW = (int) (ratioH * ratio);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(ratioW, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(ratioH, MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        Logger.v("宽高1：ratioW=" + ratioW + ",ratioH=" + ratioH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewRect.top = 0;
        mViewRect.left = 0;
        mViewRect.right = getWidth(); // 宽度
        mViewRect.bottom = getHeight(); // 高度

        setBitmapShader();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setBitmapShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setBitmapShader();
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (radius > 0 && scaleType != ScaleType.CENTER_CROP) {
            throw new IllegalArgumentException(String.format("缩放类型不支持%s。", scaleType));
        }
    }

    private RectF mViewRect = new RectF(); // imageview的矩形区域

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
//                canvas.drawRoundRect(mViewRect, mRoundRadius, mRoundRadius, mBitmapPaint);
            mPath.reset();
            mPath.addRoundRect(mViewRect, radius, radius, Path.Direction.CW);
//            mPath.addRoundRect(mViewRect, new float[]{
//                radius, radius, radius, radius,
//                radius, radius, radius, radius,
//            }, Path.Direction.CW);
            canvas.drawPath(mPath, mBitmapPaint);
        }
    }


    private void setBitmapShader() {
        // super(context, attrs, defStyle)调用setImageDrawable时,成员变量还未被正确初始化
        if (mBitmapPaint == null) {
            return;
        }
        if (mBitmap == null) {
            invalidate();
            return;
        }
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setShader(mBitmapShader);

        // 固定为CENTER_CROP,使图片在ｖｉｅｗ中居中并裁剪
        mShaderMatrix.set(null);
        // 缩放到高或宽与view的高或宽　匹配
        float scale = Math.max(getWidth() * 1f / mBitmap.getWidth(), getHeight() * 1f / mBitmap.getHeight());
        // 由于BitmapShader默认是从画布的左上角开始绘制，所以把其平移到画布中间，即居中
        float dx = (getWidth() - mBitmap.getWidth() * scale) / 2;
        float dy = (getHeight() - mBitmap.getHeight() * scale) / 2;
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate(dx, dy);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
        invalidate();
    }

    /**
     * drawable转换成bitmap
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
