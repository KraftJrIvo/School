package com.mygdx.schoolRPG;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Kraft on 27.12.2014.
 */
public class HittableEntity extends Entity {

    Rectangle hitBox;
    boolean movable;
    boolean leftSide, rightSide, downSide, upSide;
    float oldX, oldY, floorHeight, graphicX, graphicY, oldY2;
    float z = 0, zSpeed = 0;
    int pSpeed = 0;
    boolean falling;

    public HittableEntity(AssetManager assets, String texPath, float x, float y, float width, float height, float floorHeight, boolean movable) {
        super(assets, texPath, x, y);
        this.floorHeight = floorHeight;
        hitBox = new Rectangle(x, y, width, height);
        this.movable = movable;
        leftSide = rightSide = downSide = upSide = true;
        //this.width = width;
        //this.height = height;
        oldX = hitBox.x;
        oldY = -1000;//hitBox.y;
        graphicX = hitBox.x;
        graphicY = hitBox.y;
    }

    public void setSides(boolean l, boolean r, boolean d, boolean u) {
        leftSide = l;
        rightSide = r;
        downSide = d;
        upSide = u;
    }

    @Override
    Rectangle getRect() {
        return hitBox;
    }

    @Override
    void setRect(Rectangle rect) {
        hitBox = rect;
    }

    @Override
    public Rectangle invalidate(Rectangle rect, Area area, float oldX, float oldY, boolean objectIsMovable, boolean objectIsPlayer) {

        boolean overlapX = false, overlapY = false;
        boolean platformMode = area.platformMode;

        if (rect.x+rect.width > hitBox.x+0.2f && rect.x < hitBox.x+hitBox.width-0.2f) {
            overlapX = true;
        }

        if (rect.y+rect.height > hitBox.y+0.3f && rect.y < hitBox.y+hitBox.height-0.3f) {
            overlapY = true;
        }

        if (overlapX && overlapY && hitBox.overlaps(rect) && hitBox != rect) {
            oldY2 = hitBox.y;
            if (!movable && !objectIsMovable) {
                return rect;
            }
            Rectangle newHitBox = hitBox;
            boolean canUP = (newHitBox.getY() >= hitBox.getY());
            boolean canDown = (newHitBox.getY() <= hitBox.getY());
            boolean canLeft = (newHitBox.getX() <= hitBox.getX());
            boolean canRight = (newHitBox.getX() >= hitBox.getX());
            hitBox = newHitBox;
            float centerX = hitBox.x + hitBox.width/2;
            float centerY = hitBox.y + hitBox.height/2;
            float center2X = rect.getX() + rect.getWidth()/2;
            float center2Y = rect.getY() + rect.getHeight()/2;

            float diffX=0, diffY=0;


            if (rect.getX() > centerX && rect.getX() < centerX+hitBox.getWidth()/2) {
                if (rightSide && oldX > rect.x) {
                    diffX =  hitBox.x+hitBox.width-rect.x;
                }
            } else if (rect.getX()+rect.getWidth() < centerX && rect.getX()+rect.getWidth() > hitBox.getX()) {
                if (leftSide && oldX < rect.x) {
                    diffX = hitBox.x-rect.width - rect.x;
                }
            } if (rect.getY() > centerY && rect.getY() < centerY+hitBox.getHeight()/2) {
                if (upSide && oldY > rect.y) {
                    diffY = hitBox.y+hitBox.height - rect.y;
                }
            } else if (rect.getY()+rect.getHeight() < centerY && rect.getY()+rect.getHeight() > hitBox.getY()) {
                if (downSide && oldY < rect.y) {
                    diffY = hitBox.y-rect.height - rect.y;
                }
            }

            if (movable && objectIsMovable) {
                if (diffX != 0 && diffY != 0) {
                    if (Math.abs(diffX) < Math.abs(diffY)) {
                        rect.x += diffX/2;
                        hitBox.x -= diffX/2;
                    } else {
                        rect.y += diffY/2;
                        hitBox.y -= diffY/2;

                        //hitBox.x -= diffX/10;
                    }
                } else {
                    rect.x += diffX/2;
                    hitBox.x -= diffX/2;
                    rect.y += diffY/2;
                    hitBox.y -= diffY/2;
                    if (diffY != 0 && (downSide || upSide) && !platformMode){
                        if (rightSide) {
                            if (centerX < center2X && hitBox.x + hitBox.width - rect.x < 8) {
                                rect.x += (8 - (hitBox.x + hitBox.width - rect.x)) / 10;
                                hitBox.x -= (8 - (hitBox.x + hitBox.width - rect.x)) / 10;
                            } else if (oldX != rect.x && objectIsPlayer) {
                                hitBox.x += (rect.x-oldX)/22;
                            }
                        }
                        if (leftSide) {
                            if (centerX > center2X && rect.x + rect.width - hitBox.x < 8) {
                                rect.x -= (8 - (rect.x + rect.width - hitBox.x)) / 10;
                                hitBox.x += (8 - (rect.x + rect.width - hitBox.x)) / 10;
                            }else if (oldX != rect.x && objectIsPlayer) {
                                hitBox.x += (rect.x-oldX)/22;
                            }
                        }
                    } else if (diffX != 0 && (leftSide || rightSide) && !platformMode) {
                        if (upSide) {
                            if (centerY < center2Y && hitBox.y+hitBox.height-rect.y < 5) {
                                rect.y += (5-(hitBox.y+hitBox.height-rect.y)) / 10;
                                hitBox.y -= (5-(hitBox.y+hitBox.height-rect.y)) / 10;
                            }else if (oldY != rect.y && objectIsPlayer) {
                                hitBox.y += (rect.y-oldY)/10;
                            }
                        }
                        if (downSide) {
                            if (centerY > center2Y && rect.y + rect.height - hitBox.y < 5) {
                                rect.y -= (5 - (rect.y + rect.height - hitBox.y)) / 10;
                                hitBox.y += (5 - (rect.y + rect.height - hitBox.y)) / 10;
                            } else if (oldY != rect.y && objectIsPlayer) {
                                hitBox.y += (rect.y - oldY) / 10;
                            }
                        }
                    }
                }
                /*if (diffX != 0 && diffY != 0) {
                    if (Math.abs(diffX) < Math.abs(diffY)) hitBox.x -= diffX;
                    else hitBox.y -= diffY;
                } else {
                    hitBox.x -= diffX;
                    hitBox.y -= diffY;
                }*/
            } else if (objectIsMovable) {
                //System.out.println(diffX + " " + diffY + " " + rect.x + " " + (hitBox.x+hitBox.width));
                if (diffX != 0 && diffY != 0) {
                    if (Math.abs(diffX) < Math.abs(diffY)) {
                        rect.x += diffX;
                    } else {
                        rect.y += diffY;
                    }
                } else {
                    rect.x += diffX;
                    rect.y += diffY;
                    //if (objectIsPlayer) {
                    if (!platformMode) {
                        if (diffY != 0 && (downSide || upSide)){
                            if (rightSide && centerX < center2X && hitBox.x + hitBox.width - rect.x < 8) {
                                rect.x += (8 - (hitBox.x + hitBox.width - rect.x)) / 5;
                            } else if (leftSide && centerX > center2X && rect.x + rect.width - hitBox.x < 8) {
                                rect.x -= (8 - (rect.x + rect.width - hitBox.x)) / 5;
                            }
                        } else if (diffX != 0 && (leftSide || rightSide)) {
                            if (upSide && centerY < center2Y && hitBox.y+hitBox.height-rect.y < 5) {
                                rect.y += (5-(hitBox.y+hitBox.height-rect.y)) / 5;
                            } else if (downSide && centerY > center2Y && rect.y+rect.height-hitBox.y < 5) {
                                rect.y -= (5-(rect.y+rect.height-hitBox.y)) / 5;
                            }
                        }
                    }
                    //}
                }
            }
            if (/*objectIsMovable && */(diffX == 0 || diffY == 0)) {

            }
        }
        //this.oldX = x;
        //this.oldY = y;
        /*if (platformMode && Math.abs(hitBox.y - oldY2) < 0.3f && pSpeed == 0) {
            hitBox.y = oldY2;
        }*/
        x = hitBox.x;
        y = hitBox.y;
        return rect;
    }

