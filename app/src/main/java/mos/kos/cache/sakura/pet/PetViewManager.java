package mos.kos.cache.sakura.pet;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.Random;



public class PetViewManager {
    private float startX;
    private float startY;
    private float x0;
    private float y0;
    private int count = 0;
    private int state = PetState.STAND;
    private boolean isLive = false;

    private Context context;
    private PetView petView;
    private MenuView menuView;
    private WindowManager windowManager;

    private static PetViewManager instance = null;

    private MoveAction moveToLeft = new MoveAction();
    private MoveAction moveToRight = new MoveAction();

    WindowManager.LayoutParams petViewLayoutParams;
    WindowManager.LayoutParams menuViewLayoutParams;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    moveToLeft();
                    break;
                case 1:
                    moveToRight();
                    break;
                default:
                    break;
            }
        }
    };

    private View.OnTouchListener petViewTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    x0 = event.getRawX();
                    y0 = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getRawX();
                    float y = event.getRawY();
                    float dx = x - startX;
                    float dy = y - startY;
                    petViewLayoutParams.x += dx;
                    petViewLayoutParams.y += dy;
                    petView.setDragState(true);
                    windowManager.updateViewLayout(petView, petViewLayoutParams);
                    startX = x;
                    startY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    float x1 = event.getRawX();
                    petView.setDragState(false);
                    petView.setPetState(PetState.STAND);
                    if (Math.abs(x1 - x0) > 6) {
                        return true;
                    } else {
                        return false;
                    }
            }
            return false;
        }
    };

    public static PetViewManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PetViewManager.class) {
                if (instance == null) {
                    instance = new PetViewManager(context);
                }
            }
        }
        return instance;
    }

    private PetViewManager(final Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        petView = new PetView(this.context);
        petView.setOnTouchListener(petViewTouchListener);
        petView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(petView);
                isLive = false;
                showMenuView();
                menuView.startAnimation();
            }
        });
        menuView = new MenuView(this.context);
    }

    public void showPetView() {
        if (petViewLayoutParams == null) {
            petViewLayoutParams = new WindowManager.LayoutParams();
            petViewLayoutParams.width = petView.width;
            petViewLayoutParams.height = petView.height;
            petViewLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            petViewLayoutParams.x = 0;
            petViewLayoutParams.y = 0;
            petViewLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            petViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            petViewLayoutParams.format = PixelFormat.RGBA_8888;//RGBA_8s888
        }
        windowManager.addView(petView, petViewLayoutParams);
        isLive = true;
    }

    public void showPetAnim() {
        state = new Random().nextInt(3);
        if (state == PetState.STAND) {
            petView.setPetState(PetState.STAND);
            petView.startStandAnim(this);
        } else if (state == PetState.WALK_TO_RIGHT) {
            petView.setPetState(PetState.WALK_TO_RIGHT);
            petView.startWalkToRightAnim(this);
        } else if (state == PetState.WALK_TO_LEFT) {
            petView.setPetState(PetState.WALK_TO_LEFT);
            petView.startWalkToLeftAnim(this);
        }
    }

    public void moveToRight() {
        if (count < 30) {
            petViewLayoutParams.x += 2;
            petViewLayoutParams.y += 1;
            if (isLive) {
                windowManager.updateViewLayout(petView, petViewLayoutParams);
            }
        } else {
            count = 0;
        }
        count++;
    }

    public void moveToLeft() {
        if (count < 30) {
            petViewLayoutParams.x -= 2;
            petViewLayoutParams.y += 1;
            if (isLive) {
                windowManager.updateViewLayout(petView, petViewLayoutParams);
            }
        } else {
            count = 0;
        }
        count++;
    }

    public void showMenuView() {
        if (menuViewLayoutParams == null) {
            menuViewLayoutParams = new WindowManager.LayoutParams();
            menuViewLayoutParams.width = getScreenWidth();
            menuViewLayoutParams.height = getScreenHeight() - getStatusHeight();
            menuViewLayoutParams.gravity = Gravity.BOTTOM;
            menuViewLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            menuViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            menuViewLayoutParams.format = PixelFormat.RGBA_8888;
        }
        windowManager.addView(menuView, menuViewLayoutParams);
    }

    public void hideMenuView() {
        if (menuViewLayoutParams != null) {
            windowManager.removeView(menuView);
        }
    }

    public int getScreenWidth() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.x;
    }

    public int getScreenHeight() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.y;
    }

    public int getStatusHeight() {
        try {
            Class<?> aClass = Class.forName("com.android.internal.R$dimen");
            Object object = aClass.newInstance();
            Field field = aClass.getField("status_bar_height");
            int x = (Integer) field.get(object);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            return 0;
        }
    }

    public void resetCount() {
        count = 0;
    }

    public class MoveAction implements Runnable {

        @Override
        public void run() {
            if (count < 30) {
                if (state == PetState.WALK_TO_LEFT) {
                    mHandler.sendEmptyMessage(0);
                    mHandler.postDelayed(moveToLeft, 50);
                } else if (state == PetState.WALK_TO_RIGHT) {
                    mHandler.sendEmptyMessage(1);
                    mHandler.postDelayed(moveToRight, 50);
                }
            } else {
                mHandler.removeCallbacks(moveToLeft);
                mHandler.removeCallbacks(moveToRight);
            }
        }
    }

    public void startMove() {
        if (state == PetState.WALK_TO_LEFT) {
            mHandler.postDelayed(moveToLeft, 50);
        } else if (state == PetState.WALK_TO_RIGHT) {
            mHandler.postDelayed(moveToRight, 50);
        }
    }

}
