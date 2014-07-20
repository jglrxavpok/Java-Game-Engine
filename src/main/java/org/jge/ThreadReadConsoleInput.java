package org.jge;

import java.util.Scanner;

public class ThreadReadConsoleInput extends Thread
{

	private boolean running;

	public ThreadReadConsoleInput()
	{
		super("Read Console commands");
		this.running = true;
	}
	
	public void run()
	{
		Scanner sc = new Scanner(System.in);
		while(running)
		{
			try
			{
    			String line = sc.nextLine();
    			String[] tokens = line.split(" ");
    			
    			// TODO get command
    			
    			if(tokens [0].equals("setValue"))
    			{
    				if(tokens [1].equals("bool"))
    				{
    					CoreEngine.getCurrent().getRenderEngine().setBoolean(tokens[2], Boolean.parseBoolean(tokens[3]));
    				}
    				else if(tokens [1].equals("float"))
    				{
    					CoreEngine.getCurrent().getRenderEngine().setFloat(tokens[2], Float.parseFloat(tokens[3]));
    				}
    				else if(tokens [1].equals("int"))
    				{
    					CoreEngine.getCurrent().getRenderEngine().setInt(tokens[2], Integer.parseInt(tokens[3]));
    				}	
    			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		sc.close();
	}
}
