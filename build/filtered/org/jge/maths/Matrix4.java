package org.jge.maths;

import java.nio.FloatBuffer;

import org.jge.util.BufferWritable;

public class Matrix4 implements BufferWritable
{

    private double[][] _m;
    
    public Matrix4()
    {
        _m = new double[4][4];
    }

    public double[][] getM()
    {
        return _m;
    }

    public double get(int x, int y)
    {
        return _m[x][y];
    }
    
    public void set(int x, int y, double value)
    {
        _m[x][y] = value;
    }
    
    public Matrix4 mul(Matrix4 r)
    {
        Matrix4 res = new Matrix4();

        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                res.set(i, j, _m[i][0] * r.get(0, j) +
                              _m[i][1] * r.get(1, j) +
                              _m[i][2] * r.get(2, j) +
                              _m[i][3] * r.get(3, j));
            }
        }

        return res;
    }
    
    public Matrix4 copy()
    {
        return new Matrix4().set(_m);
    }
    
    public Matrix4 set(Matrix4 m)
    {
        for(int i = 0;i<4;i++)
        {
            for(int j = 0;j<4;j++)
            {
                this._m[i][j] = m.get(i, j);
            }
        }
        return this;
    }
    
    public Matrix4 set(double[][] m)
    {
        for(int i = 0;i<4;i++)
        {
            for(int j = 0;j<4;j++)
            {
                this._m[i][j] = m[i][j];
            }
        }
        return this;
    }

    public Matrix4 initIdentity()
    {
        _m[0][0] = 1;    _m[0][1] = 0;    _m[0][2] = 0;    _m[0][3] = 0;
        _m[1][0] = 0;    _m[1][1] = 1;    _m[1][2] = 0;    _m[1][3] = 0;
        _m[2][0] = 0;    _m[2][1] = 0;    _m[2][2] = 1;    _m[2][3] = 0;
        _m[3][0] = 0;    _m[3][1] = 0;    _m[3][2] = 0;    _m[3][3] = 1;

        return this;
    }

    public Matrix4 initTranslation(double x, double y, double z)
    {
        _m[0][0] = 1;    _m[0][1] = 0;    _m[0][2] = 0;    _m[0][3] = x;
        _m[1][0] = 0;    _m[1][1] = 1;    _m[1][2] = 0;    _m[1][3] = y;
        _m[2][0] = 0;    _m[2][1] = 0;    _m[2][2] = 1;    _m[2][3] = z;
        _m[3][0] = 0;    _m[3][1] = 0;    _m[3][2] = 0;    _m[3][3] = 1;

        return this;
    }

    public Matrix4 initRotation(double x, double y, double z)
    {
        Matrix4 rx = new Matrix4();
        Matrix4 ry = new Matrix4();
        Matrix4 rz = new Matrix4();

        x = Maths.toRadians(x);
        y = Maths.toRadians(y);
        z = Maths.toRadians(z);

        rz._m[0][0] = Maths.cos(z);rz._m[0][1] = -Maths.sin(z);rz._m[0][2] = 0;                rz._m[0][3] = 0;
        rz._m[1][0] = Maths.sin(z);rz._m[1][1] = Maths.cos(z);rz._m[1][2] = 0;                 rz._m[1][3] = 0;
        rz._m[2][0] = 0;                 rz._m[2][1] = 0;                 rz._m[2][2] = 1;                 rz._m[2][3] = 0;
        rz._m[3][0] = 0;                 rz._m[3][1] = 0;                 rz._m[3][2] = 0;                 rz._m[3][3] = 1;

        rx._m[0][0] = 1;                 rx._m[0][1] = 0;                 rx._m[0][2] = 0;                 rx._m[0][3] = 0;
        rx._m[1][0] = 0;                 rx._m[1][1] = Maths.cos(x);rx._m[1][2] = -Maths.sin(x);rx._m[1][3] = 0;
        rx._m[2][0] = 0;                 rx._m[2][1] = Maths.sin(x);rx._m[2][2] = Maths.cos(x);rx._m[2][3] = 0;
        rx._m[3][0] = 0;                 rx._m[3][1] = 0;                 rx._m[3][2] = 0;                 rx._m[3][3] = 1;

        ry._m[0][0] = Maths.cos(y);ry._m[0][1] = 0;                 ry._m[0][2] = -Maths.sin(y);ry._m[0][3] = 0;
        ry._m[1][0] = 0;                 ry._m[1][1] = 1;                 ry._m[1][2] = 0;                 ry._m[1][3] = 0;
        ry._m[2][0] = Maths.sin(y);ry._m[2][1] = 0;                 ry._m[2][2] = Maths.cos(y);ry._m[2][3] = 0;
        ry._m[3][0] = 0;                 ry._m[3][1] = 0;                 ry._m[3][2] = 0;                 ry._m[3][3] = 1;

        _m = rz.mul(ry.mul(rx)).getM();

        return this;
    }

    public Matrix4 initScale(double x, double y, double z)
    {
        _m[0][0] = x;    _m[0][1] = 0;    _m[0][2] = 0;    _m[0][3] = 0;
        _m[1][0] = 0;    _m[1][1] = y;    _m[1][2] = 0;    _m[1][3] = 0;
        _m[2][0] = 0;    _m[2][1] = 0;    _m[2][2] = z;    _m[2][3] = 0;
        _m[3][0] = 0;    _m[3][1] = 0;    _m[3][2] = 0;    _m[3][3] = 1;

        return this;
    }
    
    public Matrix4 initPerspective(double fov, double aspectRatio, double zNear, double zFar)
    {
        double tanHalfFOV = Maths.tan(fov / 2);
        double zRange = zNear - zFar;

        _m[0][0] = 1.0f / (tanHalfFOV * aspectRatio);    _m[0][1] = 0;                    _m[0][2] = 0;    _m[0][3] = 0;
        _m[1][0] = 0;                        _m[1][1] = 1.0f / tanHalfFOV;    _m[1][2] = 0;    _m[1][3] = 0;
        _m[2][0] = 0;                        _m[2][1] = 0;                    _m[2][2] = (-zNear -zFar)/zRange;    _m[2][3] = 2 * zFar * zNear / zRange;
        _m[3][0] = 0;                        _m[3][1] = 0;                    _m[3][2] = 1;    _m[3][3] = 0;


        return this;
    }

    public Matrix4 initOrthographic(double left, double right, double bottom, double top, double near, double far)
    {
        double width = right - left;
        double height = top - bottom;
        double depth = far - near;

        _m[0][0] = 2/width;_m[0][1] = 0;  _m[0][2] = 0;    _m[0][3] = -(right + left)/width;
        _m[1][0] = 0;    _m[1][1] = 2/height;_m[1][2] = 0; _m[1][3] = -(top + bottom)/height;
        _m[2][0] = 0;    _m[2][1] = 0;    _m[2][2] = -2/depth;_m[2][3] = -(far + near)/depth;
        _m[3][0] = 0;    _m[3][1] = 0;    _m[3][2] = 0;    _m[3][3] = 1;

        return this;
    }
    
    public Matrix4 initRotation(Vector3 forward, Vector3 up)
    {
        Vector3 f = forward.normalize();
        Vector3 r = f.cross(up).normalize();
        Vector3 u = up.normalize();
        return initRotation(f, u, r);
    }

    public Matrix4 initRotation(Vector3 forward, Vector3 up, Vector3 right)
    {
        Vector3 f = forward;
        Vector3 r = right;
        Vector3 u = up;

        _m[0][0] = r.getX(); _m[0][1] = r.getY(); _m[0][2] = r.getZ(); _m[0][3] = 0;
        _m[1][0] = u.getX(); _m[1][1] = u.getY(); _m[1][2] = u.getZ(); _m[1][3] = 0;
        _m[2][0] = f.getX(); _m[2][1] = f.getY(); _m[2][2] = f.getZ(); _m[2][3] = 0;
        _m[3][0] = 0;        _m[3][1] = 0;        _m[3][2] = 0;        _m[3][3] = 1;

        return this;
    }

    public Vector3 transform(Vector3 r)
    {
        return new Vector3(_m[0][0] * r.getX() + _m[0][1] * r.getY() + _m[0][2] * r.getZ() + _m[0][3],
                            _m[1][0] * r.getX() + _m[1][1] * r.getY() + _m[1][2] * r.getZ() + _m[1][3],
                            _m[2][0] * r.getX() + _m[2][1] * r.getY() + _m[2][2] * r.getZ() + _m[2][3]);
    }


    public void write(FloatBuffer buffer)
    {
        for(int i = 0;i<4;i++)
        {
            for(int j = 0;j<4;j++)
            {
                buffer.put((float)get(i,j));
            }
        }
    }
    
    public int getSize()
    {
        return 4*4;
    }
}
