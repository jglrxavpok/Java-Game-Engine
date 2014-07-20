package org.jge;

import org.jge.util.Log;

public class Profiler
{

	public static class ProfileTimer
	{
		private int numInvocations;
		private double totalTime;
		private double startTime;
		
		public ProfileTimer()
		{
			numInvocations = 0;
			totalTime = 0;
			startTime = 0;
		}
		
		public void startInvocation()
		{
			startTime = Time.getTime();
		}
		
		public void endInvocation()
		{
			if(startTime == 0)
			{
				Log.error("Error: stop invocation without matching start");
				System.exit(-1);
			}
			numInvocations++;
			totalTime += (Time.getTime()-startTime);
			startTime = 0;
		}
		
		public double displayAndReset(String message)
		{
			return displayAndReset(message, 0);
		}
		
		public double displayAndReset(String message, double divisor)
		{
			double actualDivisor = divisor;
			if(actualDivisor == 0)
				actualDivisor = numInvocations;
			
			double time;
			if(actualDivisor == 0)
				time = 0;
			else
				time = (totalTime/(double)(actualDivisor));
			Log.message(message+": "+time+" ms");
			totalTime = 0;
			numInvocations = 0;
			return time;
		}
	}

	public static boolean display = false;
}
