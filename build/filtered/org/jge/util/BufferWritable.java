package org.jge.util;

import java.nio.FloatBuffer;

public interface BufferWritable
{

    public void write(FloatBuffer buffer);

    public int getSize();
}
