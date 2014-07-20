package org.jge.util;

import org.jge.EngineKernel;
import org.jge.Time;

public class DefaultFormatter implements Formatter
{

    public String format(String message)
    {
        return "["+EngineKernel.getGame().getGameName()+" "+Time.getTimeAsString()+"] "+message;
    }

}
