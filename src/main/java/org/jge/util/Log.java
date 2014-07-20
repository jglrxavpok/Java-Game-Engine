package org.jge.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.jge.EngineKernel;
import org.jge.ResourceLocation;
import org.jge.Time;

public class Log
{

	private static Formatter formatter = new DefaultFormatter();
	private static OutputStream	  savingOutput;

	static
	{
		try
		{
			File f = EngineKernel
					.getDiskResourceLoader()
					.getResourceOrCreate(
							new ResourceLocation(EngineKernel.getEngineFolder()
									.getAbsolutePath(), "log_"
									+ Strings.createCorrectedFileName(Time.getTimeAsString()) + ".log"))
					.asFile();
			f.createNewFile();
			savingOutput = new BufferedOutputStream(new FileOutputStream(f));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

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
		String finalMessage = formatter.format(msg);
		out.println(finalMessage);
		try
		{
			savingOutput.write((finalMessage+"\n").getBytes());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void setSaveOutput(OutputStream output)
	{
		Log.savingOutput = output;
	}
	
	public static void finish() throws Exception
	{
		savingOutput.flush();
		savingOutput.close();
	}
}
