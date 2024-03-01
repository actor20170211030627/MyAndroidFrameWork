package com.actor.myandroidframework.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.actor.myandroidframework.R;

/**
 * Description: 从底部弹出的 非全屏 Activity <br />
 * 使用:
 * <ol>
 *     <li>继承本类, 例 MyBaseBottomActivity extends BaseBottomActivity</li>
 *     <li>
 *         在清单文件中设置主题(因为即使本类设置了theme, 子类也没效果, 至少背景不透明), 示例:
 *         <pre>
 *         &lt;activity
 *             android:name=".activity.MyBaseBottomActivity"
 *             android:theme="@style/BaseBottomActivity"/&gt;
 *         </pre>
 *     </li>
 *     <li>
 *         打开你的Activity:
 *         <pre>
 *             startActivity(new Intent(activity, MyBaseBottomActivity.class));
 *             //需要重写进入动画, 从底部弹出
 *             overridePendingTransition({@link R.anim#bottom_slide_in R.anim.bottom_slide_in}, 0);
 *         </pre>
 *     </li>
 * </ol>
 *
 * @Author     : ldf
 * @Date       : 2019/8/22 on 00:15
 */
public class BaseBottomActivity extends ActorBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
//        setTheme(R.style.BaseBottomActivity);//代码中设置无效啊??
    }

    /**
     * 设置背景透明度
     * @param trans 1.０全透明．０不透明．
     */
    protected void setDimAmount(@FloatRange(from = 0.0, to = 1.0) float trans) {
        Window window = getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
//        windowParams.alpha = trans;   //这是设置界面的透明度, 不是背景
        windowParams.dimAmount = trans; //背景透明度
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//弹起后, 状态栏变暗
    }

    @Override
    protected int getNextExitAnim() {
        return R.anim.bottom_slide_out;
    }

    @Override
    protected int getPreReEnterAnim() {
        return 0;
    }
}
