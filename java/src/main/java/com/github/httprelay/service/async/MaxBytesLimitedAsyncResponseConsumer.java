package com.github.httprelay.service.async;

import org.apache.http.ContentTooLongException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ContentBufferEntity;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 4:58 PM
 */
public class MaxBytesLimitedAsyncResponseConsumer extends AbstractAsyncResponseConsumer<HttpResponse> {
    int maxSize;
    int maxDuration;
    private volatile HttpResponse response;
    private volatile SimpleInputBuffer buf;
    long t;

    public MaxBytesLimitedAsyncResponseConsumer(int sizeLimit, int transferDurationLimit) {
        super();
        this.maxSize = sizeLimit;
        this.maxDuration = transferDurationLimit;
        this.t = System.currentTimeMillis();
    }

    @Override
    protected void onResponseReceived(final HttpResponse response) throws IOException {
        this.response = response;
    }

    @Override
    protected void onEntityEnclosed(
            final HttpEntity entity, final ContentType contentType) throws IOException {
        long len = entity.getContentLength();
        if (len > maxSize) {
            throw new ContentTooLongException("Entity content is too long: " + len);
        }
        if (len < 0) {
            len = 4096;
        }
        this.buf = new SimpleInputBuffer((int) len, new HeapByteBufferAllocator());
        this.response.setEntity(new ContentBufferEntity(entity, this.buf));
    }

    @Override
    protected void onContentReceived(
            final ContentDecoder decoder, final IOControl ioctrl) throws IOException {
        if (System.currentTimeMillis() - t > maxDuration * 1000) {
            throw new IllegalStateException("Transfer duration is too long: " + maxDuration);
        }

        if (this.buf == null) {
            throw new IllegalStateException("Content buffer is null");
        }

        int total = this.buf.consumeContent(decoder);
        if (total > maxSize) {
            throw new ContentTooLongException("Entity content is too long: "+total);
        }
    }

    @Override
    protected void releaseResources() {
        this.response = null;
        this.buf = null;
    }

    @Override
    protected HttpResponse buildResult(final HttpContext context) {
        return this.response;
    }
}