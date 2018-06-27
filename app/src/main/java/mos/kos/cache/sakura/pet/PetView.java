package mos.kos.cache.sakura.pet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import mos.kos.cache.R;


/**
 * 原作者地址：https://github.com/sufushi/MyPet
 */
public class PetView extends View {

    private PetViewManager petViewManager;

    public int width = 200;//宠物宽度
    public int height = 200;//宠物高度

    public WalkAnim walkToRightAnim = new WalkAnim();
    public WalkAnim walkToLeftAnim = new WalkAnim();
    public WalkAnim standAnim = new WalkAnim();

    private boolean isDrag = false;
    private int state = 0;
    private int count = 0;

    private Bitmap ballBitmap;
    private Bitmap petBitmap;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public PetView(Context context) {
        super(context);
        init();

    }

    public PetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        ballBitmap = Bitmap.createScaledBitmap(ball, width, height, true);
        Bitmap pet = BitmapFactory.decodeResource(getResources(), R.drawable.pet);
        petBitmap = Bitmap.createScaledBitmap(pet, width, height, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isDrag) {
            Bitmap pet;
            if (state == PetState.WALK_TO_RIGHT) {
                switch (count % 5) {
                    case 0:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_right1);
                        break;
                    case 1:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_right2);
                        break;
                    case 2:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_right3);
                        break;
                    case 3:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_right4);
                        break;
                    case 4:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_right5);
                        break;
                    default:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.pet);
                }
                petBitmap = Bitmap.createScaledBitmap(pet, width, height, true);
                canvas.drawBitmap(petBitmap, 0, 0, null);
            } else if (state == PetState.WALK_TO_LEFT) {
                switch (count % 5) {
                    case 0:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_left1);
                        break;
                    case 1:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_left2);
                        break;
                    case 2:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_left3);
                        break;
                    case 3:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_left4);
                        break;
                    case 4:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.walk_to_left5);
                        break;
                    default:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.pet);
                }
                petBitmap = Bitmap.createScaledBitmap(pet, width, height, true);
                canvas.drawBitmap(petBitmap, 0, 0, null);
            } else if (state == PetState.STAND) {
                switch (count % 5) {
                    case 0:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.stand_1);
                        break;
                    case 1:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.stand_2);
                        break;
                    case 2:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.stand_3);
                        break;
                    case 3:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.stand_4);
                        break;
                    case 4:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.stand_5);
                        break;
                    default:
                        pet = BitmapFactory.decodeResource(getResources(), R.drawable.pet);
                }
                petBitmap = Bitmap.createScaledBitmap(pet, width, height, true);
                canvas.drawBitmap(petBitmap, 0, 0, null);
            }
        } else {
            canvas.drawBitmap(ballBitmap, 0, 0, null);
            invalidate();
        }

    }

    public void setDragState(boolean dragState) {
        isDrag = dragState;
        invalidate();
    }

    public void setPetState(int state) {
        this.state = state;
    }

    public void startWalkToRightAnim(PetViewManager petViewManager) {
        handler.postDelayed(walkToRightAnim, 50);
        this.petViewManager = petViewManager;
        petViewManager.resetCount();
        petViewManager.startMove();
        //petViewManager.moveToRight();


    }

    public void startWalkToLeftAnim(PetViewManager petViewManager) {
        handler.postDelayed(walkToLeftAnim, 50);
        this.petViewManager = petViewManager;
        petViewManager.resetCount();
        petViewManager.startMove();
        //petViewManager.moveToLeft();
    }

    public void startStandAnim(PetViewManager petViewManager) {
        handler.postDelayed(standAnim, 50);
        this.petViewManager = petViewManager;
        petViewManager.resetCount();
    }

    public class WalkAnim implements Runnable {
        @Override
        public void run() {
            count++;
            if (count < 30) {
                invalidate();
                if (state == PetState.WALK_TO_RIGHT) {
                    handler.postDelayed(walkToRightAnim, 50);
                    //petViewManager.moveToRight();
                } else if (state == PetState.WALK_TO_LEFT) {
                    handler.postDelayed(walkToRightAnim, 50);
                    //petViewManager.moveToLeft();
                } else if (state == PetState.STAND) {
                    handler.postDelayed(standAnim, 50);
                }

            } else {
                handler.removeCallbacks(walkToRightAnim);
                handler.removeCallbacks(walkToLeftAnim);
                count = 0;
            }
        }
    }

}
