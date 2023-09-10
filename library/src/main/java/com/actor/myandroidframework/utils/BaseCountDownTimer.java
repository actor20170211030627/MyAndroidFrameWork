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
 *              <table border="2px" bordercolor="red" cellspacing="0px" cellpadding="5px">
 *                  <tr>
 *                      <th>№</th>
 *                      <th>Method方法</th>
 *                      <th>Doc说明</th>
 *                  </tr>
 *                  <tr>
 *                      <td>1</td>
 *                      <td>{@link #start()}</td>
 *                      <td>开始/重新开始倒计时.</td>
 *                  </tr>
 *                  <tr>
 *                      <td>2</td>
 *                      <td>{@link #cancel()}</td>
 *                      <td>取消计时/取消本次计时.</td>
 *                  </tr>
 *                  <tr>
 *                      <td>---</td>
 *                      <td>------下方是扩展方法------</td>
 *                      <td>------------------</td>
 *                  </tr>
 *                  <tr>
 *                      <td>3</td>
 *                      <td>{@link #setMillisInFuture(long)}</td>
 *                      <td>重设倒计时总时长</td>
 *                  </tr>
 *                  <tr>
 *                      <td>4</td>
 *                      <td>{@link #setCountdownInterval(long)}</td>
 *                      <td>重设计时间隔</td>
 *                  </tr>
 *                  <tr>
 *                      <td>5</td>
 *                      <td>{@link #getCountdownInterval()}</td>
 *                      <td>获取计时间隔</td>
 *                  </tr>
 *                  <tr>
 *                      <td>6</td>
 *                      <td>{@link #getMillisInFuture()}</td>
 *                      <td>获取倒计时总时长</td>
 *                  </tr>
 *                  <tr>
 *                      <td>7</td>
 *                      <td>{@link #getTimingDuration()}</td>
 *                      <td>获取实际计时时间</td>
 *                  </tr>
 *                  <tr>
 *                      <td>8</td>
 *                      <td>{@link #pause()}</td>
 *                      <td>暂停倒计时, 调用{@link #resume()}继续计时</td>
 *                  </tr>
 *                  <tr>
 *                      <td>9</td>
 *                      <td>{@link #resume()}</td>
 *                      <td>继续倒计时, 需要先{@link #pause()}暂停后, 调用本方法才有效</td>
 *                  </tr>
 *                  <tr>
 *                      <td>10</td>
 *                      <td>{@link #getState()}</td>
 *                      <td>获取计时状态</td>
 *                  </tr>
 *              </table>
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
 * @update    : 2023/9/10
 */
public abstract class BaseCountDownTimer /*extends CountDownTimer*/ {

    /**
     * Millis since epoch when alarm should stop. 设置倒计时总时长
     */
    protected long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks 设置倒计时间隔
     */
    protected long mCountdownInterval;

    /**
     * 停止时间 = 开始计时'开机时间 + 倒计时总时长 - 实际计时时长
     */
    protected long mStopTimeInFuture;



    /**
     * boolean representing if the timer was cancelled
     */
//    protected boolean mCancelled = false;

    public enum Status {
        IDLE,   //空闲
        START,  //已开始
        PAUSE,  //已暂停
        RESUME, //已重新开始 (暂停后)
        FINISH, //已计时完成
        CANCEL  //已取消
    }
    //状态
    protected Status state = Status.IDLE;

    /**
     * 实际计时时长 = 开始计时'开机时间 + 倒计时总时长 - 停止时间
     */
    protected long mTimingDuration;

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
     * 重新设置倒计时间隔
     * @param countDownInterval 倒计时间隔, 单位ms(如果你计时间隔1秒, 这个值要小于1秒, 否则几百毫秒的时候直接调用onFinish())
     */
    public BaseCountDownTimer setCountdownInterval(@IntRange(from = 1L) long countDownInterval) {
        if (countDownInterval < 1) countDownInterval = 1;
        mCountdownInterval = countDownInterval;
        return this;
    }

    /**
     * Cancel the countdown. 取消计时/取消本次计时. <br />
     * 在Activity的onDestroy(), Fragment的onDestroyView() 的时候也要调用!
     */
    public synchronized void cancel() {
        mHandler.removeMessages(MSG);
        if (state == Status.START || state == Status.RESUME) {
            mTimingDuration = SystemClock.elapsedRealtime() + mMillisInFuture - mStopTimeInFuture;
            mStopTimeInFuture = 0;
        }
        state = Status.CANCEL;
    }

    /**
     * Start the countdown. 开始/重新开始倒计时.
     */
    public synchronized /*BaseCountDownTimer*/void start() {
//        mCancelled = false;
        mHandler.removeMessages(MSG);
        //倒计时总时长 > 0       && 计时间隔 > 0
        if (mMillisInFuture > 0 && mCountdownInterval > 0) {
            mTimingDuration = 0;        //实际计时时长
            mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture - mTimingDuration;
            mHandler.sendMessage(mHandler.obtainMessage(MSG));
            state = Status.START;
        }
        return /*this*/;
    }

    /**
     * 倒计时
     * @param millisUntilFinished 离计时结束还有多少时间. The amount of time until finished.
     */
    protected abstract void onTick(long millisUntilFinished);

    /**
     * 计时结束. Callback fired when the time is up.
     */
    protected abstract void onFinish();


    protected static final int MSG = 1;


    // handles counting down
    protected Handler mHandler = new Handler(Looper.getMainLooper()/*, @Nullable Callback callback*/) {

        @Override
        public void handleMessage(@NonNull Message msg) {

            synchronized (BaseCountDownTimer.this) {
//                if (mCancelled) {
                if (state == Status.PAUSE || state == Status.FINISH || state == Status.CANCEL) {
                    return;
                }

                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    state = Status.FINISH;
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
     * 暂停计时 (非停止计时)
     */
    public void pause() {
        //if         已开始        or          重新开始
        if (state == Status.START || state == Status.RESUME) {
            //实际计时时长
            mTimingDuration = SystemClock.elapsedRealtime() + mMillisInFuture  - mStopTimeInFuture;
            state = Status.PAUSE;
            mHandler.removeMessages(MSG);
        }
    }

    /**
     * 暂停后, 继续计时
     */
    public void resume() {
        if (state == Status.PAUSE) {
            //if计时未完成
            if (mMillisInFuture > mTimingDuration) {
                //停止时间        = 系统开机时间                    + 上次计时'剩余时间
                mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture - mTimingDuration;
                state = Status.RESUME;
                mHandler.sendMessage(mHandler.obtainMessage(MSG));
            } else {
                state = Status.FINISH;
                onFinish();
            }
        }
    }

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
        if (state == Status.IDLE) {
            return 0;
        } else if (state == Status.FINISH) {
            //if倒计时正常结束
            return mMillisInFuture;
        } else if (state == Status.PAUSE || state == Status.CANCEL) {
            //if调用了pause(), cancel()
            return mTimingDuration;
        }
        //if还在倒计时中
        return SystemClock.elapsedRealtime() + mMillisInFuture - mStopTimeInFuture;
    }

    /**
     * 获取倒计时状态
     */
    public Status getState() {
        return state;
    }
}