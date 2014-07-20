package org.jge.cl;

import org.jge.Disposable;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;

public class GPUProgramObject implements Disposable
{

	Object value;
	String type;
	
	public GPUProgramObject(int val)
	{
		value = val;
		type = "int";
	}
	
	public GPUProgramObject(double val)
	{
		value = val;
		type = "double";
	}
	
	public GPUProgramObject(float val)
	{
		value = val;
		type = "float";
	}
	
	public GPUProgramObject(long val)
	{
		value = val;
		type = "long";
	}
	
	public GPUProgramObject(short val)
	{
		value = val;
		type = "short";
	}
	
	public GPUProgramObject(byte val)
	{
		value = val;
		type = "byte";
	}
	
	public GPUProgramObject(CLMem val)
	{
		value = val;
		type = "clmem";
	}

	public void apply(CLKernel kernel, int id)
	{
		if(type.equals("int"))
		{
			kernel.setArg(id, (Integer)value);
		}
		else if(type.equals("float"))
		{
			kernel.setArg(id, (Float)value);
		}
		else if(type.equals("short"))
		{
			kernel.setArg(id, (Short)value);
		}
		else if(type.equals("long"))
		{
			kernel.setArg(id, (Long)value);
		}
		else if(type.equals("double"))
		{
			kernel.setArg(id, (Double)value);
		}
		else if(type.equals("byte"))
		{
			kernel.setArg(id, (Byte)value);
		}
		else if(type.equals("clmem"))
		{
			kernel.setArg(id, (CLMem)value);
		}
	}

	public void dispose()
	{
		if(type.equals("clmen"))
		{
			CL10.clReleaseMemObject((CLMem)value);
		}
	}
}
