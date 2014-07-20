package org.jge.util;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * VERY OLD, MUCH TO UPDATE
 * @author jglrxavpok
 */
public class LWJGLHandler
{

	
	private static boolean	loaded;
	private static int	joystickPort;

	/**
	 * @throws Exception
	 */
	public static void load(String nativesFolder) throws Exception
	{
		if(!loaded)
		{
			File folder = new File(nativesFolder);
			if(!folder.exists())folder.mkdirs();
			if(folder.isDirectory())
			{
				installNatives(folder);
				System.setProperty("org.lwjgl.librarypath", nativesFolder);
			}
			loaded = true;
		}
	}
	
	private static void installNatives(File folder) throws Exception 
	{
		Log.message("OS found : "+System.getProperty("os.name")+ " "+System.getProperty("os.version"));
		Log.message("Installing natives...");
		if(getNameOfOS().equalsIgnoreCase("windows"))
		{
			if(!new File(folder.getPath()+"/jinput-dx8_64.dll").exists())
			{
				IO.copy(getFFJar("/windows/jinput-dx8_64.dll", LWJGLHandler.class), folder.getPath()+"/jinput-dx8_64.dll").close();
				IO.copy(getFFJar("/windows/jinput-dx8.dll", LWJGLHandler.class), folder.getPath()+"/jinput-dx8.dll").close();
				IO.copy(getFFJar("/windows/jinput-raw_64.dll", LWJGLHandler.class), folder.getPath()+"/jinput-raw_64.dll").close();
				IO.copy(getFFJar("/windows/jinput-raw.dll", LWJGLHandler.class), folder.getPath()+"/jinput-raw.dll").close();
				IO.copy(getFFJar("/windows/lwjgl.dll", LWJGLHandler.class), folder.getPath()+"/lwjgl.dll").close();
				IO.copy(getFFJar("/windows/lwjgl64.dll", LWJGLHandler.class), folder.getPath()+"/lwjgl64.dll").close();
				IO.copy(getFFJar("/windows/OpenAL32.dll", LWJGLHandler.class), folder.getPath()+"/OpenAL32.dll").close();
				IO.copy(getFFJar("/windows/OpenAL64.dll", LWJGLHandler.class), folder.getPath()+"/OpenAL64.dll").close();
			}
			else
			{
			    Log.message("Natives already exist.");
			}
		}
		if(getNameOfOS().equalsIgnoreCase("solaris"))
		{
			if(!new File(folder.getPath()+"/liblwjgl.so").exists())
			{
				IO.copy(getFFJar("/solaris/liblwjgl.so", LWJGLHandler.class), folder.getPath()+"/liblwjgl.so").close();
				IO.copy(getFFJar("/solaris/liblwjgl64.so", LWJGLHandler.class), folder.getPath()+"/liblwjgl64.so").close();
				IO.copy(getFFJar("/solaris/libopenal.so", LWJGLHandler.class), folder.getPath()+"/libopenal.so").close();
				IO.copy(getFFJar("/solaris/libopenal64.so", LWJGLHandler.class), folder.getPath()+"/libopenal64.so").close();
			}
			else
			{
			    Log.message("Natives already exist.");
			}

		}
		if(getNameOfOS().equalsIgnoreCase("linux"))
		{
			if(!new File(folder.getPath()+"/liblwjgl.so").exists())
			{
				IO.copy(getFFJar("/linux/liblwjgl.so", LWJGLHandler.class), folder.getPath()+"/liblwjgl.so").close();
				IO.copy(getFFJar("/linux/liblwjgl64.so", LWJGLHandler.class), folder.getPath()+"/liblwjgl64.so").close();
				IO.copy(getFFJar("/linux/libopenal.so", LWJGLHandler.class), folder.getPath()+"/libopenal.so").close();
				IO.copy(getFFJar("/linux/libopenal64.so", LWJGLHandler.class), folder.getPath()+"/libopenal64.so").close();
			}
			else
			{
			    Log.message("Natives already exist.");
			}

		}
		if(getNameOfOS().equalsIgnoreCase("macosx"))
		{
			if(!new File(folder.getPath()+"/openal.dylib").exists())
			{
				IO.copy(getFFJar("/macosx/liblwjgl.jnilib", LWJGLHandler.class), folder.getPath()+"/liblwjgl.jnilib").close();
				IO.copy(getFFJar("/macosx/liblwjgl-osx.jnilib", LWJGLHandler.class), folder.getPath()+"/liblwjgl-osx.jnilib").close();
				IO.copy(getFFJar("/macosx/openal.dylib", LWJGLHandler.class), folder.getPath()+"/openal.dylib").close();
			}
			else
			{
			    Log.message("Natives already exist.");
			}

		}
	    System.setProperty("net.java.games.input.librarypath", folder.getPath()+"/");
	}

