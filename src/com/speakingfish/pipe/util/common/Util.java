/**
 * 
 */
package com.speakingfish.pipe.util.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.speakingfish.common.function.Creator;
import com.speakingfish.common.function.Getter;

import static com.speakingfish.common.value.util.Values.*;

/**
 * @author borka
 *
 */
public class Util {

    public static final Creator<DateFormat, String> CREATOR_SimpleDateFormat = new Creator<DateFormat, String>() {
        public DateFormat apply(String params) {
            return new SimpleDateFormat(params);
        }};
    
    
    public static final Getter<DateFormat> DATEFORMAT_TIMESTAMP_COMMON = threadLocalCloseableSingleton(CREATOR_SimpleDateFormat, "yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * 
     */
    public Util() {
        // TODO Auto-generated constructor stub
    }

    public static Integer parseInteger(String src, Integer defaultValue) {
        if(null != src)
            try {
                return Integer.parseInt(src);
            } catch(Throwable e) {
            }
        return defaultValue;
    }
    
    /**
     * <p>split command line arguments into flags map and rest arguments</p>
     * <p>[flags] [any of terminals] [rest params]</p>
     * <p>where <code>flag</code> is name[=value]
     * 
     * @param flags
     * @param terminals
     * @param args
     * @return rest arguments
     */
    public static List<String> splitArgs(Map<String, String> flags, Set<String> terminals, String[] args) {
        final Iterator<String> argsIterator = Arrays.asList(args).iterator();
        final List<String> rest = new ArrayList<String>(); 
        while(argsIterator.hasNext()) {
            final String item = argsIterator.next();
            if(terminals.contains(item)) {
                break;
            }
            final int equalPos = item.indexOf('=');
            if(0 <= equalPos) {
                flags.put(item.substring(0, equalPos), item.substring(equalPos + 1));
            } else {
                flags.put(item, "");
            }
        }
        while(argsIterator.hasNext()) {
            rest.add(argsIterator.next());
        }
        return rest;
    }

}
