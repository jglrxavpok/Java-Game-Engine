package org.jge;

public class EngineException extends Exception
{

    private static final long serialVersionUID = 6351531791319818804L;

    public EngineException(String message)
    {
        super(message);
    }
    
    public EngineException(Throwable cause)
    {
        super(cause);
    }
    
    public EngineException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
