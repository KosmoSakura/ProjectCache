package mos.kos.cache.tool;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * @Description: 动画工具类
 * @Author: Kosmos
 * @Date: 2017年7月20日 11:32
 * @Email: KosmoSakura@foxmail.com
 */
public class UAnimValue {

    /**
     * 属性动画：
     * alpha:  ObjectAnimator showAnimation = ObjectAnimator.ofFloat(splasher, "alpha", 0f, 1.0f, 0.8f, 1.0f);
     * scaleX: ObjectAnimator.ofFloat(splasher, "scaleX", 0.0f, 1.0f);
     * scaleY: ObjectAnimator.ofFloat(splasher, "scaleY", 0.0f, 2.0f);
     * translationX: ObjectAnimator.ofFloat(splasher, "translationX", 100, 400);
     * translationY: ObjectAnimator.ofFloat(splasher, "translationY", 100, 750);
     * rotation:  ObjectAnimator.ofFloat(splasher, "rotation", 0, 360);
     */
    public static class ObjectUtil {
        //显-隐-转圈
        public static void showToDismis(View view) {
            ObjectAnimator showAnimation = ObjectAnimator.ofFloat(view, "alpha", 0f, 1.0f, 0f);
            ObjectAnimator dismisAnimation = ObjectAnimator.ofFloat(view, "rotation", 0, 360);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(showAnimation, dismisAnimation);
            animatorSet.setDuration(4000);
            animatorSet.start();
        }
    }

    /**
     * 旋转
     */
    public static class RotationUtil {
        public static void fullscreen(View view) {
            final ViewPropertyAnimatorCompat animate = ViewCompat.animate(view);
            animate.rotationBy(90f); //旋转90°
            animate.scaleX(100f);//X-->100
            animate.scaleY(200f);//Y-->200
            animate.setDuration(500);
            animate.start();
        }

        public static void runRotation(View view) {
            runRotation(view, 180f);
        }

        private static void runRotation(View view, float degree) {
            ViewCompat.animate(view).rotationBy(degree).setDuration(500).start();
        }
    }

    public static class AutoAlphaTwoViews {
        private boolean isOpen = false;

        private View vShort, vLong, vLogo;


        public AutoAlphaTwoViews(View vLong, View vShort, View vLogo) {
            this.vShort = vShort;
            this.vLong = vLong;
            this.vLogo = vLogo;
        }


        public void state() {
            RotationUtil.runRotation(vLogo); //进行旋转

            ValueAnimator valueAnimator = new ValueAnimator();
            if (isOpen) {
                valueAnimator.setIntValues(0, 100);
            } else {
                valueAnimator.setIntValues(100, 0);
            }

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    vShort.setAlpha(value);
                    vLong.setAlpha(100 - value);
                }
            });

            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isOpen) {
                        vLong.setVisibility(View.GONE);
                        vShort.setVisibility(View.VISIBLE);
                    } else {
                        vLong.setVisibility(View.VISIBLE);
                        vShort.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            valueAnimator.setDuration(500);
            valueAnimator.start();
            //状态更改
            isOpen = !isOpen;
        }
    }


    /**
     * 收缩抽屉动画
     */
    public static class AutoHide {
        private int mLayoutHeight;  //动画执行的padding高度-330
        private int x;
        private boolean isOpen = false; //是否开启状态
        private static ViewGroup layer;

        public AutoHide(ViewGroup viewGroup) {
            layer = viewGroup;
            layer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    //移除所有监听
                    layer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    x = layer.getHeight() / 2;
//                    ULoo.kosmos_d("x：" + x);
                    //隐藏当前控件-330px-165
                    if (x <= 200) {
                        layer.setPadding(36, 30, 36, 0);
                    } else if (x > 200 && x <= 500) {
                        mLayoutHeight = x / 2;
                        layer.setPadding(36, 30, 36, -mLayoutHeight);
                    } else {
                        mLayoutHeight = x * 2 - 335;//x + x / 4
                        layer.setPadding(36, 30, 36, -mLayoutHeight);
                    }
//                    if (x <= 200) {
//                        layer.setPadding(36, 30, 36, 0);
//                    } else if (x > 200 && x <= 500) {
//                        mLayoutHeight = x / 2;
//                        layer.setPadding(36, 30, 36, -mLayoutHeight);
//                    } else if (x > 500 && x <= 800) {
//                        mLayoutHeight = x * 2 - 335;//x + x / 4
//                        layer.setPadding(36, 30, 36, -mLayoutHeight);
//                    } else if (x > 600 && x < 800) {
//                        mLayoutHeight = x * 2 - 335;//x + x / 3;//800-1050
//                        layer.setPadding(36, 30, 36, -mLayoutHeight);
//                    } else {
//                        mLayoutHeight = x * 2 - 335;//x + x / 2;
//                        layer.setPadding(36, 30, 36, -mLayoutHeight);
//                    }
//                    ULoo.kosmos_d("mLayoutHeight：" + mLayoutHeight);
                }
            });
        }


        public String startAnim(final View thisBotton) {
            if (x <= 200) {
                return "已全部展示";
            }

            ValueAnimator valueAnimator = new ValueAnimator();
            if (isOpen) {
                valueAnimator.setIntValues(0, -mLayoutHeight);
            } else {
                valueAnimator.setIntValues(-mLayoutHeight, 0);
            }

            //设置监听的值
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    int value = (int) animator.getAnimatedValue();
                    layer.setPadding(36, 30, 36, value);
                }
            });
            //动画执行中监听
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    //动画开始，不能点击
                    thisBotton.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    thisBotton.setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            valueAnimator.setDuration(500);
            valueAnimator.start();
            if (listener != null) {
                listener.onLayerStatusChanged(isOpen);
            }
            //状态更改
            isOpen = !isOpen;

            //进行旋转
            RotationUtil.runRotation(thisBotton, 180f + 360);
            return null;
        }


        public void setOnLayerStatusChangedListener(LayerStatusChangedListener layerStatusChangedListener) {
            this.listener = layerStatusChangedListener;
        }

        private LayerStatusChangedListener listener;

        public interface LayerStatusChangedListener {
            void onLayerStatusChanged(boolean open);
        }

    }

}
