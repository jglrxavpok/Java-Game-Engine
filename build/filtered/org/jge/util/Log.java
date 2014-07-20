package org.jge.util;

import java.io.PrintStream;

public class Log
{

    private static Formatter formatter = new DefaultFormatter();

    public static void message(String msg)
    {
        log(msg, System.out);
    }
    
    public static void error(String msg)
    {
        log(msg, System.err);
    }
    
    public static void log(String msg, PrintStream out)
    {
        out.println(formatter.format(msg));
    }
}
