package me.juhezi.mediademo.media;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

public class SampleInfo {

    private SampleType mType = SampleType.VIDEO;

    private MediaCodec.BufferInfo mBufferInfo;

    private ByteBuffer mBuffer;

    private int mBufferLength;

    private boolean isPackedData = false;

    public enum SampleType {
        VIDEO, AUDIO
    }

    public SampleInfo(SampleType type, MediaCodec.BufferInfo bufferInfo) {
        mType = type;
        mBufferInfo = bufferInfo;
    }

    public SampleType getType() {
        return mType;
    }

    public void setType(SampleType type) {
        this.mType = type;
    }

    public void setBuffer(ByteBuffer buffer) {

        this.mBuffer = buffer;
        if (mBuffer != null) {
            mBufferLength = mBuffer.capacity();
        } else {
            mBufferLength = 0;
        }
    }

    public int getBufferLength() {
        return mBufferLength;
    }

    public ByteBuffer getBuffer() {
        return mBuffer;
    }

    public MediaCodec.BufferInfo getBufferInfo() {
        return mBufferInfo;
    }

    public void setBufferInfo(MediaCodec.BufferInfo bufferInfo) {
        this.mBufferInfo = bufferInfo;
    }

    public long getRealPtsTime() {
        return mBufferInfo.presentationTimeUs;
    }

    public boolean isPackedData() {
        return isPackedData;
    }

    public void setPackedData(boolean packedData) {
        isPackedData = packedData;
    }
}
