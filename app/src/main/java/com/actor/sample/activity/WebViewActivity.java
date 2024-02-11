package com.actor.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;

import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.actor.myandroidframework.widget.webview.BaseWebChromeClient;
import com.actor.myandroidframework.widget.webview.BaseWebViewClient;
import com.actor.sample.R;
import com.actor.sample.bean.Item;
import com.actor.sample.databinding.ActivityWebViewBinding;
import com.actor.sample.info.BaseInfo;
import com.blankj.utilcode.util.GsonUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description: WebView
 *
 * @author : ldf
 * date       : 2020/12/21 on 13:33
 */
public class WebViewActivity extends BaseActivity<ActivityWebViewBinding> {

    private final ValueCallback<String> valueCallback = new ValueCallback() {
        @Override
        public void onReceiveValue(Object value) {
            ToasterUtils.success("h5回调:\n" + value);
        }
    };

    //byte, short, int, long, float, double, String, char, boolean
    private final byte aByte = Byte.MIN_VALUE;  //-128
    private final short aShort = Short.MAX_VALUE;
    private final int anInt = Integer.MAX_VALUE;
    private final long aLong = Long.MAX_VALUE;
    private final float aFloat = Float.MAX_VALUE;
    private final double aDouble = Double.MAX_VALUE;
    private final String string = "This is String!";
//    private final char aChar = Character.MAX_VALUE;   //低版本手机会报错
    private final char aChar = 'c';
    private final boolean  aBoolean = true;
    private final Object aNull   = null;
    private final Object[] array   = {"arr0", 23, true, 'a', 35L, null, valueCallback};
    private final List<Object> list = Arrays.asList(array);
    private final Map<String, Object> map = new LinkedHashMap<>();
    private final BaseInfo<Item> object = new BaseInfo<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list.set(0, "list0");
        map.clear();
        map.put("key0", "map0");
        for (int i = 1; i < array.length; i++) {
            map.put("key" + i, array[i]);
        }
        object.code = 200;
        object.message = "请求成功!";
        object.data = new Item("item0");
        object.data.setLetter("Letter1");

        viewBinding.webView.init(new BaseWebViewClient(), new BaseWebChromeClient());
        viewBinding.webView.addJavascriptInterface(this, "android123");
        viewBinding.webView.loadUrl("file:///android_asset/html_call_java_call_html.html");
    }


    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_alert:
                viewBinding.webView.evaluateJavascript("javascript:alert('你好Alert!')", valueCallback);
                break;
            case R.id.btn_confirm:
                viewBinding.webView.evaluateJavascript("javascript:confirm('你好Confirm!')", valueCallback);
                break;
            case R.id.btn_prompt:
                viewBinding.webView.evaluateJavascript("javascript:prompt('请输入口令:')", valueCallback);
                break;
            case R.id.btn_console_log:
                viewBinding.webView.loadUrl("javascript:console.log('H5打印日志!')");
                break;
            case R.id.btn_set_basic:    //调用h5方法, 传入一些基础参数
                viewBinding.webView.callH5Method("callH5Method_Basic_Test",
                        aByte, aShort, anInt, aLong, aFloat, aDouble, aBoolean, aChar,
                        aNull, string
                );
                break;
            case R.id.btn_set_array:    //调用h5方法, 传入数组[]对象
                viewBinding.webView.callH5MethodByJson("callH5Method_Array_Object", GsonUtils.toJson(array));
                break;
            case R.id.btn_set_list:    //调用h5方法, 传入List对象
                viewBinding.webView.callH5MethodByJson("callH5Method_List_Object", GsonUtils.toJson(list));
                break;
            case R.id.btn_set_map:    //调用h5方法, 传入Map对象
                viewBinding.webView.callH5MethodByJson("callH5Method_Map_Object", GsonUtils.toJson(map));
                break;
            case R.id.btn_set_object:    //调用h5方法, 传入Object对象
                viewBinding.webView.callH5MethodByJson("callH5Method_Object", GsonUtils.toJson(object));
                break;



            case R.id.btn_set_basic_return:    //调用h5方法, 传入一些基础参数, 并有返回值
                viewBinding.webView.callH5Method("callH5Method_Basic_Return_Test", valueCallback,
                        aByte, aShort, anInt, aLong, aFloat, aDouble, aBoolean, aChar,
                        aNull, string
                );
                break;
            case R.id.btn_set_array_return:    //调用h5方法, 传入数组[]对象, 并有返回值
                viewBinding.webView.callH5MethodByJson("callH5Method_Array_Object_Return",
                        GsonUtils.toJson(array), valueCallback);
                break;
            case R.id.btn_set_list_return:    //调用h5方法, 传入List对象, 并有返回值
                viewBinding.webView.callH5MethodByJson("callH5Method_List_Object_Return",
                        GsonUtils.toJson(list), valueCallback);
                break;
            case R.id.btn_set_map_return:    //调用h5方法, 传入Map对象, 并有返回值
                viewBinding.webView.callH5MethodByJson("callH5Method_Map_Object_Return",
                        GsonUtils.toJson(map), valueCallback);
                break;
            case R.id.btn_set_object_return:    //调用h5方法, 传入Object对象, 并有返回值
                viewBinding.webView.callH5MethodByJson("callH5Method_Object_Return",
                        GsonUtils.toJson(object), valueCallback);
                break;
            default:
                break;
        }
    }



    //h5调用Java方法
    @JavascriptInterface
    public void calledByH5() {
        ToasterUtils.success("Java方法成功被h5调用!");
    }
    //h5调用Java方法并传参
    @JavascriptInterface
    public void calledByH5param(int anInt, float aFloat, double aDouble, boolean aBoolean, Object aNull, String string) {
        //123, 456.7f, 789.012, true, null, "This is String!"
        ToasterUtils.success(getStringFormat("Java方法成功被h5调用, 并接收到参数: int=%d, float=%f, double=%f, boolean=%b, object=%s, string=%s",
                anInt, aFloat, aDouble, aBoolean, aNull, string));
    }
    //h5调用Java方法并传参, 并将结果返回给h5
    @JavascriptInterface
    public int calledByH5paramReturn(int a, int b) {
        ToasterUtils.success(getStringFormat("Java方法成功被h5调用, 并接收到参数: a=%d, b=%d", a, b));
        return a + b;
    }
    //h5调用Java方法并传json, 再将json返回给h5
    @JavascriptInterface
    public String calledByH5JsonReturn(String json) {
        ToasterUtils.success(getStringFormat("Java方法成功被h5调用, 并接收到Json: json=%s", json));
        return json;
    }
    /**
     * h5调用Java方法, 等待Java有结果后才回调h5方法
     * @param callbackMethodName Java有结果后要回调h5方法, 这个就是h5回调方法的名称
     * @return 回调1个json给回调方法, 具体回调什么东西, 需要android和h5协商
     */
    @JavascriptInterface
    public void calledByH5CallbackToH5UserInfo(String callbackMethodName) {
        //android端处理一些耗时操作
        Item userInfo = new Item("张三");
        userInfo.setLetter("1234546789@qq.com");
        //然后回调h5方法
        viewBinding.webView.callH5MethodByJson(callbackMethodName, GsonUtils.toJson(userInfo), null);
    }
}