	public static String getNameOfOS() 
	{
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win"))
		{
			return "Windows";
		}
		if(os.contains("sunos") || os.contains("solaris"))
		{
			return "Solaris";
		}
		if(os.contains("unix"))
		{
			return "Linux";
		}
		if(os.contains("mac"))
		{
			return "Macosx";
		}
		return "Unknown";
	}
	
	private static InputStream getFFJar(String string, Class<?> class1)
	{
		return new BufferedInputStream(class1.getResourceAsStream(string));
	}
	
	public static int getJoystickPort()
	{
		return joystickPort;
	}
	
	public static int setJoystickPort(int value)
	{
		return (joystickPort = value);
	}
	
	/**
	 */
	public static float getValueOfJoystick(final String value) throws LWJGLException
	{
		Controllers.create(); 
		Controller joystick = createJoystick();
		Controllers.destroy();
		if(joystick != null)
		{
			if(value.equalsIgnoreCase("x"))return joystick.getXAxisValue();
			else if(value.equalsIgnoreCase("y"))return (float) -(joystick.getYAxisValue());
			else if(value.equalsIgnoreCase("z"))return joystick.getZAxisValue();
			else if(value.equalsIgnoreCase("povx"))return joystick.getPovX();
			else if(value.equalsIgnoreCase("povy"))return joystick.getPovY();
			else return 0;
		}
		return 0;
	}
	
	public static String POS_X = "x";
	public static String POS_Y = "y";
	public static String POS_POV_X = "povx";
	public static String POS_POV_Y = "povy";
    private static int[] screenshotBufferArray;
    private static IntBuffer screenshotBuffer;
	
	/**
	 * @throws LWJGLException
	 */
	public static boolean isButtonDown(int button) throws LWJGLException
	{
		Controllers.create();
		Controller joystick = createJoystick();
		Controllers.destroy();
		if(joystick != null)
		{
			if(button <= joystick.getButtonCount())
			{
				return joystick.isButtonPressed(button);
			}
		}
		return false;
	}
	
	/**
	 * @throws LWJGLException
	 */
	public static Controller createJoystick() throws LWJGLException
	{
		Controllers.create();
		Controller joystick = null;
		if(Controllers.getControllerCount() > 0)
			joystick = Controllers.getController(getJoystickPort());
		Controllers.destroy();
		return joystick;
	}

	public static boolean isButtonDown(int button, Controller controller) throws LWJGLException
	{
		Controllers.create(); 
		Controller joystick = null;
		if(Controllers.getControllerCount() > 0)
		joystick = Controllers.getController(controller.getIndex());
		Controllers.destroy();
		if(joystick != null)
		{
			if(button <= joystick.getButtonCount())
			{
				return joystick.isButtonPressed(button);
			}
		}
		return false;
	}

	public static float getValueOfJoystick(String value, Controller controller) throws LWJGLException
	{
		Controllers.create(); 
		Controller joystick = null;
		if(Controllers.getControllerCount() > 0)
		joystick = Controllers.getController(controller.getIndex());
		Controllers.destroy();
		if(joystick != null)
		{
			if(value.equalsIgnoreCase("x"))return joystick.getXAxisValue();
			else if(value.equalsIgnoreCase("y"))return (float) -(joystick.getYAxisValue());
			else if(value.equalsIgnoreCase("z"))return joystick.getZAxisValue();
			else if(value.equalsIgnoreCase("povx"))return joystick.getPovX();
			else if(value.equalsIgnoreCase("povy"))return joystick.getPovY();
			else return 0;
		}
		return 0;
	}
	
