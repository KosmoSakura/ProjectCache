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
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

import mos.kos.cache.R;

@SuppressLint("NewApi")
public class LatinKeyboard extends Keyboard {

    private Key mEnterKey;
    
    public LatinKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public LatinKeyboard(Context context, int layoutTemplateResId, 
            CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    @Override
    /* 
     * 描绘键盘时候(由构造函数 )自动调用 
     * */  
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y, 
            XmlResourceParser parser) {
        Key key = new LatinKey(res, parent, x, y, parser);
      //重载的目的，好像仅仅是为了记录回车键的值而已（以Key型记录）  
        //无非就是想对回车键做改观  
        if (key.codes[0] == 10) {
            mEnterKey = key;
        }
        return key;
    }
    
    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     * 这看起来目前的输入法选项编辑器,设置适当的标签在键盘上的回车键(如果它有一个)。
     */
    void setImeOptions(Resources res, int options) {
    	//在SoftKeyboard的StartInput函数最后用到了  
        //传入了EditorInfo.imeOptions类型的options参数。
    	//此变量地位与EditorInfo.inputType类似。但作用截然不同  
        if (mEnterKey == null) {
            return;
        }
        //惊爆：只要加载了EditorInfo的包，就可以使用其中的常量，所熟知的TextView类中的常量，
        //经过试验也是可以任意使用的，猜测这些都是静态变量
        
        switch (options&(EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;//把图片设为空，并不代表就是空，只是下面的Lable可以代替
                mEnterKey.label = res.getText(R.string.label_go_key);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_next_key);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                mEnterKey.icon = res.getDrawable(
                        R.drawable.sym_keyboard_search);
                mEnterKey.label = null;
                break;
            case EditorInfo.IME_ACTION_SEND:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_send_key);
                break;
            default:
                mEnterKey.icon = res.getDrawable(
                        R.drawable.sym_keyboard_return);
                mEnterKey.label = null;
                break;
        }
    }
    
    static class LatinKey extends Key {
        
        public LatinKey(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }
        
        /**
         * Overriding this method so that we can reduce the target area for the key that
         * closes the keyboard. 
         * 重写这个方法,这样我们可以减少目标区域的钥匙关闭键盘。
         */
        @Override
        public boolean isInside(int x, int y) {
            return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
          //只有一个左下角cancel键跟super的此函数不一样，其余相同  
            //仅仅为了防止错误的点击？将cancel键的作用范围减小了10，其余的，如果作用到位，都返回true  
        }
    }

}
