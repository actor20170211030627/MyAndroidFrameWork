package com.actor.sample.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.actor.sample.R;
import com.actor.sample.adapter.SharedElementAdapter;
import com.actor.sample.databinding.ActivitySharedElementBinding;
import com.actor.sample.utils.Global;
import com.actor.sample.utils.ImageConstants;

import java.util.Arrays;

/**
 * Description: 主页->元素共享跳转
 * Author     : ldf
 * Date       : 2020/2/6 on 18:41
 */
public class SharedElementActivity extends BaseActivity<ActivitySharedElementBinding> {

    private           RecyclerView              recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("主页->元素共享跳转");
        recyclerView = viewBinding.recyclerView;

        SharedElementAdapter myAdapter = new SharedElementAdapter(Arrays.asList(ImageConstants.IMAGE_SOURCE));
        myAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            //startActivity & startActivityForResult
            startActivityForResult(new Intent(this, ViewPagerActivity.class)
                    .putExtra(ViewPagerActivity.START_POSITION, position),
                    0, true, view);
        });
        recyclerView.setAdapter(myAdapter);
    }

    /**
     * 4.重写方法, 用于更新动画位置
     */
    @Override
    @NonNull
    public View sharedElementPositionChanged(int oldPosition, int currentPosition) {
        return recyclerView.findViewHolderForAdapterPosition(currentPosition).itemView.findViewById(R.id.iv);
    }

    /***
     * 5.重写方法, 更新位置
     */
    @Override
    public void onSharedElementBacked(int oldPosition, int currentPosition) {
        super.onSharedElementBacked(oldPosition, currentPosition);
        recyclerView.scrollToPosition(currentPosition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();//延时动画
            recyclerView.post(this::startPostponedEnterTransition);//开始延时共享动画
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            showToast(data.getStringExtra(Global.CONTENT));
        }
    }
}
