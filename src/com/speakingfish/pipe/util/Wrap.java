/**
 * 
 */
package com.speakingfish.pipe.util;

import com.speakingfish.pipe.StreamGobbler;


/**
 * @author borka
 * 
 */
public class Wrap {

    public static void main(String args[]) {
        if(args.length < 1) {
            System.out.println("Usage: java " + Wrap.class.getName() + " program-to-execute [program-parameters...]");
            System.exit(1);
        }
        try {
            final ProcessBuilder builder = new ProcessBuilder(args);

            final Process process = builder.start();

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    process.destroy();
                }
                }));

            new Thread(new StreamGobbler(process.getErrorStream(), System.err)).start();
            new Thread(new StreamGobbler(process.getInputStream(), System.out)).start();
            new Thread(new StreamGobbler(System.in, process.getOutputStream())).start();

            int exitCode = 1;
            try {
                exitCode = process.waitFor();
            } finally {
                try {
                    process.destroy();
                } finally {
                    System.exit(exitCode);
                }
            }
            
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

}
