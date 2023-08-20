package com.actor.myandroidframework.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * description: 倒计时 <br />
 *              参考{@link android.os.CountDownTimer}, 扩展了一些功能:
 *              <ul>
 *                  <li>重设倒计时总时长: {@link #setMillisInFuture(long)}</li>
 *                  <li>重设计时间隔: {@link #setCountdownInterval(long)}</li>
 *                  <li>获取倒计时总时长: {@link #getMillisInFuture()}</li>
 *                  <li>获取计时间隔: {@link #getCountdownInterval()}</li>
 *                  <li>获取实际计时时间: {@link #getTimingDuration()}</li>
 *              </ul>
 *              使用示例:                                                            <br />
 *              private BaseCountDownTimer countDownTimer = new BaseCountDownTimer(5 * 1000L, 500L) { <br />
 *                  &emsp;//重写2个方法                                                                  <br />
 *              });                                                                                     <br />
 *              countDownTimer.start(); //开始倒计时                                                     <br />
 *              <br />
 *              //可重设倒计时总时长 & 计时间隔                                                          <br />
 *              countDownTimer.setMillisInFuture(10 * 1000L).setCountdownInterval(300L).start();        <br />
 *              <br />
 *              //在Activity的onDestroy(), Fragment的onDestroyView() 的时候要调用!                       <br />
 *              countDownTimer.cancel();                                                                <br />
 * @author    : ldf
 * @update    : 2023/8/16
 */
public abstract class BaseCountDownTimer /*extends CountDownTimer*/ {

    /**
     * Millis since epoch when alarm should stop.
     */
    protected long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    protected long mCountdownInterval;

    protected long mStopTimeInFuture;

    /**
     * boolean representing if the timer was cancelled
     */
    protected boolean mCancelled = false;

    /**
     * 实际计时时长
     */
    protected long mTimingDuration;

    /**
     * 倒计时是否结束
     */
    protected boolean isTimingFinished = false;

    /**
     * @param millisInFuture 倒计时总时长, 单位ms (如果≤0, 会直接调用onFinish())
     * @param countDownInterval 倒计时间隔, 单位ms(如果你计时间隔1秒, 这个值要小于1秒, 否则几百毫秒的时候直接调用onFinish())
     */
    public BaseCountDownTimer(@IntRange(from = 1L) long millisInFuture, @IntRange(from = 1L) long countDownInterval) {
        setMillisInFuture(millisInFuture);
        setCountdownInterval(countDownInterval);
    }

    /**
     * 重新设置倒计时总时长
     * @param millisInFuture 倒计时总时长, 单位ms (如果≤0, 会直接调用onFinish())
     */
    public BaseCountDownTimer setMillisInFuture(@IntRange(from = 1L) long millisInFuture) {
        if (millisInFuture < 1) millisInFuture = 1;
        mMillisInFuture = millisInFuture;
        return this;
    }

    /**
     * 重新设置 倒计时间隔
     * @param countDownInterval 倒计时间隔, 单位ms(如果你计时间隔1秒, 这个值要小于1秒, 否则几百毫秒的时候直接调用onFinish())
     */
    public BaseCountDownTimer setCountdownInterval(@IntRange(from = 1L) long countDownInterval) {
        if (countDownInterval < 1) countDownInterval = 1;
        mCountdownInterval = countDownInterval;
        return this;
    }

    /**
     * 取消计时, 在Activity的onDestroy(), Fragment的onDestroyView() 的时候要调用! Cancel the countdown.
     */
    public synchronized void cancel() {
        //如果已经开始倒计时 & 倒计时未结束  &&  不要重复计算
        if (mStopTimeInFuture != 0 && mTimingDuration == 0) {
            mTimingDuration = mMillisInFuture + SystemClock.elapsedRealtime() - mStopTimeInFuture;
        }
        mCancelled = true;
        mHandler.removeMessages(MSG);
    }

    /**
     * Start the countdown.
     */
    public synchronized /*BaseCountDownTimer*/void start() {
        //移除消息, 防止重复多次计时
        mHandler.removeMessages(MSG);
        mCancelled = false;
        isTimingFinished = false;
        mTimingDuration = 0;
        if (mMillisInFuture <= 0) {
            isTimingFinished = true;
            mStopTimeInFuture = 0;
            onFinish();
            return /*this*/;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return /*this*/;
    }

    /**
     * 倒计时
     * @param millisUntilFinished 离计时结束还有多少时间. The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * 计时结束. Callback fired when the time is up.
     */
    public abstract void onFinish();


    protected static final int MSG = 1;


    // handles counting down
    protected Handler mHandler = new Handler(Looper.getMainLooper()/*, @Nullable Callback callback*/) {

        @Override
        public void handleMessage(@NonNull Message msg) {

            synchronized (BaseCountDownTimer.this) {
                if (mCancelled) {
                    return;
                }

                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    isTimingFinished = true;
                    mStopTimeInFuture = 0;
                    onFinish();
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);

                    // take into account user's onTick taking time to execute
                    long lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart;
                    long delay;

                    if (millisLeft < mCountdownInterval) {
                        // just delay until done
                        delay = millisLeft - lastTickDuration;

                        // special case: user's onTick took more than interval to
                        // complete, trigger onFinish without delay
                        if (delay < 0) delay = 0;
                    } else {
                        delay = mCountdownInterval - lastTickDuration;

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval;
                    }

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };

    /**
     * @return 获取你设置的倒计时总时长, 单位ms
     */
    public long getMillisInFuture() {
        return mMillisInFuture;
    }

    /**
     * @return 获取你设置的倒计时间隔, 单位ms
     */
    public long getCountdownInterval() {
        return mCountdownInterval;
    }

    /**
     * 获取实际计时时间, 单位ms
     * @return 0 ~ mMillisInFuture
     */
    public long getTimingDuration() {
        if (isTimingFinished) {
            //if倒计时正常结束
            return mMillisInFuture;
        } else if (mCancelled) {
            //if调用了cancel()取消倒计时
            return mTimingDuration;
        }
        //if还在倒计时中
        return mMillisInFuture + SystemClock.elapsedRealtime() - mStopTimeInFuture;
    }
}