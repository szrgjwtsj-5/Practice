package com.whx.practice.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.lang.ref.WeakReference;

/**
 * Schedule a countdown until a time in the future, with
 * regular notifications on intervals along the way.
 * <p>
 * Example of showing a 30 second countdown in a text field:
 * <p>
 * <pre class="prettyprint">
 * new CountdownTimer(30000, 1000) {
 * <p>
 * public void onTick(long millisUntilFinished) {
 * mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
 * }
 * <p>
 * public void onFinish() {
 * mTextField.setText("done!");
 * }
 * }.start();
 * </pre>
 * <p>
 * The calls to {@link #onTick(long)} are synchronized to this object so that
 * one call to {@link #onTick(long)} won't ever occur before the previous
 * callback is complete.  This is only relevant when the implementation of
 * {@link #onTick(long)} takes an amount of time to execute that is significant
 * compared to the countdown interval.
 */

/**
 * reference : https://gist.github.com/Gautier/737759
 * 解决在onTick里面cancel自己
 */
public abstract class CountdownTimer {

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    private boolean mCancelled;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountdownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     * <p>
     * Do not call it from inside CountDownTimer threads
     */
    public synchronized final void cancel() {
        mHandler.removeMessages(MSG);
        mCancelled = true;
    }

    /**
     * Start the countdown.
     */
    public synchronized final CountdownTimer start() {
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        mCancelled = false;
        return this;
    }


    // handles counting down
    private Handler mHandler = new CountingDownHandler(this);

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();


    private static final int MSG = 1;

    static class CountingDownHandler extends Handler {
        final WeakReference<CountdownTimer> mRef;

        public CountingDownHandler(CountdownTimer timer) {
            super();

            mRef = new WeakReference<>(timer);
        }

        @Override
        public void handleMessage(Message msg) {
            CountdownTimer timer = mRef.get();
            if (timer == null) {
                return;
            }

            synchronized (timer) {
                final long millisLeft = timer.mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    timer.onFinish();
                } else if (millisLeft < timer.mCountdownInterval) {
                    // no tick, just delay until done
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    timer.onTick(millisLeft);

                    // take into account user's onTick taking time to execute
                    long delay = lastTickStart + timer.mCountdownInterval - SystemClock.elapsedRealtime();

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0) {
                        delay += timer.mCountdownInterval;
                    }

                    if (!timer.mCancelled) {
                        sendMessageDelayed(obtainMessage(MSG), delay);
                    }
                }
            }
        }
    }
}
