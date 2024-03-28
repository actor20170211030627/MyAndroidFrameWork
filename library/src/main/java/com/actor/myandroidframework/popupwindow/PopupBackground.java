package com.actor.myandroidframework.popupwindow;

/**
 * PopupWindow 背景遮盖层实现类
 */
/*public*/ class PopupBackground implements BasePopupWindow.OnShowListener, BasePopupWindow.OnDismissListener {

        protected float mAlpha;

        public void setAlpha(float alpha) {
            mAlpha = alpha;
        }

        @Override
        public void onShow(BasePopupWindow popupWindow) {
            popupWindow.setActivityAlpha(mAlpha);
        }

        @Override
        public void onDismiss(BasePopupWindow popupWindow) {
            popupWindow.setActivityAlpha(1);
        }
    }
