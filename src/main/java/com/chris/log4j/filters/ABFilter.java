package com.chris.log4j.filters;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public final class ABFilter extends Filter
{
    /**
     * Decides what to do with logging event.<br>
     * This method accepts only log events that contain exceptions.
     *
     * @param loggingEvent
     *            log event that is going to be filtred.
     * @return {@link org.apache.log4j.spi.Filter#ACCEPT} if admin command, {@link org.apache.log4j.spi.Filter#DENY}
     *         otherwise
     */
    @Override
    public final int decide(LoggingEvent loggingEvent)
    {
        Object message = loggingEvent.getMessage();
        if(message == null){
            return ACCEPT;
        }
        if (((String) message).startsWith("[AB]"))
        {
            return ACCEPT;
        }

        return DENY;
    }
}
