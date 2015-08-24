package com.speakingfish.pipe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamGobbler implements Runnable {
    final InputStream  _input ;
    final OutputStream _output;

    /*
     * StreamGobbler(InputStream is, String type) { this(is, type, null); }
     */
    public StreamGobbler(InputStream input, OutputStream output) {
        _input  = input ;
        _output = output;
    }

    public void run() {
        try {
            final byte[] buffer = new byte[1024*1024];
            while (true) {
                final int count = _input.read(buffer);
                if (count <= 0)
                    break;
                _output.write(buffer, 0, count);
                _output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}