/*
 * Copyright (C) 2008-2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package mos.kos.cache.sakura.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mos.kos.cache.R;

@SuppressLint("WrongCall")
public class CandidateView extends View {

    private static final int OUT_OF_BOUNDS = -1;
  //这个是这个candidateView的宿主类，也就是该view是为什么输入法服务的。 
    private MySoftKeyboard mService;
  //这个是建议。比如说当我们输入一些字母之后输入法希望根据输入来进行联想建议。
    private List<String> mSuggestions;
  //这个是用户选择的词的索引
    private int mSelectedIndex;
    private int mTouchX = OUT_OF_BOUNDS;
  //这个是用来描绘选择区域高亮的一个类  
    private Drawable mSelectionHighlight;
  //键入的word是否合法正确。
    private boolean mTypedWordValid;
  //背景填充区域，决定将要在那个部分显示？ 
    private Rect mBgPadding;

    private static final int MAX_SUGGESTIONS = 32;
    private static final int SCROLL_PIXELS = 20;
  //这个是对于候选词的每个词的宽度 
    private int[] mWordWidth = new int[MAX_SUGGESTIONS];
  //这个是每个候选词的X坐标。 
    private int[] mWordX = new int[MAX_SUGGESTIONS];
  //难道是两个词语之间的间隙？对了！ 
    private static final int X_GAP = 10;
    
    private static final List<String> EMPTY_LIST = new ArrayList<String>();

    private int mColorNormal;
    private int mColorRecommended;
    private int mColorOther;
    private int mVerticalPadding;
    //所有关于绘制的信息，比如线条的颜色等
    private Paint mPaint;
    private boolean mScrolled;
    private int mTargetScrollX;
    
    private int mTotalWidth;
    
    private GestureDetector mGestureDetector;

    /**
     * Construct a CandidateView for showing suggested words for completion.
     * @param context
     * @param attrs
     */
    public CandidateView(Context context) {
    	//activity,inputmethodservice,这都是context的派生类 
        super(context);
        //getResouces这个函数用来得到这个应用程序的所有资源,就连android自带的资源也要如此
        mSelectionHighlight = context.getResources().getDrawable(
                android.R.drawable.list_selector_background);
        //mSelectionHighlight类型是Drawable,而Drawable设置状态就是这样 
        mSelectionHighlight.setState(new int[] {
                android.R.attr.state_enabled,//这行如果去掉，点击候选词的时候是灰色，但是也可以用  
                android.R.attr.state_focused,//用处不明。。。。  
                android.R.attr.state_window_focused,//这行如果去掉，当点击候选词的时候背景不会变成橙色  
                android.R.attr.state_pressed//点击候选词语时候背景颜色深浅的变化，不知深层意义是什么？  
        });

        Resources r = context.getResources();
        
        setBackgroundColor(r.getColor(R.color.candidate_background));
      //设置高亮区域的背景颜色，还是透明的，很美，很美，但为什么是透明的还有待考证?  
        
        //这个颜色，是非首选词的颜色  
        mColorNormal = r.getColor(R.color.candidate_normal);
      //找到了，这个是显示字体的颜色  
        mColorRecommended = r.getColor(R.color.candidate_recommended);
      //这个是候选词语分割线的颜色  
        mColorOther = r.getColor(R.color.candidate_other);
      //这是系统定义的一个整型变量。用就可以了  
        mVerticalPadding = r.getDimensionPixelSize(R.dimen.candidate_vertical_padding);
        
        mPaint = new Paint();
        mPaint.setColor(mColorNormal);
      //这行如果没有，那么字体的线条就不一样  
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(r.getDimensionPixelSize(R.dimen.candidate_font_height));
        mPaint.setStrokeWidth(0);
        
        //用手可以滑动，这是在构造函数里面对滑动监听的重载，猜测，这个函数与onTouchEvent函数应该是同时起作用?  
        
        mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                    float distanceX, float distanceY) {
                mScrolled = true;
                //得到滑动开始的横坐标  
                int sx = getScrollX();
                //加上滑动的距离，这个滑动距离是最后一次call滑动之间的距离，很小，应该 
                sx += distanceX;
                if (sx < 0) {
                    sx = 0;
                }
                if (sx + getWidth() > mTotalWidth) {                    
                    sx -= distanceX;
                }
                //记录将要移动到的位置，后面会用到  
                mTargetScrollX = sx;
              //这是处理滑动的函数，view类的函数。后面一个参数，说明Y轴永远不变,如果你尝试去改变一下，经测试，太好玩了 
                scrollTo(sx, getScrollY());
                //文档中说的是使得整个VIew作废，但是如果不用这句，会发生什么?  
                invalidate();
                return true;
            }
        });
      //这后三行语句不是在GestureDetector函数中的，而是在构造函数中的，当候选View建立成功的时候就已经是下面的状态了  
        //拖动时刻左右两边的淡出效果  
        setHorizontalFadingEdgeEnabled(true);
      //当拖动的时候，依旧可以输入并显示  
        setWillNotDraw(false);
      //作用暂时不明？  
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }
    
    /**
     * A connection back to the service to communicate with the text field
     * @param listener
     */
    public void setService(MySoftKeyboard listener) {
    	 //自己定义的废柴函数，使得私有变量mService的值得以改变  
        mService = listener;
    }
    
    @Override
    public int computeHorizontalScrollRange() {
        return mTotalWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	//猜测：如果第二参数是从onMeasure的参数中来的，就用第二变量  
        //这个函数是个调和宽度函数，一般情况下用参数1值，除非第二个参数给其限制
        int measuredWidth = resolveSize(50, widthMeasureSpec);
        
        // Get the desired height of the icon menu view (last row of items does
        // not have a divider below)
        //得到所需的图标菜单视图的高度(最后一行的物品没有下面的分频器)
        Rect padding = new Rect();
      //吴：高亮区域除了字以外，剩下的空隙，用getPadding得到，或许，这是由list_selector_background决定的。
        mSelectionHighlight.getPadding(padding);
        final int desiredHeight = ((int)mPaint.getTextSize()) + mVerticalPadding
                + padding.top + padding.bottom;
        
        // Maximum possible width and desired height
        //最大可能的宽度和期望的高度
        setMeasuredDimension(measuredWidth,
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    /**
     * If the canvas is null, then only touch calculations are performed to pick the target
     * candidate.
     * 如果画布是null,则只接触计算执行目标候选人。
     */
    @Override
    protected void onDraw(Canvas canvas) {
    	//这是每个View对象绘制自己的函数，重载之。经测试：没有这个函数的重载，则显示不出字来，这个就是用来显示字条  
        if (canvas != null) {
            super.onDraw(canvas);
        }
        mTotalWidth = 0;
        if (mSuggestions == null) return;
        
        if (mBgPadding == null) {
            mBgPadding = new Rect(0, 0, 0, 0);
            if (getBackground() != null) {
                getBackground().getPadding(mBgPadding);
            }
        }
      //第一个词左侧为0,测试知道：这个地方能改变文字的左侧开端  
        int x = 0;
        final int count = mSuggestions.size(); 
        final int height = getHeight();
        final Rect bgPadding = mBgPadding;
        final Paint paint = mPaint;
        final int touchX = mTouchX;//取得被点击词语的横坐标  
        final int scrollX = getScrollX();
        final boolean scrolled = mScrolled;
        final boolean typedWordValid = mTypedWordValid;
        final int y = (int) (((height - mPaint.getTextSize()) / 2) - mPaint.ascent());

        for (int i = 0; i < count; i++) {//开始一个一个地添置候选词，但是本例中，候选词只能有1个？  
            String suggestion = mSuggestions.get(i);
            //获取词语宽度，但是词语的字号又是怎么设定的呢？  
            float textWidth = paint.measureText(suggestion);
            //整体宽度是词语宽度加上两倍间隙
            final int wordWidth = (int) textWidth + X_GAP * 2;

            mWordX[i] = x;
            mWordWidth[i] = wordWidth;
            paint.setColor(mColorNormal);
          //保持正常输出而不受触摸影响的复杂条件 
            if (touchX + scrollX >= x && touchX + scrollX < x + wordWidth && !scrolled) {
                if (canvas != null) {
                	//画布转变位置，按下候选词后，看到的黄色区域是画布处理的位置
                    canvas.translate(x, 0);
                    mSelectionHighlight.setBounds(0, bgPadding.top, wordWidth, height);
                    mSelectionHighlight.draw(canvas);
                    //上面两句是密不可分的，第一步给框，第二步画画，
                   //不知与canvas.translate(x, 0);什么关系。画布与词的显示位置好像没有什么关系  
                    //词的位置的改变在下面处理 
                    canvas.translate(-x, 0);
                }
                mSelectedIndex = i;
            }

            if (canvas != null) {
                if ((i == 1 && !typedWordValid) || (i == 0 && typedWordValid)) {
                	//第一个候选词，设置不同的显示式样，粗体  
                	paint.setFakeBoldText(true);
                    paint.setColor(mColorRecommended);
                } else if (i != 0) {
                    paint.setColor(mColorOther);
                }
                //测试得：这里才能决定词语出现的位置  
                canvas.drawText(suggestion, x + X_GAP, y, paint);
                paint.setColor(mColorOther); 
                canvas.drawLine(x + wordWidth + 0.5f, bgPadding.top, 
                        x + wordWidth + 0.5f, height + 1, paint);
                paint.setFakeBoldText(false);
            }
            x += wordWidth;
        }
        mTotalWidth = x;
        //每个滑动，都会造成mTargetScrollX改变，因为他在动作监听函数里面赋值 
        if (mTargetScrollX != getScrollX()) {
        	//意思是说：只要移动了。难道是说如果在移动完成之后进行的输入，则进行下面操作？  
            //如果在移动完成之后输入，那么mTargetScrollX记录的也是移动最终目标的水平坐标  
            scrollToTarget();
        }
    }
  //这个地方，应该和下面的setSuggestions函数一起看，对于滑动之后一输入就归零的问题，有两个原因，源头  
    //都在setSuggestions函数中，一个是scrollTo(0, 0);这句话，每当输入一个字母，就有了一个新词语，这个新词语  
    //会致使scrollTo(0, 0);的发生。但是就算把这句话注释掉，下面的一句mTargetScrollX = 0;也会使得Ondraw()  
    //这个函数的调用到最后的时候，执行scrollToTarget();产生作用，回复到0位置。 
    private void scrollToTarget() {
        int sx = getScrollX();
        if (mTargetScrollX > sx) {
            sx += SCROLL_PIXELS;
            if (sx >= mTargetScrollX) {
                sx = mTargetScrollX;
                requestLayout();
            }
        } else {
            sx -= SCROLL_PIXELS;
            if (sx <= mTargetScrollX) {
                sx = mTargetScrollX;
                requestLayout();
            }//移动之  。 p.s不要把高亮区与候选栏相混，移动的，是候选栏，高亮区自从生成就亘古不变，直到消失  
        }
        scrollTo(sx, getScrollY());
        invalidate();
    }
    
    @SuppressLint("WrongCall")
	public void setSuggestions(List<String> suggestions, boolean completions,
            boolean typedWordValid) {
    	//此函数本类中出现就一次，会在别的类中调用,没有内部调用 
        clear();
        if (suggestions != null) {
        	//新的建议集合字串就是传过来的这个参数字串。  
            mSuggestions = new ArrayList<String>(suggestions);
        }
      //确定此词是否可用？  
        mTypedWordValid = typedWordValid;
        //每当有新的候选词出现，view就会滑动到初始的位置  
        scrollTo(0, 0);
        mTargetScrollX = 0;
        // Compute the total width
      //onDraw的参数为null的时候，他不再执行super里面的onDraw  
        onDraw(null);
        invalidate();
        requestLayout();//文档：当View作废时候使用 
    }

    public void clear() {
    	 //前面定义了，这是一个空数组，将候选词库弄为空数组  
        mSuggestions = EMPTY_LIST;
        mTouchX = OUT_OF_BOUNDS;
      //把被触摸的横坐标定为一个负数，这样的话就等于没触摸  
        mSelectedIndex = -1;
        invalidate();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent me) {
    	//这是触屏选词工作 
    	
    	 //猜测，如果前面那个滑动监听函数起了作用，就不用再乎这个函数后面的了，这是对的！  
        //文档中这样解释：GestureDetector.OnGestureListener使用的时候，这里会返回  
        //true,后面又说，前面定义的GestureDetector.SimpleOnGestureListener，  
        //是GestureDetector.OnGestureListener的派生类  
    	
        if (mGestureDetector.onTouchEvent(me)) {
            return true;
        }//p.s.经注解忽略测试发现：所有的触摸效果源自这里。如果注解掉，则不会发生滑动

        int action = me.getAction();
        int x = (int) me.getX();
        int y = (int) me.getY();
        mTouchX = x;//被点击词语的横坐标  
  
        //如果后续出现滑动，又会被前面那个监听到的  

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mScrolled = false;
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
        	//选词，经过测试，当向上滑动的时候也是可以选词的  
            if (y <= 0) {
                // Fling up!?
                if (mSelectedIndex >= 0) {
                    mService.pickSuggestionManually(mSelectedIndex);
                    mSelectedIndex = -1;
                }
            }
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            if (!mScrolled) {
                if (mSelectedIndex >= 0) {
                    mService.pickSuggestionManually(mSelectedIndex);//点击选词经测试合格  
                }
            }
            mSelectedIndex = -1;
            removeHighlight();//消除高亮区域  
            requestLayout();//文档：当View作废时候使用  
            break;
        }
        return true;
    }
    
    /**
     * For flick through from keyboard, call this method with the x coordinate of the flick 
     * gesture.
     * 浏览的键盘,调用这个方法的x坐标电影姿态。
     * @param x
     */
    public void takeSuggestionAt(float x) {
    	//本类中只出现了一次，在别的类中有调用
    	//此处也给mTouchX赋了非负值
        mTouchX = (int) x;
        // To detect candidate
        onDraw(null);
        if (mSelectedIndex >= 0) {
            mService.pickSuggestionManually(mSelectedIndex);
        }
        invalidate();
    }

    private void removeHighlight() {//取消高亮区域的显示，等待下次生成  
    	 //把被触摸的横坐标定为一个负数，这样的话就等于没触摸  
        mTouchX = OUT_OF_BOUNDS;
        invalidate();
    }
}
