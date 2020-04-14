package me.juhezi.mediademo.media.decoder;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

import me.juhezi.mediademo.media.SampleInfo;

public class AudioDecoder extends BaseMediaCodecDecoder {

    private static final String TAG = "AudioDecoder";

    private static final String MIME = "audio/";
    private int mFrameCount = 0;
    private SampleInfo mAudioSampleInfo;
    private ByteBuffer mBuffer;
    private ByteBuffer mCopyBuffer = ByteBuffer.allocate(1);

    public AudioDecoder(String path) {
        this.mInputPath = path;
    }

    public void prepare() throws Exception {
        mExtractor = new MediaExtractor();
        mExtractor.setDataSource(mInputPath);
        for (int i = 0; i < mExtractor.getTrackCount(); i++) {
            MediaFormat format = mExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(MIME)) {
                mExtractor.selectTrack(i);
                mDuration = format.getLong(MediaFormat.KEY_DURATION);
                mAudioSampleInfo = new SampleInfo(SampleInfo.SampleType.AUDIO, mBufferInfo);
                mDecoder = MediaCodec.createDecoderByType(mime);
                mDecoder.configure(format, null, null, 0);
                break;
            }
        }
    }

    public SampleInfo getNextSampleInfo() {
        boolean mThisFrameValid = false;
        while (!mThisFrameValid && mRunning) {
            log("getNextSampleInfo", "...");
            int outIndex = mDecoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            switch (outIndex) {
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    log("getNextSampleInfo", "INFO_OUTPUT_BUFFERS_CHANGED to " + mDecoder.getOutputBuffers().length + " size");
                    break;

                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    log("getNextSampleInfo", "INFO_OUTPUT_FORMAT_CHANGED format : " + mDecoder.getOutputFormat());
                    break;

                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    log("getNextSampleInfo", "INFO_TRY_AGAIN_LATER");
                    break;

                default:
                    if (mBufferInfo.size > 0 && outIndex > 0) {
                        mFrameCount++;

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            mBuffer = mDecoder.getOutputBuffer(outIndex);
                        }
                        log("getNextSampleInfo", "this frame want releaseOutputBuffer, time is " + System.currentTimeMillis() + " framesCount" + mFrameCount);
                        // ? 为什么这么做
                        setCopyBuffer(mAudioSampleInfo, mBufferInfo);
                        mDecoder.releaseOutputBuffer(outIndex, false);
                        if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM || mBufferInfo.presentationTimeUs >= mSeekStartTime) {
                            mThisFrameValid = true;
                        }

                    } else {
                        log("getNextSampleInfo", "this option decode bufferInfo.size not > 0");
                    }
                    break;
            }
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                mRunning = false;
                setCopyBuffer(mAudioSampleInfo, mBufferInfo);
                stop();
            }
        }
        return mAudioSampleInfo;
    }

    private void setCopyBuffer(SampleInfo sampleInfo, MediaCodec.BufferInfo bufferInfo) {
        mCopyBuffer.clear();
        if (bufferInfo.size > 0 && mBuffer != null && mBuffer.remaining() > 0) {
            if (mCopyBuffer.capacity() != mBuffer.capacity()) {
                mCopyBuffer = ByteBuffer.allocate(bufferInfo.size);
            }
            mCopyBuffer.put(mBuffer);
            mCopyBuffer.position(0);
            sampleInfo.setBufferInfo(bufferInfo);
            sampleInfo.setBuffer(mCopyBuffer);
        } else {
            sampleInfo.setBufferInfo(bufferInfo);
            sampleInfo.setBuffer(null);
        }
    }

}
