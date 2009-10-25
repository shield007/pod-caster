package org.stanwood.podcaster.logging;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A custom logging layout, used to layout the logging message simalar to System.out.println()
 */
public class CustomLayout extends Layout {

    StringBuffer sbuf = new StringBuffer(128);

    /**
     * Format the message so that it is plain text only with no logging info around it.
     * @param event The logging event
     * @return The formatted message
     */
    @Override
    public String format(LoggingEvent event) {
        sbuf.setLength(0);
        sbuf.append(event.getMessage());
        sbuf.append(LINE_SEP);
        
        return sbuf.toString();
    }
    
    /**
     * Always returns true to tell the logger to logger to ignore throwables
     * @return Always returns true
     */
    @Override
    public boolean ignoresThrowable() {
        return true;
    }

    /**
     * This does nothing
     */
    @Override
    public void activateOptions() {

    }
}
