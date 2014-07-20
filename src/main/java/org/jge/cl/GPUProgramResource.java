package org.jge.cl;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;

import org.jge.AbstractResource;
import org.jge.EngineException;
import org.jge.RuntimeEngineException;
import org.jge.util.BinaryUtils;
import org.jge.util.Buffers;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLProgram;

public class GPUProgramResource
{

	private int referenceCounter;
	private static CLPlatform clPlatform;
	private static List<CLDevice> clDevices;
	private static CLContext clContext;
	private CLProgram clProgram;
	private static CLCommandQueue clQueue;
	private HashMap<String, CLKernel> kernels = new HashMap<String, CLKernel>();
	private AbstractResource res;

	public GPUProgramResource(AbstractResource res) throws EngineException
	{
		try
		{
			this.res = res;
    		initCLIfNecessary();
    		String text = BinaryUtils.toString(res.getData());
    		
    		clProgram = CL10.clCreateProgramWithSource(clContext, text, null);
    		
    		CL10.clBuildProgram(clProgram, clDevices.get(0), "", null);
		}
		catch(Exception e)
		{
			throw new EngineException("Exception while loading CLProgram: "+res.getResourceLocation().getFullPath(), e);
		}
	}
	
	public void runKernel(String kernelName, GPUProgramObject[] args, int globalWorkSizeInt)
	{
		if(!kernels.containsKey(kernelName))
		{
			 preloadKernel(kernelName);
		}
		
		CLKernel kernel = kernels.get(kernelName);
		int index = 0;
		for(GPUProgramObject o : args)
		{
			o.apply(kernel, index++);
		}
		
		final int dimensions = 1;
		PointerBuffer globalWorkSize = BufferUtils.createPointerBuffer(dimensions);
		globalWorkSize.put(0, globalWorkSizeInt);

		CL10.clEnqueueNDRangeKernel(clQueue, kernel, dimensions, null, globalWorkSize, null, null, null);

		CL10.clFinish(clQueue);
	}
	
	private static void initCLIfNecessary() throws Exception
	{
		if(clPlatform == null)
		{
			clPlatform = CLPlatform.getPlatforms().get(0);
			clDevices = clPlatform.getDevices(CL10.CL_DEVICE_TYPE_GPU);
			clContext = CLContext.create(clPlatform, clDevices, null); // TODO: replace null
			clQueue = CL10.clCreateCommandQueue(clContext, clDevices.get(0), CL10.CL_QUEUE_PROFILING_ENABLE, null); // TODO: replace null
		}
	}
	
	public static void destroyAll()
	{
		if(clPlatform != null)
		{
			CL10.clReleaseCommandQueue(clQueue);
			CL10.clReleaseContext(clContext);
		}
	}

	public boolean decreaseCounter()
    {
    	referenceCounter--;
    	return referenceCounter <= 0;
    }
    
    public void increaseCounter()
    {
    	referenceCounter++;
    }
    
    @Override
    protected void finalize()
    {
    	dispose();
    }
    
    public void dispose()
    {
    	for(CLKernel kernel : kernels.values())
    		CL10.clReleaseKernel(kernel);
    	CL10.clReleaseProgram(clProgram);
    }
    
	public GPUProgramObject createMemory(float[] data)
	{
		FloatBuffer buffer = Buffers.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.rewind();
		return createMemory(buffer);
	}

	public GPUProgramObject createMemory(FloatBuffer buffer)
	{
		return new GPUProgramObject(CL10.clCreateBuffer(clContext, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buffer, null));
	}

	public GPUProgramObject createMemory(int size)
	{
		return new GPUProgramObject(CL10.clCreateBuffer(clContext, CL10.CL_MEM_READ_ONLY, size, null));
	}
	
	public void read(GPUProgramObject o, FloatBuffer writeTo, boolean blocking)
	{
		CL10.clEnqueueReadBuffer(clQueue, (CLMem)o.value, blocking ? CL10.CL_TRUE : CL10.CL_FALSE, 0, writeTo, null, null);
		return;
	}

	public void preloadKernel(String kernelName)
	{
		CLKernel k = CL10.clCreateKernel(clProgram, kernelName, null);
		kernels.put(kernelName, k);
		if(!k.isValid())
		{
			throw new RuntimeEngineException("Kernel " + kernelName
					+ " not valid in clProgram "
					+ res.getResourceLocation().getFullPath());
		}
	}

	public void preloadKernel(String kernelName, GPUProgramObject[] args, int size)
	{
		preloadKernel(kernelName);
		CLKernel kernel = kernels.get(kernelName);
		int index = 0;
		for(GPUProgramObject o : args)
		{
			o.apply(kernel, index++);
		}
		
		PointerBuffer globalWorkSize = BufferUtils.createPointerBuffer(1);
		globalWorkSize.put(0, size);
	}
}