	public static int loadTexture(InputStream in)
	{
        BufferedImage i;
        try 
        {
        	i = ImageIO.read(in);
        	return loadTexture(i);
        } 
        catch (IOException e1) 
        {
        	e1.printStackTrace();
        	return -1;
        }
	}
	
	public static int loadTexture(BufferedImage img) 
	{
		int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
		 
		int bufLen = pixels.length * 4;
		ByteBuffer oglPixelBuf = BufferUtils.createByteBuffer(bufLen);
		 
		for (int y = img.getHeight()-1; y >= 0; y--) 
		{
			for (int x = 0; x < img.getWidth(); x++) 
			{
				int rgb = pixels[y*img.getWidth()+x];
				float a = ((rgb >> 24) & 0xFF)/255f;  
				float r = ((rgb >> 16) & 0xFF)/255f;
				float g = ((rgb >> 8) & 0xFF)/255f;
				float b = ((rgb >> 0) & 0xFF)/255f;
				oglPixelBuf.put((byte) (r*255f));
				oglPixelBuf.put((byte) (g*255f));
				oglPixelBuf.put((byte) (b*255f));
				oglPixelBuf.put((byte) (a*255f));
			}
		}

		oglPixelBuf.flip();
		
		ByteBuffer temp = ByteBuffer.allocateDirect(4);
		temp.order(ByteOrder.nativeOrder());
		IntBuffer textBuf = temp.asIntBuffer();
		GL11.glGenTextures(textBuf);
		int textureID = textBuf.get(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

		// TODO: We take  a flag for whether or not to store the alpha, but if we don't check that flag for the options 
		// below - they assume alpha, and if useTextureAlpha is false we only allocate num pixels * 3, so the 
		// call below then throws an exception.   Long story short either always assume we want alpha or fix the
		// code below to change those options depending on flag setting.
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
				0,
				GL11.GL_RGBA8,
				img.getWidth(),
				img.getHeight(),
				0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				oglPixelBuf);

		return textureID;
    }

    public static BufferedImage takeScreenshot()
    {
        int k = Display.getWidth()*Display.getHeight();
        if (screenshotBuffer == null || screenshotBuffer.capacity() < k)
        {
            screenshotBuffer = BufferUtils.createIntBuffer(k);
            screenshotBufferArray = new int[k];
        }
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        screenshotBuffer.clear();
        GL11.glReadPixels(0, 0, Display.getWidth(), Display.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, screenshotBuffer);
        screenshotBuffer.get(screenshotBufferArray);
        int[] aint1 = new int[Display.getWidth()];
        int j = Display.getHeight() / 2;
        for (int l = 0; l < j; ++l)
        {
            System.arraycopy(screenshotBufferArray, l * Display.getWidth(), aint1, 0, Display.getWidth());
            System.arraycopy(screenshotBufferArray, (Display.getHeight() - 1 - l) * Display.getWidth(), screenshotBufferArray, l * Display.getWidth(), Display.getWidth());
            System.arraycopy(aint1, 0, screenshotBufferArray, (Display.getHeight() - 1 - l) * Display.getWidth(), Display.getWidth());
        }
        BufferedImage bufferedimage = new BufferedImage(Display.getWidth(), Display.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0;i<screenshotBufferArray.length;i++)
        {
            Color c = ImageUtils.getColor(screenshotBufferArray[i]);
            screenshotBufferArray[i] = new Color(c.getBlue(), c.getGreen(), c.getRed()).getRGB();
        }
        bufferedimage.setRGB(0, 0, Display.getWidth(), Display.getHeight(), screenshotBufferArray, 0, Display.getWidth());
        return bufferedimage;
    }
}
