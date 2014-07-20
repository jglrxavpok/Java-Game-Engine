package org.jge;

public class RuntimeEngineException extends RuntimeException
{

    private static final long serialVersionUID = 6351531791319818804L;

    public RuntimeEngineException(String message)
    {
        super(message);
    }
    
    public RuntimeEngineException(Throwable cause)
    {
        super(cause);
    }
    
    public RuntimeEngineException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
