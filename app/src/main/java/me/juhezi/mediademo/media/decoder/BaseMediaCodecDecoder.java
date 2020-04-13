package me.juhezi.mediademo.media.decoder;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.util.Log;

import me.juhezi.mediademo.BuildConfig;

public class BaseMediaCodecDecoder {

    private static final String TAG = "BaseMediaCodecDecoder";

    protected int TIMEOUT_USEC = 10000;    // 10[msec]

    protected volatile boolean mRunning = false;
    protected boolean mExtractEndOfStream = false;
    private boolean mException;

    protected MediaExtractor mExtractor = null;
    protected MediaCodec mDecoder;

    protected long mDuration;
    protected MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    protected long mSeekStartTime = 0;

    protected String mInputPath;
    protected int mTrackIndex = -1;

    private Object mLock = new Object();

    public void start() throws Exception {
        if (mDecoder != null) {
            mDecoder.start();
            mRunning = true;
            new Thread(this::extractAndDecode, getClass().getSimpleName()).start();
        } else {
            throw new Exception("Decoder is null!!");
        }
    }

    public void stop() {
        mRunning = false;
        synchronized (mLock) {
            if (mExtractor != null) {
                mExtractor.release();
                mExtractor = null;
            }
            if (mDecoder != null) {
                mDecoder.stop();
                mDecoder.release();
                mDecoder = null;
            }
        }
    }

    public void seek(long time) {
        if (mExtractor != null && time > 0 && time <= mDuration) {
            mSeekStartTime = time;
            mExtractor.seekTo(time, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        }
    }

    protected void extractAndDecode() {
        while (isRunning() && !mExtractEndOfStream) {
            synchronized (mLock) {
                if (isRunning() && !mExtractEndOfStream) {
                    assert (mExtractor.getSampleTrackIndex() == mTrackIndex);
                    try {
                        int inputIndex = mDecoder.dequeueInputBuffer(0);
                        if (inputIndex >= 0) {
                            log("extractAndDecode", "inputIndex is " + inputIndex);
                            int sampleSize = mExtractor.readSampleData(mDecoder.getInputBuffer(inputIndex), 0);
                            if (sampleSize > 0) {
                                mDecoder.queueInputBuffer(inputIndex,
                                        0,
                                        sampleSize,
                                        mExtractor.getSampleTime(),
                                        0);
                                mExtractor.advance();
                            } else {
                                log("extractAndDecode", "No buffer can extract.");
                                mDecoder.queueInputBuffer(inputIndex,
                                        0,
                                        0,
                                        0,
                                        MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                mExtractEndOfStream = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        mRunning = false;
                        mException = true;
                    }
                }
            }
        }
    }

    public boolean isRunning() {
        return mRunning;
    }

    public boolean hasException() {
        return mException;
    }

    protected void log(String function, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "(" + function + "):--:" + message);
        }
    }

}
