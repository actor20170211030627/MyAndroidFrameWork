package com.actor.picture_selector.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.actor.myandroidframework.utils.toaster.ToasterUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener;

import java.util.List;

/**
 * description: 录音回调事件
 *
 * @author : ldf
 * date       : 2023/4/2 on 21
 * @version 1.0
 */
public class OnRecordAudioInterceptListenerImpl implements OnRecordAudioInterceptListener {
    @Override
    public void onRecordAudio(Fragment fragment, int requestCode) {
        String permission = Permission.RECORD_AUDIO;
        FragmentActivity activity = fragment.getActivity();
        if (activity == null) return;
        boolean granted = XXPermissions.isGranted(activity, permission);
        if (granted) {
            PictureSelectorUtils.recordAudio(fragment, requestCode);
        } else {
            boolean isDoNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(activity, permission);
            if (isDoNotAskAgain) {
                ToasterUtils.warning("请在设置开启录音权限.");
                XXPermissions.startPermissionActivity(fragment, permission);
            } else {
                XXPermissions.with(fragment).permission(permission).request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (allGranted) {
                            PictureSelectorUtils.recordAudio(fragment, requestCode);
                        } else {
                            ToasterUtils.warning("未获取到录音权限.");
                        }
                    }
                });
            }
        }
    }
}
