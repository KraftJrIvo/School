package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

/**
 * Created by Kraft on 25.07.2015.
 */
public class DeathZone extends Entity {

    static class MyPoint2f{
        public float x, y;
        
        public MyPoint2f(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    enum ZoneShape {NONE, RECT, TRIANGLE, CIRCLE, DOT}
    ZoneShape shape = ZoneShape.NONE;
    float a=0, b=0;
    ArrayList<MyPoint2f> points;
    

    public DeathZone(AssetManager assets, Texture tex, float x, float y, float h, float floorHeight, int angle, ZoneShape shape, float a, float b) {
        super(assets, tex, x, y, h, floorHeight, angle);
        this.shape = shape;
        this.a = a;
        this.b = b;
    }

    public boolean collide(Rectangle rect) {
        if (shape == ZoneShape.NONE) return false;
        if (shape == ZoneShape.RECT) {
            Rectangle thisRect = new Rectangle(x, y, a, b);
            return thisRect.overlaps(rect);
        }
        if (points == null) points = new ArrayList<MyPoint2f>();
        if (shape == ZoneShape.TRIANGLE) {
            if (points.size() == 0) {
                /*for (int i = 0; i<a/2; i++) {
                    points.add(new MyPoint2f(x-a/2+i, y-i*b/a/2));
                    points.add(new MyPoint2f(x+a/2-i, y-i*b/a/2));
                }*/
                //x += 1;
                //y += 1;
                a -= 2;
                b -= 2;

                if (angle == 0) {
                    points.add(new MyPoint2f(x-a/2+a/2, y));
                    points.add(new MyPoint2f(x-a/4+a/2, y-b/2));
                    points.add(new MyPoint2f(x+a/2, y-b+2));
                    points.add(new MyPoint2f(x+a/4+a/2, y-b/2));
                    points.add(new MyPoint2f(x+a/2+a/2, y));
                } else if (angle == 1) {
                    points.add(new MyPoint2f(x+a/2+1, y-a/2));
                    points.add(new MyPoint2f(x+a/2+b/2+1, y-a/4));
                    points.add(new MyPoint2f(x+a/2+b-1, y));
                    points.add(new MyPoint2f(x+a/2+b/2+1, y+a/4));
                    points.add(new MyPoint2f(x+a/2+1, y+a/2));
                } else if (angle == 2) {
                    points.add(new MyPoint2f(x-a/2+a/2-1, y+2));
                    points.add(new MyPoint2f(x-a/4+a/2-1, y+b/2+2));
                    points.add(new MyPoint2f(x+a/2-1, y+b));
                    points.add(new MyPoint2f(x+a/4+a/2-1, y+b/2+2));
                    points.add(new MyPoint2f(x+a/2+a/2-1, y+2));
                } else if (angle == 3) {
                    points.add(new MyPoint2f(x+a/2, y-a/2));
                    points.add(new MyPoint2f(x-b/2+a/2, y-a/4));
                    points.add(new MyPoint2f(x-b+a/2+2, y));
                    points.add(new MyPoint2f(x-b/2+a/2, y+a/4));
                    points.add(new MyPoint2f(x+a/2, y+a/2));
                }

                //x -= 1;
                //y -= 1;
                a += 1;
                b += 1;
            }
        }
        else if (shape == ZoneShape.CIRCLE) {
            if (points.size() == 0) {
                if (angle == 0) {
                    points.add(new MyPoint2f(x+a/2+a/2, y-b/2));
                    points.add(new MyPoint2f(x-a/2+a/2, y-b/2));
                    points.add(new MyPoint2f(x+a/2, y));
                    points.add(new MyPoint2f(x+a/2, y-b));
                    points.add(new MyPoint2f(x+a/2+0.7f*(a/2), y-b/2-0.7f*(b/2)));
                    points.add(new MyPoint2f(x+a/2-0.7f*(a/2), y-b/2-0.7f*(b/2)));
                    points.add(new MyPoint2f(x+a/2+0.7f*(a/2), y-b/2+0.7f*(b/2)));
                    points.add(new MyPoint2f(x+a/2-0.7f*(a/2), y-b/2+0.7f*(b/2)));
                } else if (angle == 1) {
                    points.add(new MyPoint2f(x+a/2+a, y));
                    points.add(new MyPoint2f(x-a/2+a, y));
                    points.add(new MyPoint2f(x+a, y+b/2));
                    points.add(new MyPoint2f(x+a, y-b/2));
                    points.add(new MyPoint2f(x+a+0.7f*(a/2), y-b/2-0.7f*(b/2)+b/2));
                    points.add(new MyPoint2f(x+a-0.7f*(a/2), y-b/2-0.7f*(b/2)+b/2));
                    points.add(new MyPoint2f(x+a+0.7f*(a/2), y-b/2+0.7f*(b/2)+b/2));
                    points.add(new MyPoint2f(x+a-0.7f*(a/2), y-b/2+0.7f*(b/2)+b/2));
                } else if (angle == 2) {
                    points.add(new MyPoint2f(x+a/2+a/2, y-b/2+b));
                    points.add(new MyPoint2f(x-a/2+a/2, y-b/2+b));
                    points.add(new MyPoint2f(x+a/2, y+b));
                    points.add(new MyPoint2f(x+a/2, y));
                    points.add(new MyPoint2f(x+a/2+0.7f*(a/2), y-b/2-0.7f*(b/2)+b));
                    points.add(new MyPoint2f(x+a/2-0.7f*(a/2), y-b/2-0.7f*(b/2)+b));
                    points.add(new MyPoint2f(x+a/2+0.7f*(a/2), y-b/2+0.7f*(b/2)+b));
                    points.add(new MyPoint2f(x+a/2-0.7f*(a/2), y-b/2+0.7f*(b/2)+b));
                } else if (angle == 3) {
                    points.add(new MyPoint2f(x+a/2, y));
                    points.add(new MyPoint2f(x-a/2, y));
                    points.add(new MyPoint2f(x, y+b/2));
                    points.add(new MyPoint2f(x, y-b/2));
                    points.add(new MyPoint2f(x+0.7f*(a/2), y-b/2-0.7f*(b/2)+b/2));
                    points.add(new MyPoint2f(x-0.7f*(a/2), y-b/2-0.7f*(b/2)+b/2));
                    points.add(new MyPoint2f(x+0.7f*(a/2), y-b/2+0.7f*(b/2)+b/2));
                    points.add(new MyPoint2f(x-0.7f*(a/2), y-b/2+0.7f*(b/2)+b/2));
                }
            }
        } else if (shape == ZoneShape.DOT) {
            if (points.size() == 0) {
                points.add(new MyPoint2f(x+a/2, y-b/2));
            }
        }
        for (int i =0; i < points.size(); ++i) {
            if (rect.contains(points.get(i).x, points.get(i).y)) return true;
        }

        return false;
    }
}