    @Override
    public void dropShadow(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight, Texture shadow) {
        super.initialiseIfNeeded();
        if (shadow != null) {
            batch.draw(shadow, offsetX + hitBox.getX() + hitBox.getWidth() / 2 - shadow.getWidth() / 2, offsetY - hitBox.getY() + shadow.getHeight() / 2, shadow.getWidth(), shadow.getHeight());
        }
    }

    /*public void checkFall(Rectangle rect) {
        if (rect.contains(hitBox)) {
            zSpeed = 1;
        }
    }*/

    public void fall() {
        if (falling) {
            z += zSpeed;
            zSpeed += 0.3f;
        }
    }

    public void platformFall() {
        hitBox.y += pSpeed/10;
        pSpeed += 2;
        if (pSpeed > 20) {
            pSpeed = 20;
        }
    }

    @Override
    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight) {
        oldX = x;
        oldY = y;
        x = hitBox.x;
        y = hitBox.y;

        /*if (Math.abs(hitBox.x-oldX)>0.4f) {
            graphicX = x;
        }*/

        super.initialiseIfNeeded();
        if (tex != null) {
            batch.draw(tex, offsetX+hitBox.x+hitBox.getWidth()/2-tex.getWidth()/2, offsetY - hitBox.y-floorHeight-z, tex.getWidth(), tex.getHeight());
        } else {
            batch.draw(texDef, offsetX+hitBox.x+hitBox.getWidth()/2-texDef.getWidth()/2, offsetY - hitBox.y-floorHeight-z, texDef.getWidth(), texDef.getHeight());
        }
    }
}
