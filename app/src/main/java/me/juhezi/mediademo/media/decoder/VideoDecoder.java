package me.juhezi.mediademo.media.decoder;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.view.Surface;

import me.juhezi.mediademo.media.SampleInfo;

public class VideoDecoder extends BaseMediaCodecDecoder {

    private static final String TAG = "VideoDecoder";

    private static final String MIME_TYPE = "video/";

    private Surface mSurface;
    private int mFrameCount = 0;
    private SampleInfo mVideoSampleInfo;

    public VideoDecoder(String inputPath) {
        this.mInputPath = inputPath;
    }

    public void prepare(SurfaceTexture surfaceTexture) throws Exception {
        if (surfaceTexture != null) {
            mSurface = new Surface(surfaceTexture);
        }
        mExtractor = new MediaExtractor();
        mExtractor.setDataSource(mInputPath);

        for (int i = 0; i < mExtractor.getTrackCount(); i++) {
            MediaFormat format = mExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(MIME_TYPE)) {
                mTrackIndex = i;
                mExtractor.selectTrack(mTrackIndex);
                mVideoSampleInfo = new SampleInfo(SampleInfo.SampleType.VIDEO, mBufferInfo);
                mDecoder = MediaCodec.createDecoderByType(mime);
                mDecoder.configure(format, mSurface, null, 0);
                break;
            }
        }
    }

    public synchronized SampleInfo getNextSampleInfo() throws Exception {
        if (hasException()) {
            throw new Exception("Has exception");
        }
        boolean thisFrameValid = false;
        while (!thisFrameValid && isRunning()) {
            log("decodeFrame", "...");
            int outIndex = 0;
            outIndex = mDecoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            switch (outIndex) {
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    log("decodeFrame", "INFO_OUTPUT_BUFFERS_CHANGED to " + mDecoder.getOutputBuffers().length + " size");
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    log("decodeFrame", "INFO_OUTPUT_FORMAT_CHANGED format: " + mDecoder.getOutputFormat());
                    break;
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    log("decodeFrame", "INFO_TRY_AGAIN_LATER");
                    break;
                default:
                    if (mBufferInfo.size > 0) {
                        mFrameCount++;
                        log("decodeFrame", "This frame want release OutputBuffer. Time is " + System.currentTimeMillis());
                        mDecoder.releaseOutputBuffer(outIndex, true);
                        mVideoSampleInfo.setBufferInfo(mBufferInfo);
                        if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM ||
                                mBufferInfo.presentationTimeUs >= mSeekStartTime) {
                            thisFrameValid = true;
                        }
                    } else {
                        log("decodeFrame", "This option decode bufferInfo.size not > 0");
                    }
                    break;
            }

            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                mRunning = false;
                mVideoSampleInfo.setBufferInfo(mBufferInfo);
                log("decodeFrame", "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                stop();
            }
        }
        return mVideoSampleInfo;
    }

}
