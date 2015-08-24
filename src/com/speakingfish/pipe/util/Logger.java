package com.speakingfish.pipe.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.speakingfish.pipe.StreamGobbler;

import static java.util.Collections.*;
import static com.speakingfish.pipe.util.common.Util.*;

public class Logger {
    
    public static class LogFilenameFormat {
        protected final String _prefix;
        protected final String _suffix;
        
        public LogFilenameFormat(Map<String, String> args) {
            this(
                args.get("-file"),
                args.get("-ext" )
                );
        }
        
        public LogFilenameFormat(
            String prefix,
            String suffix
        ) {
            super();
            _prefix= (null == prefix) ? ""     : prefix;
            _suffix= (null == suffix) ? ".log" : suffix;
        }
        
        public String prefix() { return _prefix; } 
        public String suffix() { return _suffix; }
        
        public Appendable format(Appendable dest, Date date) {
            try {
                return dest
                    .append(prefix())
                    .append(DATEFORMAT_TIMESTAMP_COMMON.get().format(date))
                    .append(suffix());
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String format(Date date) {
            return format(new StringBuilder(), date).toString();
        }
        
        public String format() {
            return format(Calendar.getInstance().getTime());
        }
        
    }
    
    public static class LogParameters {
        protected final LogFilenameFormat _filenameFormat;
        protected final Integer           _logSizeMb     ;
        protected final Integer           _logDays       ;

        public LogParameters(LogFilenameFormat filenameFormat, Map<String, String> args) {
            this(filenameFormat,
                parseInteger(args.get("-size"), null),
                parseInteger(args.get("-days"), null)
                );
        }
        
        public LogParameters(
            LogFilenameFormat filenameFormat,
            Integer           logSizeMb     ,
            Integer           logDays
        ) {
            super();
            _filenameFormat= filenameFormat;
            _logSizeMb     = logSizeMb     ;
            _logDays       = logDays       ;
        }
        
        public LogFilenameFormat filenameFormat() { return _filenameFormat; }
        public Integer           logSizeMb     () { return _logSizeMb     ; } 
        public Integer           logDays       () { return _logDays       ; } 
        
    }
    
    public static class LogCleanParameters {
        protected final LogFilenameFormat _filenameFormat;
        protected final Integer           _maxTotalBlocks;
        protected final Integer           _maxTotalDays  ;
        protected final Integer           _maxTotalSize  ;

        public LogCleanParameters(LogFilenameFormat filenameFormat, Map<String, String> args) {
            this(filenameFormat,
                parseInteger(args.get("clearBlocks"), null),
                parseInteger(args.get("clearDays  "), null),
                parseInteger(args.get("clearSize  "), null)
                );
        }
        
        public LogCleanParameters(
            LogFilenameFormat filenameFormat,
            Integer           maxTotalBlocks,
            Integer           maxTotalDays  ,
            Integer           maxTotalSize
        ) {
            super();
            _filenameFormat = filenameFormat;
            _maxTotalBlocks = maxTotalBlocks;
            _maxTotalDays   = maxTotalDays  ;
            _maxTotalSize   = maxTotalSize  ;
        }
        
        public LogFilenameFormat filenameFormat() { return _filenameFormat; }
        public Integer           maxTotalBlocks() { return _maxTotalBlocks; } 
        public Integer           maxTotalDays  () { return _maxTotalDays  ; } 
        public Integer           maxTotalSize  () { return _maxTotalSize  ; } 
    }

    final LogParameters      _params     ;
    final LogCleanParameters _cleanParams;
    final List<String>       _commandLine;
    
    public Logger(
        LogParameters      params     ,
        LogCleanParameters cleanParams,
        List<String>       commandLine
    ) {
        _params      = params     ;
        _cleanParams = cleanParams;
        _commandLine = commandLine;
    }

    public Logger(LogFilenameFormat filenameFormat, Map<String, String> flags, List<String> commandLine) {
        this(
            new LogParameters(filenameFormat, flags),
            new LogCleanParameters(filenameFormat, flags),
            commandLine
            );
    }
    
    public Logger(Map<String, String> flags, List<String> commandLine) {
        this(new LogFilenameFormat(flags), flags, commandLine);
    }
    
    public void run() {
        try {
            final ProcessBuilder builder = new ProcessBuilder(_commandLine);

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

    /**
     * @param args
     */
    public static void main(String[] args) {
        final Map<String, String> flags = new HashMap<String, String>();
        final Set<String> terminals = singleton("-run");
        final List<String> commandLine = splitArgs(flags, terminals, args);
        if(args.length < 1) {
            System.err.println(""
                + "\nUsage: java " + Logger.class.getName() + "parameters -run program-to-execute [program-parameters...]"
                + "\nwhere parameters is:"
                + "\n"
                );
            System.exit(1);
        }
        new Logger(flags, commandLine).run();
    }

}
