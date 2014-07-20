package org.jge.maths;

import java.nio.FloatBuffer;

import org.jge.util.BufferWritable;


public class Quaternion implements BufferWritable
{
    public static final Quaternion NULL = new Quaternion(0,0,0,1);
    
	private double x;
    private double y;
    private double z;
    private double w;
    
    public Quaternion()
    {
        this(0,0,0,1);
    }
    
    public Quaternion(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Quaternion(Vector3 axis, double angle)
    {
        double sinHalfAngle = Maths.sin(angle/ 2) ;
        double cosHalfAngle = Maths.cos(angle/ 2);

        this.x = axis.getX() * sinHalfAngle;
        this.y = axis.getY() * sinHalfAngle;
        this.z = axis.getZ() * sinHalfAngle;
        this.w = cosHalfAngle;
    }
    
  //From Ken Shoemake's "Quaternion Calculus and Fast Animation" article
  	public Quaternion(Matrix4 rot)
  	{
  		float trace = (float)(rot.get(0, 0) + rot.get(1, 1) + rot.get(2, 2));

  		if(trace > 0)
  		{
  			float s = 0.5f / (float)Math.sqrt(trace+ 1.0f);
  			w = 0.25f / s;
  			x = (rot.get(1, 2) - rot.get(2, 1)) * s;
  			y = (rot.get(2, 0) - rot.get(0, 2)) * s;
  			z = (rot.get(0, 1) - rot.get(1, 0)) * s;
  		}
  		else
  		{
  			if(rot.get(0, 0) > rot.get(1, 1) && rot.get(0, 0) > rot.get(2, 2))
  			{
  				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(0, 0) - rot.get(1, 1) - rot.get(2, 2));
  				w = (rot.get(1, 2) - rot.get(2, 1)) / s;
  				x = 0.25f * s;
  				y = (rot.get(1, 0) + rot.get(0, 1)) / s;
  				z = (rot.get(2, 0) + rot.get(0, 2)) / s;
  			}
  			else if(rot.get(1, 1) > rot.get(2, 2))
  			{
  				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(1, 1) - rot.get(0, 0) - rot.get(2, 2));
  				w = (rot.get(2, 0) - rot.get(0, 2)) / s;
  				x = (rot.get(1, 0) + rot.get(0, 1)) / s;
  				y = 0.25f * s;
  				z = (rot.get(2, 1) + rot.get(1, 2)) / s;
  			}
  			else
  			{
  				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(2, 2) - rot.get(0, 0) - rot.get(1, 1));
  				w = (rot.get(0, 1) - rot.get(1, 0) ) / s;
  				x = (rot.get(2, 0) + rot.get(0, 2) ) / s;
  				y = (rot.get(1, 2) + rot.get(2, 1) ) / s;
  				z = 0.25f * s;
  			}
  		}

  		double length = length();
  		x /= length;
  		y /= length;
  		z /= length;
  		w /= length;
  	}
    
    public Matrix4 toRotationMatrix()
    {
        Vector3 forward = new Vector3(2.0f * (x*z - w*y), 2.0f * (y*z + w*x), 1.0f - 2.0f * (x*x + y*y));
        Vector3 up = new Vector3(2.0f * (x*y + w*z), 1.0f - 2.0f * (x*x + z*z), 2.0f * (y*z - w*x));
        Vector3 right = new Vector3(1.0f - 2.0f * (y*y + z*z), 2.0f * (x*y - w*z), 2.0f * (x*z + w*y));
        
        return new Matrix4().initRotation(forward, up, right);
    }

    public Vector3 getForward()
    {
        return new Vector3(0,0,1).rotate(this);
    }

    public Vector3 getBack()
    {
        return new Vector3(0,0,-1).rotate(this);
    }

    public Vector3 getUp()
    {
        return new Vector3(0,1,0).rotate(this);
    }

    public Vector3 getDown()
    {
        return new Vector3(0,-1,0).rotate(this);
    }

    public Vector3 getRight()
    {
        return new Vector3(1,0,0).rotate(this);
    }

    public Vector3 getLeft()
    {
        return new Vector3(-1,0,0).rotate(this);
    }
    
    public double length()
    {
        return Maths.sqrt(x*x+y*y+z*z+w*w);
    }
    
    public Quaternion nlerp(Quaternion dest, double lerpFactor, boolean shortest)
	{
		Quaternion correctedDest = dest;

		if(shortest && this.dot(dest) < 0)
			correctedDest = new Quaternion(-dest.getX(), -dest.getY(), -dest.getZ(), -dest.getW());

		return correctedDest.sub(this).mul(lerpFactor).add(this).normalize();
	}

	public Quaternion slerp(Quaternion dest, double lerpFactor, boolean shortest)
	{
		final double EPSILON = 1e3;

		double cos = this.dot(dest);
		Quaternion correctedDest = dest;

		if(shortest && cos < 0)
		{
			cos = -cos;
			correctedDest = new Quaternion(-dest.getX(), -dest.getY(), -dest.getZ(), -dest.getW());
		}

		if(Math.abs(cos) >= 1 - EPSILON)
			return nlerp(correctedDest, lerpFactor, false);

		double sin = Maths.sqrt(1.0f - cos * cos);
		double angle = Math.atan2(sin,cos);
		double invSin =  1.0f/sin;

		double srcFactor = Maths.sin((1.0f - lerpFactor) * angle) * invSin;
		double destFactor = Maths.sin((lerpFactor) * angle) * invSin;

		return this.mul(srcFactor).add(correctedDest.mul(destFactor)).normalize();
	}
	
	public Quaternion sub(Quaternion r)
	{
		return new Quaternion(x - r.getX(), y - r.getY(), z - r.getZ(), w - r.getW());
	}

	public Quaternion add(Quaternion r)
	{
		return new Quaternion(x + r.getX(), y + r.getY(), z + r.getZ(), w + r.getW());
	}

	public double dot(Quaternion r)
	{
		return x * r.getX() + y * r.getY() + z * r.getZ() + w * r.getW();
	}
	
    public Quaternion normalize()
    {
        double l = length();
        x/=l;
        y/=l;
        z/=l;
        w/=l;
        return this;
    }
    
    public Quaternion conjugate()
    {
        return new Quaternion(-x, -y, -z, w);
    }
    
    public Quaternion mul(double d)
    {
        return new Quaternion(x * d, y * d, z * d, w * d);
    }

    public Quaternion mul(Quaternion r)
    {
        double w_ = w * r.getW() - x * r.getX() - y * r.getY() - z * r.getZ();
        double x_ = x * r.getW() + w * r.getX() + y * r.getZ() - z * r.getY();
        double y_ = y * r.getW() + w * r.getY() + z * r.getX() - x * r.getZ();
        double z_ = z * r.getW() + w * r.getZ() + x * r.getY() - y * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }

    public Quaternion mul(Vector3 r)
    {
        double w_ = -x * r.getX() - y * r.getY() - z * r.getZ();
        double x_ =  w * r.getX() + y * r.getZ() - z * r.getY();
        double y_ =  w * r.getY() + z * r.getX() - x * r.getZ();
        double z_ =  w * r.getZ() + x * r.getY() - y * r.getX();
        return new Quaternion(x_, y_, z_, w_);
    }
    

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public double getW()
    {
        return w;
    }

    public void setW(double w)
    {
        this.w = w;
    }
    
    public boolean equals(Object o)
    {
        if(o instanceof Quaternion)
        {
            Quaternion other = (Quaternion)o;
            return other.getX() == getX() && other.getY() == getY() && other.getZ() == getZ() && other.getW() == getW(); 
        }
        return false;
    }

    public Quaternion set(Quaternion v)
    {
        this.x = v.getX();
        this.y = v.getY();
        this.z = v.getZ();
        this.w = v.getW();
        return this;
    }

	public Quaternion copy()
	{
		return new Quaternion(x, y, z, w);
	}

	public void write(FloatBuffer buffer)
	{
		buffer.put((float)getX());
		buffer.put((float)getY());
		buffer.put((float)getZ());
		buffer.put((float)getW());
	}

	public int getSize()
	{
		return 4;
	}

	public void set(double x, double y, double z, double w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector3 xyz()
	{
		return new Vector3(x, y, z);
	}
}
