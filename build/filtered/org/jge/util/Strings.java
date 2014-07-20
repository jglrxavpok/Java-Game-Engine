package org.jge.util;

public class Strings
{

	public static String removeSpacesAtStart(String line)
	{
		while(!line.isEmpty() && line.startsWith(" "))
			line = line.substring(1);
		return line;
	}

}
