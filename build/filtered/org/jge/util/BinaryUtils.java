package org.jge.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryUtils
{

    public static short unsigned(byte b)
    {
        return (short)(b & 0xFF);
    }

    public static byte[] extract(byte[] data, int offset, int length)
    {
        byte[] result = new byte[length];
        for(int i = offset; i < offset + length; i++ )
            result[i - offset] = data[i];
        return result;
    }

    public static short asShort(byte[] data, int index, int length)
    {
        return (short)asLong(data, index, length);
    }

    public static long asLong(byte[] data, int index, int length)
    {
        return asLong(data, index, length, false);
    }
    
    public static long asLong(byte[] data, int index, int length, boolean reverse)
    {
        long result = 0;
        for(int i = index; i < index + length; i++ )
        {
            result |= (short)(((data[i] & 0xFF) << 8 * (reverse ? index-length-(i-index)-1 : (i - index))));
        }
        return result;
    }

    public static short[] asShorts(byte[] bytes)
    {
        int length = bytes.length;
        if(bytes.length % 2 != 0)
        {
            length++ ;
        }
        short[] result = new short[length];
        int i = 0;
        for(; i < result.length; i++ )
        {
            result[i] = (short)(((bytes[i / 2 + 1] << 8) & 0xFF) | (bytes[i / 2] & 0xFF));
        }
        if(bytes.length % 2 != 0)
        {
            result[length - 1] = bytes[bytes.length - 1];
        }
        return result;
    }

    public static int asInt(byte[] data, int index, int length)
    {
        return (int)asLong(data, index, length);
    }

    public static double asDouble(byte[] data, int index, int length)
    {
        byte[] bytes = extract(data, index, length);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    public static float asFloat(byte[] data, int index, int length)
    {
        byte[] bytes = extract(data, index, length);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
    
    protected final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String dumpBytes(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for(int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String toString(byte[] data) throws UnsupportedEncodingException
    {
        return new String(data, "UTF-8");
    }

}
