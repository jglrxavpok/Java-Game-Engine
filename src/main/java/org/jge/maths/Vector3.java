package org.jge.maths;

import java.nio.FloatBuffer;

import org.jge.util.BufferWritable;

public class Vector3 implements BufferWritable
{

    public static final Vector3 NULL = new Vector3(0, 0, 0);
    
	public double x;
    public double y;
    public double z;

    public Vector3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public String toString()
    {
        return "vec3("+x+","+y+","+z+")";
    }
    
    public Vector3 normalize()
    {
        double l = length();
        if(l == 0)
            return new Vector3(0, 0, 0);
        double _x = this.x/l;
        double _y = this.y/l;
        double _z = this.z/l;
        return new Vector3(_x,_y,_z);
    }
    
    /**
     * @param v
     * @return
     */
    public double dot(Vector3 r)
    {
        return x * r.x + y * r.y + z * r.z;
    }
    
    public Vector3 cross(Vector3 v)
    {
        double x = this.y*v.z-this.z*v.y;
        double y = this.z*v.x-this.x*v.z;
        double z = this.x*v.y-this.y*v.x;
        return new Vector3(x,y,z);
    }
    
    public Vector3 add(double factor)
    {
        return new Vector3(this.x+factor, this.y+factor, this.z+factor);
    }
    
    public Vector3 add(double x, double y, double z)
    {
        return new Vector3(this.x+x, this.y+y, this.z+z);
    }
    
    public Vector3 add(Vector3 v)
    {
        return new Vector3(this.x+v.x, this.y+v.y, this.z+v.z);
    }
    
    public Vector3 sub(double factor)
    {
        return new Vector3(this.x-factor, this.y-factor, this.z-factor);
    }
    
    public Vector3 sub(double x, double y, double z)
    {
        return new Vector3(this.x-x, this.y-y, this.z+z);
    }
    
    public Vector3 sub(Vector3 v)
    {
        return new Vector3(this.x-v.x, this.y-v.y, this.z+v.z);
    }
    
    public Vector3 div(double factor)
    {
        return new Vector3(this.x/factor, this.y/factor, this.z/factor);
    }
    
    public Vector3 div(double x, double y, double z)
    {
        return new Vector3(this.x/x, this.y/y, this.z/z);
    }
    
    public Vector3 div(Vector3 v)
    {
        return new Vector3(this.x/v.x, this.y/v.y, this.z/v.z);
    }
    
    public Vector3 mul(double factor)
    {
        return new Vector3(this.x*factor, this.y*factor, this.z*factor);
    }
    
    public Vector3 mul(Vector3 v)
    {
        return new Vector3(this.x*v.x,this.y*v.y, this.z*v.z);
    }
    
    public Vector3 mul(double x, double y, double z)
    {
        return new Vector3(this.x*x,this.y*y, this.z*z);
    }
    
    public double length()
    {
        return Maths.sqrt(x*x+y*y+z*z);
    }
    
    public Vector3 set(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public double getX()
    {
        return x;
    }
    
    public double getY()
    {
        return y;
    }
    
    public double getZ()
    {
        return z;
    }

    public void write(FloatBuffer buffer)
    {
        buffer.put((float)getX());
        buffer.put((float)getY());
        buffer.put((float)getZ());
    }
    
    public int getSize()
    {
        return 3;
    }

    public Vector3 rotate(double angle, Vector3 axis)
    {
    	double sinAngle = Math.sin(-angle);
        double cosAngle = Math.cos(-angle);

        return this.cross(axis.mul(sinAngle)).add(           //Rotation on local X
                (this.mul(cosAngle)).add(                     //Rotation on local Z
                        axis.mul(this.dot(axis.mul(1 - cosAngle))))); //Rotation on local Y
    }
    
    public Vector3 rotate(Quaternion rotation)
    {
        Quaternion conjugate = rotation.conjugate();

        Quaternion w = rotation.mul(copy()).mul(conjugate);

        return new Vector3(w.getX(), w.getY(), w.getZ());
    }
    
    public Vector3 copy()
    {
        return new Vector3(x, y, z);
    }

    public Vector3 negative()
    {
        return new Vector3(-x, -y, -z);
    }

    public Vector2 xy()
    {
        return new Vector2(x, y);
    }
    
    public Vector2 xz()
    {
        return new Vector2(x, z);
    }
    
    public Vector2 zx()
    {
        return new Vector2(z, x);
    }
    
    public Vector2 yx()
    {
        return new Vector2(y, x);
    }
    
    public Vector2 zy()
    {
        return new Vector2(z, y);
    }
    
    public Vector2 yz()
    {
        return new Vector2(y, z);
    }
    
    public Vector3 lerp(Vector3 dest, double factor)
    {
        return dest.sub(this).mul(factor).add(this);
    }
    
    public boolean equals(Object o)
    {
        if(o == null)
            return false;
        if(o instanceof Vector3)
        {
            Vector3 other = (Vector3)o;
            return x == other.getX() && y == other.getY() && z == other.getZ();
        }
        return false;
    }
    
    public double max()
    {
        return Maths.max(getX(), Maths.max(getY(), getZ()));
    }

    public Vector3 set(Vector3 v)
    {
        this.x = v.getX();
        this.y = v.getY();
        this.z = v.getZ();
        return this;
    }
    
    public Vector3 setX(double x)
	{
		this.x = x;
		return this;
	}
    
	public Vector3 setY(double y)
	{
		this.y = y;
		return this;
	}
	
	public Vector3 setZ(double z)
	{
		this.z = z;
		return this;
	}

	public static Vector3 cross(Vector3 a, Vector3 b)
	{
		return a.cross(b);
	}
	
	public static double dot(Vector3 a, Vector3 b)
	{
		return a.dot(b);
	}
}
