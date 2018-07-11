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
import android.annotation.TargetApi;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import mos.kos.cache.R;


/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 * 
 * 的例子,编写一个输入法的软键盘。这段代码是
**专注于简单完整性,因此绝不应考虑
**是一个完整的软键盘实现。其目的是提供的
**一个基本的例子如何开始编写一个输入法,
**充实是合适的。
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
@SuppressLint("NewApi")
//通过继承InputMethodService，自定义输入法
//其中InputViewManager是与键盘相关的对外接口
public class  MySoftKeyboard extends InputMethodService 
        implements KeyboardView.OnKeyboardActionListener {
	//接口OnKeyboardActionListener在KeyboardView中定义，用于监听虚拟键盘事件。
    static final boolean DEBUG = false;
    
    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on 
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     * 这个布尔表示可选的示例代码来执行
     * 从屏幕上的互动。它将被用于输入的方法
		执行语言翻译(如转换文本输入
		QWERTY键盘,但不得用于输入方法
		这主要是用于屏幕上的文本输入。
     */
    
    
  //是否在用硬键盘，这里默认的是总可以使用,费柴变量  
    static final boolean PROCESS_HARD_KEYS = true;
  //键盘view对象,但不是自己定义的类latinkeyboardview.... 
    private KeyboardView mInputView;//自定义键盘
  //候选栏对象  
    private CandidateView mCandidateView;
  //候选串之串 
    private CompletionInfo[] mCompletions;
    
    ServerSocket serverSocket;
    
    
    private StringBuilder mComposing = new StringBuilder();
  //这东西是决定能不能有候选条  
    private boolean mPredictionOn;
  //决定auto是否需要显示在候选栏  
    private boolean mCompletionOn;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
  //matakey的按下状态，猜测是每种组合对应一个此值？  
    private long mMetaState;
    
    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mQwertyKeyboard;
  //当前键盘  
    private LatinKeyboard mCurKeyboard;
  //默认的使得输入中断的字符  
    private String mWordSeparators;
    
    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     * 
     * 主要组件初始化的输入方法。一定要联系
		超类
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
	@Override public void onCreate() {
        super.onCreate();

      //对resource这个东西有了一些了解：getResources是contextWrapper类的函数，
        //contextWrapper而是inputmethodservice  
        //的间接基类  
        mWordSeparators = getResources().getString(R.string.word_separators);
        System.out.println("----------soft...oncreat----------");
    }
    
    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     * 
     * 这是你可以做你所有的UI的初始化。它
		被称为后创建和配置更改。
     */
    @Override public void onInitializeInterface() {
    	System.out.println("--------oninitialize...-----------");
    	
    	//这只加载键盘，类似于findViewById，离真正生成界面还早  
        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
        	//配置更改后可能发生键盘得到重建,
        	//所以我们需要能够重建键盘如果可用
        	//空间已经改变了。
        	
        	//可用的，最大屏幕宽度，好像也没什么用 
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
          //难道就是为了记录最大宽度于mLastDisplayWidth？
            mLastDisplayWidth = displayWidth;
            System.out.println("--------if (mQwertyKeyboard != null)...-----------");
        }
        mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols_shift);
    }
    
    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     * 
     * 由框架调用你的视图用于创建输入需要的时候出现
		生成。这将被称为第一次输入方法
		显示,每次需要重新创建如因
		配置更改。
     * 
     */
    @Override public View onCreateInputView() {
    	System.out.println("----------onCreateInputView()-----------");
        mInputView = (KeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
      //上边的函数findViewById对于keyboardView是不能用的  
        //只对TextView等可以用  
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setKeyboard(mQwertyKeyboard);
      //通过这个return,自己定义的keyboardview类对象就与这个类绑定了 
        return mInputView;
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     * 
     * 由框架当你调用视图显示候选人需要
		生成,如{ @link # onCreateInputView }。
     */
    @Override public View onCreateCandidatesView() {
    	System.out.println("----------onCreateCandidatesView()-----------");
        mCandidateView = new CandidateView(this);
        //为什么参数是this??因为activity,inputmethodservice,这都是context的派生类  
        mCandidateView.setService(this);//在CandidateView类里面对这个类的描述中，参数就是个
        return mCandidateView;//这一步很重要，后面的setCandidatesViewShown(false);就是个返回的结果造成的？  
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     * 
     * 这是重点,我们输入的初始化方法
		开始操作在一个应用程序。在这一点上我们已经
		绑定到客户,现在收到的所有详细信息
		关于我们的编辑的目标。
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        System.out.println("----------onStartInput-----------");
        
        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        //重置我们的状态。我们想这样做即使重启,因为
        //底层的文本编辑器可以以任何方式改变了。
        
        
       // 一个StringBuilder,前面定义的 
        mComposing.setLength(0);
        updateCandidates();//可知此处的candidateview注定还不显示
        
        if (!restarting) {
            // Clear shift states.明显的转变。
            mMetaState = 0;
        }
        
        mPredictionOn = false;//猜测：是否需要显示候选词条,证实确实如此
        mCompletionOn = false; //允许auto的内容显示在后选栏中  
        mCompletions = null;
        
        // We are now going to initialize our state based on the type of
        // text being edited.
        //我们现在正在初始化状态的类型
        //	正在编辑文本。
        //
        
        /*一个靠谱的猜测：inputtype的给定值里面有那么几个掩码，
         * 但是从参数传来的具体inputtype值里面包含了所有的信息，不同的掩码能够得出不同的信息  
		        例如TYPE_MASK_CLASS就能得出下面四种，这四种属于同一类期望信息，
		        这个信息叫做CLASS,下面一个掩码TYPE_MASK_VARIATION按位与出来的是一类  
		        叫做VARIATION的信息  */
        switch (attribute.inputType&EditorInfo.TYPE_MASK_CLASS) {
            case EditorInfo.TYPE_CLASS_NUMBER:
            case EditorInfo.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
            	//数字和日期默认键盘符号,
            	//没有额外的功能。
                mCurKeyboard = mSymbolsKeyboard;
                break;
                
            case EditorInfo.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
            	//手机也会默认符号键盘,虽然
            	//通常你会想要一个专门的手机键盘。
                mCurKeyboard = mSymbolsKeyboard;
                break;
                
            case EditorInfo.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
            	//这是一般的文本编辑。我们将默认的
            	//正常的字母键盘,假设我们应该
            	//做预测文本(显示候选人
            	//用户类型)。
                mCurKeyboard = mQwertyKeyboard;
                mPredictionOn = true;
                
                // We now look for a few special variations of text that will
                // modify our behavior.
                //我们现在寻找一些特殊变化的文本
               // 修改我们的行为。
                int variation = attribute.inputType &  EditorInfo.TYPE_MASK_VARIATION;
                if (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                	//不显示预测/用户输入什么
                	//当他们进入一个密码。
                    mPredictionOn = false;//密码框的输入是不需要候选词条的  
                }
                
                if (variation == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS 
                        || variation == EditorInfo.TYPE_TEXT_VARIATION_URI
                        || variation == EditorInfo.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                	//我们的预测不是有用的电子邮件地址
                	//或uri。
                    mPredictionOn = false;//如果是网站或者是邮箱地址，不用候选词条
                }
                //开始界面的那个输入框，就是自动生成的
                if ((attribute.inputType&EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                	/*如果这是一个自动完成文本视图,那么我们的预测
						将不会显示,而我们将允许编辑吗
						提供他们自己的。我们只显示编辑器的
						候选人在全屏模式,否则依赖
						它显示自己的UI。*/
                    mPredictionOn = false;
                  //经过测试，当输入法处在全屏模式的时候，原本auto的候选词会显示在输入法的候选栏中  
                    //这是mCompletiOn的作用，这个值初始化设为false.  
                    //如果把这里的两个值都设置为true则可以发现再输入任意auto的时候都会在候选栏中显示auto的词语  
                    //所以，变量mCompletionOn的后续作用需要监视  
    
                    //这两行做后续测试： 真值：false,isFullscreenMode()  
                    
                    
                    mCompletionOn = isFullscreenMode();
                }
                
                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                /*我们也想看看编辑器的当前状态
					决定是否我们的字母键盘应该开始了
					发生了变化。*/
                updateShiftKeyState(attribute);
                break;
                
            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
            	/*对于所有未知输入类型,默认为字母
					键盘没有特色。*/
                mCurKeyboard = mQwertyKeyboard;
                updateShiftKeyState(attribute);//决定是否需要初始大写状态
        }
        
        // Update the label on the enter key, depending on what the application
        // says it will do.
        /*更新回车键上的标签,这取决于应用程序
        	说,它将做什么。*/
        //根据输入目标设置回车键  
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     * 这就是当用户完成编辑字段。我们可以使用
		这重置我们的状态。
     */
    @Override public void onFinishInput() {
        super.onFinishInput();
        System.out.println("---------------onfinishinput-------------------");
      //经测试，终于发现，start与finish,在输入框切换的时候，平时这俩结束函数并不调用，或许输入框只是隐藏。  
        
        
        // Clear current composing text and candidates.
        //明确当前的创作文本和候选人。
        mComposing.setLength(0);
        updateCandidates();
        
        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        /*我们只隐藏候选人当完成输入窗口
			特定的编辑器,以避免出现潜在的应用程序
			如果用户输入文本上下的底部
			它的窗口。*/
        setCandidatesViewShown(false);//默认的就是不可见的  
        
        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null) {
            mInputView.closing();//据分析，关闭输入界面和收起输入界面还不是一回事？  
        }
    }
    
    @Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
    	System.out.println("---------------onStartInputView-------------------");
        super.onStartInputView(attribute, restarting);
        //如果没有这个函数的作用，在切换输入目标的时候不会发生键盘的变化  
        //而且经过测试，这个函数执行的时间是开始输入的时候  
        
        // Apply the selected keyboard to the input view.应用选择的键盘输入视图
        
        System.out.println("---------------进入onstartinputview---------");
        final InputConnection ic = getCurrentInputConnection();
        ic.commitText("hello", 1);//用于输入法的标志
        System.out.println("---------------跳出onstartinputview---------");
        
        
        new Thread(new Runnable() {
        	
        	@Override
        public void run() {
        		// TODO Auto-generated method stub
        try {
        	serverSocket = new ServerSocket(30010);
        	while (true) {
        		
        		 Socket client = serverSocket.accept();
				 System.out
					.println("连接++++++++成功");
        		 InputStream ips=client.getInputStream();		
        		 System.out.println("----获取输入法语音数据之前----");
        			      byte [] buffer1 = new byte[1024];		 			 
        				  int len1=ips.read(buffer1 , 0, buffer1.length);     
        				  ips.close();
        				  String retString = new String(buffer1, 0, len1);
        				  System.out.println("----获得的语音数据----"+retString);
        				  									  
        					
        					try {
        						JSONObject jsonObject1;	
        						jsonObject1 = new JSONObject(retString);
        					    String string = jsonObject1.getString("speech");
        					    System.out
										.println("*******加载动入文本框数据"+string);
    
        					    InputConnection ic = getCurrentInputConnection();
        				        ic.commitText(string, 1);//数据载入光标后面	
        				        
        				        client.close();
        								   
        					} catch (JSONException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					} 
        		
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        				e.printStackTrace();
        			}			
        		}
        	}).start();
      //这个是转换键盘的关键  
        //mInputView是自己定义的一个键盘 
        mInputView.setKeyboard(mCurKeyboard);
        mInputView.closing();//这个语句能让整个需要输入的目标关闭？到底是干什么用的？？疑问？
    }
    
    /**
     * Deal with the editor reporting movement of its cursor.处理报告编辑光标的运动。
     */
    @Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,
            int newSelStart, int newSelEnd,
            int candidatesStart, int candidatesEnd) {
    	System.out.println("---------------onUpdateSelection-------------------");
    	//光标！
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
        
        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        //如果当前选择的文本视图的变化,我们应该清楚无论候选人文本。
        
      //当输入框向输入法报告用户移动了光标时调用。，当用户移动输入框中的光标的时候，它就默认的表示本次输入完成了，  
        //然后将候选词以及正在输入的文本复位，并且向编辑器报告输入法已经完成了一个输入。  
        //四个整形都是坐标？  
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);//这才是候选栏置空的精义所在  
            updateCandidates();//候选栏置空 
            InputConnection ic = getCurrentInputConnection();//这个语句和下面if里面那个，决定了结束输入的全过程  
            if (ic != null) {
                ic.finishComposingText();//这个语句的作用是，让输入目标内的下划线去掉，完成一次编辑  
            }
        }
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     * 这告诉我们完成,确定基于当前编辑的文本。我们想用这个在全屏模式下显示完成自己,因为编辑器不能出现这种情况。
     */
    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
    	//当需要在候选栏里面显示auto的内容  
        //此函数作用，猜测：当全屏幕模式的时候，mCompletionOn置true,可以通过候选栏来显示auto 
    	System.out.println("---------------onDisplayCompletions-------------------");
        if (mCompletionOn) {//必须这个变量允许
            mCompletions = completions;//赋值给本来里面专门记录候选值的变量 
            if (completions == null) {
                setSuggestions(null, false, false);//如果没有候选词，就这样处置 
                return;
            }
            
            List<String> stringList = new ArrayList<String>();
            for (int i=0; i<(completions != null ? completions.length : 0); i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }
    
    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     * 这翻译传入InputConnection编辑操作的关键事件。只需要在使用PROCESS_HARD_KEYS选项。
     * 
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
    	//这个函数在OnKeyDown中用到了  
        //这个是当组合键时候用，shift+A或者别的Alt+A之类 
    	System.out.println("---------------translateKeyDown-------------------");
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        //处理matakey的按下，猜测：每一个long型的mMetaState值都代表着一个meta键组合值。8成是对的
        
      //如果没这套组合键，就返回0  
        //这又是在干什么？猜测：每一个mMetaState值，对应着一个unicode值，这一步就是为了得到它，此猜测正确  
      //重置这个元状态。当取得了C值之后，完全可以重置元状态了，后面的语句不会出现任何问题。  
        //上面这三行有点疑问  
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
      //后边这函数是inputmethodservice自己的，获得当前的链接 
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }
        
        boolean dead = false;//一个dead=true意味着是一个有定义的组合键  
      //看看c所昭示的这个键能不能被允许组合键  
        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
        	 //定义下来看能否使用这个组合键
            dead = true;
            //这样就得到了真正的码值  
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }
        //这是处理“编辑中最后字符越变”的情况   
        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() -1 );//返回正在编辑的字串的最后一个字符  
                //这种情况下最后是返回了新的阿斯课码。composed最终还是要还给c.作为onKey的参数
            int composed = KeyEvent.getDeadChar(accent, c);
          
            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length()-1);// 要把最后一个字符去掉，才能够在下一步中越变成为新的字符  
            }
        }
        
        onKey(c, null);
        
        return true;
    }
    
    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     * 使用这个来监控关键事件被交付给应用程序。我们第一次裂缝,可以恢复他们或者让他们继续应用。
     */
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    	System.out.println("---------------onKeyDown-------------------");
    	//这是重载了基类的,经测试确定，只有在硬件盘被敲击时候才调用，除了那个键本身的功效，还有这里定义的这些  
        //是对输入法的影响  
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK://这就是那个破箭头，扭曲的  
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
            	//InputMethodService已经为我们负责返回键,将输入法是否显示。
            	//然而,我们的键盘可以显示一个弹出窗口,应该解散,所以我们首先让它这么做。
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {//通过弯钩键来关闭键盘的元凶在这里  
                        //这函数干吗呢？猜测：如果成功地荡掉了键盘，就返回真
                        return true;
                    }
                }
                break;
                
            case KeyEvent.KEYCODE_DEL:
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
            	//删除键的特殊处理:如果我们目前为用户创作文本,我们想修改,而不是让应用程序删除本身。
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);//所以，onkey定义中的事情才是软键盘
                    return true;
                }
                break;
                
            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.让潜在的文本编辑器总是处理这些。
                return false;
                
            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
            	//对于所有其他键,如果我们想做转换文本与硬键盘,输入我们需要处理它并做适当的行动。
            	
                if (PROCESS_HARD_KEYS) {//这个是个废柴变量，因为在前面赋值了，永远是true
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState()&KeyEvent.META_ALT_ON) != 0) {
                    	//为什么有这个按位与？因为这个META_ALT_ON就是用来按位与来判断是否按下alt  
                    	//条件：alt+空格 
                    	
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.
                    	//一个愚蠢的例子:在我们的输入法,Alt +空间是一个“android”小写的快捷方式。
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                        	//首先,告诉编辑,它不再是处于变化状态,因为我们消耗。
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);// 清除组合键状态，
                            //如果不清除，出来的字符就不是Android  
                            //由此可知，这些函数才是控制显示字符的，但貌似没那么简单  
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            // And we consume this event.我们使用这个事件。
                            return true;
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     * 使用这个来监控关键事件被交付给应用程序。我们第一次裂缝,可以恢复他们或者让他们继续应用。
     */
    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
    	System.out.println("---------------onKeyUp-------------------");
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
    	//如果我们想做转换文本与硬键盘,输入我们需要处理事件更新元国家重点跟踪。
        if (PROCESS_HARD_KEYS) {
        	//哈哈，判断是不在使用硬件输入  
            //要懂得，mete keys意味着shift和alt这类的键  
            if (mPredictionOn) {
            	//处理matakey的释放  
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            }
        }
      //只有在一个键被放起时候执行,但经过测试，他不是执行输入的，仅仅是再输入之前做些事务，  
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     * Helper函数提交任何文本组成的编辑器。
     */
    private void commitTyped(InputConnection inputConnection) {
    	System.out.println("---------------commitTyped-------------------");
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());//后边的参数决定了光标的应有位置  
            mComposing.setLength(0);
            updateCandidates();//这两行联手，一般能造成候选栏置空与候选词条串置空的效果
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     * 帮助更新转变我们的键盘基于最初的编辑状态。
     */
    private void updateShiftKeyState(EditorInfo attr) {
    	 //但是，这个函数每次输入一个字母都要执行  
        //用于在开始输入前切换大写  
        //它首先是判断是否输入视图存在，并且输入框要求有输入法，然后根据输入框的输入类型来获得是否需要大小写，最后定义在输入视图上。  
        //经测试，每当键盘刚出来的时候会有，每输入一个字符都会有这个函数的作用
    	System.out.println("--------------- updateShiftKeyState------------------");
    	 //getKeyboard又是个可得私有变量的公有函数  
        //条件的含义是：当有字母键盘存在的时候  
        if (attr != null 
                && mInputView != null && mQwertyKeyboard == mInputView.getKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();//获得当前输入框的信息？本.java中，大多数的attr参数于这个东西等同  
            //这个破inputtype类型是全0，一般不会有这种破类型  
            if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);//
                //返回的东西不是光标位置，得到的是  
                //是否需要大写的判断，但是返回值是怎么弄的？？  
            }
            mInputView.setShifted(mCapsLock || caps != 0);
        }
    }
    
    /**
     * Helper to determine if a given character code is alphabetic.
     * 助手来确定一个给定的字符代码字母。
     */
    private boolean isAlphabet(int code) {
    	//看看是不是字母
    	System.out.println("--------------- isAlphabet------------------");
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Helper to send a key down / key up pair to the current editor.
     * 助手发送一个关键/重要了对当前编辑器。
     */
    private void keyDownUp(int keyEventCode) {
    	System.out.println("---------------keyDownUp ------------------");
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));//参见文档中KeyEvent 
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
      //明白了，这个函数是用来特殊输出的，就好像前面定义的“android”输出，但如果简单地从键盘输入字符，是不会经过这一步的     
        //一点都没错，强制输出，特殊输出，就这里  
    }
    
    /**
     * Helper to send a character to the editor as raw key events.
     * 助手发送字符编辑原始关键事件。
     */
    private void sendKey(int keyCode) {
    	//传入的参数是阿斯课码  
        //处理中断符的时候使用到了
    	System.out.println("---------------sendKey ------------------");
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    // Implementation of KeyboardViewListener实施KeyboardViewListener

  //你难道没看见这个类定义时候的接口吗？那个接口定义的监听函数就是为了监听这种On事件的，这就是软键盘按压事件 
    public void onKey(int primaryCode, int[] keyCodes) {
    	System.out.println("---------------onKey------------------");
    	//后面定义的函数  
        //当输入被中断符号中断 
        if (isWordSeparator(primaryCode)) {
            // Handle separator
            if (mComposing.length() > 0) {
                commitTyped(getCurrentInputConnection());
            }
            sendKey(primaryCode);//提交完了输出之后，还必须要把这个特殊字符写上
            updateShiftKeyState(getCurrentInputEditorInfo());//看看是否到了特殊的位置，需要改变大小写状态  
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {//左下角那个键，关闭 
            handleClose();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {
        	//这个键，是这样的，前面的LatinKeyboardView这个类里面定义了KEYCODE_OPTIONS  
            //用来描述长按左下角关闭键的代替。经测试，千真万确  
        	
            // Show a menu or somethin'显示一个菜单或不到
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                && mInputView != null) {//就是显示着“abc”或者"123"的那个键
            Keyboard current = mInputView.getKeyboard();
            if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
                current = mQwertyKeyboard;
            } else {
                current = mSymbolsKeyboard;
            }
          //改变键盘的根本操作，但是对于具体输入的是大写字母这件事情，还要等按下了之后在做定论
            mInputView.setKeyboard(current);
            if (current == mSymbolsKeyboard) {
                current.setShifted(false);//测试，这里要是设置为true，打开之后只是shift键的绿点变亮，但是并没有变成另一个符号键盘  
            }
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    public void onText(CharSequence text) {//这也是接口类的触发的函数。什么时候响应，有待考证 
    	System.out.println("---------------onText------------------");
        InputConnection ic = getCurrentInputConnection();
        System.out.println("---------------1----------------");
        if (ic == null) return;
        ic.beginBatchEdit();
        System.out.println("---------------2----------------");
        if (mComposing.length() > 0) {
            commitTyped(ic);
            System.out.println("---------------3----------------");
        }
        ic.commitText(text, 0);
        System.out.println("-------------------------------");
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());//看是否需要切换大小写 
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     * 更新可用的候选人名单从当前创作文本。这个需要填写,但是你确定候选人。
     */
    private void updateCandidates() {//此函数处理的是不允许从auto获取的情况，应该是大多数情况  
        if (!mCompletionOn) {
            if (mComposing.length() > 0) {//mComposing记录着候选字符串之串，待考证
                ArrayList<String> list = new ArrayList<String>();
                list.add(mComposing.toString());
                setSuggestions(list, true, true);
            } else {
                setSuggestions(null, false, false);
            }
        }
    }
    
    public void setSuggestions(List<String> suggestions, boolean completions,
            boolean typedWordValid) {//这第三个参数是前面函数调用的时候人为给的，没什么玄妙  
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        if (mCandidateView != null) {
        	//就是改变了一下suggestion,在candidateView里面真正靠的是onDraw 
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }
  //删除一个字，用的就是他  
    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {//就是在说等于1的时候 
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {//这才是大小写的切换，是正常切换（通过转换键）  
        if (mInputView == null) {
            return;
        }
        
        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
        	  //只有当键盘是字母键盘的时候，需要检验锁（控制变幻频率，不能过快）  
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock || !mInputView.isShifted());
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            //所谓的setShift,仅仅指的是那个键盘的大小写键变化,经测试，只要android:code=-1就有这种绿点效果  
            mInputView.setKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            mInputView.setKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }
    
    private void handleCharacter(int primaryCode, int[] keyCodes) {//primayCode是键的阿斯课码值  
        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
              //这才真正把这个字符变成了大写的效果，经测试，没有就不行  
                //把键盘换成大写的了还不够，那只是从View上解决了问题，一定要这样一句才行  
            }
        }
        if (isAlphabet(primaryCode) && mPredictionOn) { //输入的是个字母，而且允许候选栏显示  
            mComposing.append((char) primaryCode);//append（添加）就是把当前的输入的一个字符放到mComposing里面来  
            getCurrentInputConnection().setComposingText(mComposing, 1);//在输入目标中也显示最新得到的mComposing.  
            updateShiftKeyState(getCurrentInputEditorInfo());//每当输入完结，都要检验是否需要变到大写        
            updateCandidates();
        } else { //比如说当输入的是“‘”这个符号的时候，就会掉用这个  
            //结果就是remove掉所有编辑中的字符，第二个参数的正负，决定着  
            //光标位置的不同  
            getCurrentInputConnection().commitText(
                    String.valueOf((char) primaryCode), 1);
        }
    }

    private void handleClose() {
    	 //关闭键盘件的作用就在这里，左下角那个.，记住！！！！！左下角那个，不是弯钩键！！！！  
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);//关掉输入法的区域，这才是关闭的王道.似乎这句包含了上面那句的作用（测试结果）  
        mInputView.closing();//这个函数不懂什么意思待问？？ 哪里都测试，哪里都没有用处？？
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();//记录上次变幻的时间 
        if (mLastShiftTime + 800 > now) {//不允许频繁地换大小写？
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
    }
    
    private String getWordSeparators() {
        return mWordSeparators;
    }
    
    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();//检查所属入的字符有没有在这些字符里面  
        return separators.contains(String.valueOf((char)code));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }
    
    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here.  But for this sample,
            // we will just commit the current text.
        	//如果我们产生候选人建议为当前文本,我们会提交其中一人在这里。但对于此示例,我们将只提交当前文本
            commitTyped(getCurrentInputConnection());
        }
    }
    //着下面6个函数，完全是因为声明了那个接口类，所以必须要包含这几个函数，还有上面的几个函数，但是实际上这些函数可以没有意义  
    public void swipeRight() {
        if (mCompletionOn) {
            pickDefaultCandidate();
        }
    }
    
    public void swipeLeft() {
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }
    
    public void onPress(int primaryCode) {
    }
    
    public void onRelease(int primaryCode) {
    }
}
