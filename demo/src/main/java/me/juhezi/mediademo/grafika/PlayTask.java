package me.juhezi.mediademo.grafika;

import static me.juhezi.mediademo.grafika.VideoPlayerKt.MSG_PLAY_STOPPED;

public class PlayTask implements Runnable {

    private VideoPlayer mPlayer;
    private VideoPlayer.PlayerFeedback mFeedback;
    private boolean mDoLoop;
    private Thread mThread;
    private LocalHandler mLocalHandler;

    private final Object mStopLock = new Object();
    private boolean mStopped = false;

    public PlayTask(VideoPlayer player, VideoPlayer.PlayerFeedback feedback) {
        mPlayer = player;
        mFeedback = feedback;

        mLocalHandler = new LocalHandler();
    }

    public void setLoopMode(boolean loopMode) {
        mDoLoop = loopMode;
    }

    public void execute() {
        mPlayer.setLoop(mDoLoop);
        mThread = new Thread(this, "Movie Player");
        mThread.start();
    }

    public void requestStop() {
        mPlayer.requestStop();
    }

    public void waitForStop() {
        synchronized (mStopLock) {
            while (!mStopped) {
                try {
                    mStopLock.wait();
                } catch (InterruptedException ie) {
                    // discard
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            mPlayer.play();
        } finally {
            synchronized (mStopLock) {
                mStopped = true;
                mStopLock.notifyAll();
            }

            mLocalHandler.sendMessage(
                    mLocalHandler.obtainMessage(MSG_PLAY_STOPPED, mFeedback));
        }
    }
}