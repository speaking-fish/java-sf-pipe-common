package com.speakingfish.pipe;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.speakingfish.common.exception.wrapped.java.io.WrappedEOFException;
import com.speakingfish.common.exception.wrapped.java.io.WrappedIOException;

public class Helper {

    public static void internalWrite(OutputStream dest, int offset, int size, byte[] buffer) {
        try {
            dest.write(buffer, offset, size);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void internalWrite(OutputStream dest, byte[] buffer) {
        try {
            dest.write(buffer);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void internalWrite(OutputStream dest, byte value) {
        try {
            dest.write(value);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte internalRead(InputStream src) throws WrappedEOFException {
        final int result;
        try {
            result = src.read();
        } catch(EOFException e) {
            throw new WrappedEOFException(e);
        } catch(IOException e) {
            throw new WrappedIOException(e);
        }
        if(result < 0) {
            throw new WrappedEOFException(new EOFException());
        }
        return (byte) result;
    }
    
    public static void internalRead(InputStream src, int offset, int size, byte[] buffer) throws WrappedEOFException {
        if(size <= 0) {
            return;
        }

        while(true) {
            final int count;
            try {
                count = src.read(buffer, offset, size);
            } catch(EOFException e) {
                throw new WrappedEOFException(e);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
            if(count == size) {
                break;
            }
            
            if(count <= 0) {
                throw new WrappedEOFException(new EOFException());
            }
            offset+= count;
            size  -= count;
        }
    }

    public static void internalRead(InputStream src, byte[] buffer) throws WrappedEOFException {
        internalRead(src, 0, buffer.length, buffer);
    }
    
    
}
