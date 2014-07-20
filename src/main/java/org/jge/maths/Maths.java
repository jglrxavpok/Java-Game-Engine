package org.jge.maths;

import java.util.Random;

import org.jge.RuntimeEngineException;

public class Maths
{

    public static final double TAU = 6.28318530717958647692528676655900576839433879875021;
    
    public static final double PI = TAU/2.0;
    
    private static Random localRandom = new Random();
    
    public static double rand()
    {
        return localRandom.nextDouble();
    }
    
    public static double toRadians(double adegrees)
    {
        return (adegrees % 360 / 360.0D * TAU) ;
    }
    
    public static double toDegrees(double arad)
    {
        return (arad * 360.0D / TAU);
    }

    public static double cos(double arad)
    {
        return cos(arad, 20);
    }

    public static double cos(double arad, int precision)
    {
        return trig(arad, precision, 2, 1);
    }    

    public static double sin(double arad)
    {
        return sin(arad, 20);
    }

    public static double sin(double arad, int precision)
    {
        return trig(arad, precision, 3, arad);
    }
    
    public static double tan(double arad)
    {
        return StrictMath.tan(arad);
    }
    
    public static double atan(double tan)
    {
        return StrictMath.atan(tan);
    }
    
    public static double acos(double cos)
    {
        return StrictMath.acos(cos);
    }
    
    public static double asin(double sin)
    {
        return StrictMath.asin(sin);
    }
    
    /**
     * @param arad
     * @param precision
     * @param start : 2 is for cosinus and 3 for sine
     * @return
     */
    public static double trig(double arad, int precision, int start, double firstValue)
    {
        if(precision < 0)
            throw new RuntimeEngineException("precision must be >= 0");
        arad = arad % TAU;
        firstValue = firstValue % TAU;
        double result = firstValue;
        for(int i = 0;i<precision;i++)
        {
            int pos = start+i*2;
            double val = pow(arad, pos)/factorial(pos);
            if(i % 2 == 0)
            {
                result -= val;
            }
            else
            {
                result += val;
            }
        }
        return result;
    }
    
    /**
     * Not yet optimized, please do NOT use values > 69 or < 0
     * @param value
     * @return
     */
    public static double factorial(int value)
    {
        if(value <= 0)
            return 1;
        return factorial(value-1)*value;
    }

    public static double pow(double x, int pow)
    {
        if (pow<0) return pow(1/x, -pow);
        else if (pow == 0) return 1;
        else if (pow == 1) return x;
        else if (pow % 2 == 0) return pow(x*x, pow/2);
        else return x * pow(x*x, (pow-1)/2);
    }

    /**
     * Rely on Java
     * @param num
     * @return
     */
    public static double sqrt(double num)
    {
        return StrictMath.sqrt(num);
    }
    
    public static long numbits(long num)
    {
        long n = num;
        long result = 0;
        if (n < 0)
            n = -n;
        while (n != 0)
        {
            ++result;
            n >>= 1;
        }
        return result;
    }

    public static double floor(double d)
    {
        return (int)d;
    }
    
    public static double ceil(double d)
    {
        return ((int)d)+1.0;
    }

    public static float sign(double num)
    {
        return num < 0.0 ? num == 0.0 ? 0 : 1 : -1; 
    }

    public static double abs(double n)
    {
        if(n < 0.0)
            return 0.0 - n;
        return n;
    }

    
    public static int min(int a, int b)
    {
        if(a < b)
            return a;
        return b;
    }
    
    public static int max(int d, int e)
    {
        if(d > e)
            return d;
        return e;
    }
    
    public static double min(double a, double b)
    {
        if(a < b)
            return a;
        return b;
    }
    
    public static double max(double d, double e)
    {
        if(d > e)
            return d;
        return e;
    }
}
