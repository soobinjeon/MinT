/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MinTFramework.Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 *
 * @author soobin
 */
public class ByteBufferPool {

    private static final int MEMORY_BLOCKSIZE = 1024;
    private static final int FILE_BLOCKSIZE = 10240;

    private final ArrayList memoryQueue = new ArrayList();
    private final ArrayList fileQueue = new ArrayList();

    private boolean wait = false;

    public ByteBufferPool(int memorySize, int fileSize, File file) throws IOException {
        if (memorySize > 0) {
            initMemoryBuffer(memorySize);
        }

        if (fileSize > 0) {
            initFileBuffer(fileSize, file);
        }
    }

    private void initMemoryBuffer(int size) {
        int bufferCount = size / MEMORY_BLOCKSIZE;
        size = bufferCount * MEMORY_BLOCKSIZE;
        ByteBuffer directBuf = ByteBuffer.allocateDirect(size);
        divideBuffer(directBuf, MEMORY_BLOCKSIZE, memoryQueue);
    }

    private void initFileBuffer(int size, File f) throws IOException {
        int bufferCount = size / FILE_BLOCKSIZE;
        size = bufferCount * FILE_BLOCKSIZE;
        RandomAccessFile file = new RandomAccessFile(f, "rw");
        try {
            file.setLength(size);
            ByteBuffer fileBuffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0L, size);
            divideBuffer(fileBuffer, FILE_BLOCKSIZE, fileQueue);
        } finally {
            file.close();
        }
    }

    private void divideBuffer(ByteBuffer buf, int blockSize, ArrayList list) {
        int bufferCount = buf.capacity() / blockSize;
        int position = 0;
        for (int i = 0; i < bufferCount; i++) {
            int max = position + blockSize;
            buf.limit(max);
            list.add(buf.slice());
            position = max;
            buf.position(position);
        }
    }

    public ByteBuffer getMemoryBuffer() {
        return getBuffer(memoryQueue, fileQueue);
    }

    public ByteBuffer getFileBuffer() {
        return getBuffer(fileQueue, memoryQueue);
    }

    private ByteBuffer getBuffer(ArrayList firstQueue, ArrayList secondQueue) {
        ByteBuffer buffer = getBuffer(firstQueue, false);
        if (buffer == null) {
            buffer = getBuffer(secondQueue, false);
            if (buffer == null) {
                if (wait) {
                    buffer = getBuffer(firstQueue, true);
                } else {
                    buffer = ByteBuffer.allocate(MEMORY_BLOCKSIZE);
                }
            }
        }
        return buffer;
    }

    private ByteBuffer getBuffer(ArrayList queue, boolean wait) {
        synchronized (queue) {
            if (queue.isEmpty()) {
                if (wait) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            return (ByteBuffer) queue.remove(0);
        }
    }

    public void putBuffer(ByteBuffer buffer) {
        if (buffer.isDirect()) {
            switch (buffer.capacity()) {
                case MEMORY_BLOCKSIZE:
                    putBuffer(buffer, memoryQueue);
                    break;
                case FILE_BLOCKSIZE:
                    putBuffer(buffer, fileQueue);
                    break;
            }
        }
    }

    private void putBuffer(ByteBuffer buffer, ArrayList queue) {
        buffer.clear();
        synchronized (queue) {
            queue.add(buffer);
            queue.notify();
        }
    }

    public synchronized void setWait(boolean wait) {
        this.wait = wait;
    }

    public synchronized boolean isWait() {
        return wait;
    }
}
