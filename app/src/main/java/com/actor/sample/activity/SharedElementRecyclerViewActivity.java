package com.actor.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.sharedelement.BaseSharedElementCallback;
import com.actor.myandroidframework.utils.sharedelement.SharedElementUtils;
import com.actor.sample.R;
import com.actor.sample.adapter.SharedElementAdapter;
import com.actor.sample.databinding.ActivitySharedElementRecyclerViewBinding;
import com.actor.sample.utils.Global;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;

import java.util.List;
import java.util.Map;

/**
 * description: 共享元素 -> RecyclerView测试
 * company    :
 * @author    : ldf
 * date       : 2025/5/10 on 14:18
 */
public class SharedElementRecyclerViewActivity extends BaseActivity<ActivitySharedElementRecyclerViewBinding> {

    private final BaseSharedElementCallback enterSharedElementCallback = new BaseSharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            View iv = myAdapter.getViewByPosition(startPosition, R.id.iv);
            if (iv == null) return;
            names.clear();
            sharedElements.clear();
            String transitionName = Global.getListTransitionName(startPosition, true);
            names.add(transitionName);
            sharedElements.put(transitionName, iv);
            LogUtils.errorFormat("names=%s, sharedElements.size=%d", names, sharedElements.size());
        }
    };
    public static final String   IS_CHANGE_TRANSITION = "IS_CHANGE_TRANSITION";
    private SharedElementAdapter myAdapter;
    private       int            startPosition;
    private       boolean        isChangeTransition = true;

    public static Intent getIntent(Context context, int position, boolean isChangeTransition) {
        return new Intent(context, SharedElementRecyclerViewActivity.class)
                .putExtra(Global.START_POSITION, position)
                .putExtra(IS_CHANGE_TRANSITION, isChangeTransition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->元素共享跳转->RecyclerView");
        Intent intent = getIntent();
        startPosition = intent.getIntExtra(Global.START_POSITION, 0);
        isChangeTransition = intent.getBooleanExtra(IS_CHANGE_TRANSITION, isChangeTransition);
        //
        SharedElementUtils.postponeEnterTransition(this);

        enterSharedElementCallback.setLogPageTag("页面B");

        myAdapter = new SharedElementAdapter(startPosition);
        if (isChangeTransition) {
            myAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    startPosition = position;
                    onBackPressed();
                }
            });
        }
        viewBinding.recyclerView.setAdapter(myAdapter);
        viewBinding.recyclerView.scrollToPosition(startPosition);
    }

    @Override
    public void onBackPressed() {
        if (isChangeTransition) {
//        super.onBackPressed();
            Intent intent = new Intent()
                    .putExtra(Global.POSITION, startPosition)
                    .putExtra(Global.CONTENT, "result ok!");
            SharedElementUtils.finishAfterTransition(mActivity, enterSharedElementCallback, RESULT_OK, intent);
        } else {
            super.onBackPressed();
        }
    }
}