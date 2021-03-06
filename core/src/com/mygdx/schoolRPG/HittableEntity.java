package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.schoolRPG.DeathZone.MyPoint2f;
import com.mygdx.schoolRPG.tools.AnimationSequence;

import java.util.ArrayList;

/**
 * Created by Kraft on 27.12.2014.
 */
public class HittableEntity extends Entity {

    final int SLIP_EDGE_X = 4;
    final int SLIP_EDGE_Y = 4;

    Rectangle hitBox;
    boolean movable;
    boolean leftSide, rightSide, downSide, upSide;
    float oldX, oldY, graphicX, graphicY, oldY2;
    boolean deltaX=false, deltaY=false;
    int pSpeed = 0;
    boolean canUp = true, canDown = true, canLeft = true, canRight = true;
    float deadEndX=0, deadEndY=0;
    float deadEndObjectX = 0, deadEndObjectY = 0;
    HittableEntity deadEndObjectLeft = null, deadEndObjectRight = null, deadEndObjectUp = null, deadEndObjectDown = null;
    boolean pusher = false;
    int checksum = 0;
    float platformOffset = 0;
    boolean isPlatform = false;
    MyPoint2f point1, point2;

    public HittableEntity(AssetManager assets, String texPath, float x, float y, float width, float height, float floorHeight, boolean movable, int angle) {
        super(assets, texPath, x, y, height-1, floorHeight, angle);
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

    public HittableEntity(AssetManager assets, TextureRegion tex, float x, float y, float width, float height, float floorHeight, boolean movable, int angle) {
        super(assets, tex, x, y, height-1, floorHeight, angle);
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

    public HittableEntity(AssetManager assets, AnimationSequence anim, float x, float y, float width, float height, float floorHeight, boolean movable, int angle) {
        super(assets, anim, x, y, height-1, floorHeight, angle);
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

    public HittableEntity(AssetManager assets, Texture tex, float x, float y, float width, float height, float floorHeight, boolean movable, int angle) {
        super(assets, tex, x, y, height-1, floorHeight, angle);
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

    public HittableEntity(HittableEntity he) {
        super(he.assets, he.tex, he.x, he.y, he.hitBox.height-1, he.floorHeight, he.angle);
        this.floorHeight = he.floorHeight;
        hitBox = new Rectangle(he.x, he.y, he.hitBox.width, he.hitBox.height);
        this.movable = he.movable;
        leftSide = rightSide = downSide = upSide = true;
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

    public void fall() {
        if (falling) {
            z += zSpeed;
            zSpeed += 0.3f;
        }
    }

    private float clamp(float xx, float lower, float upper) {
        return Math.max(lower, Math.min(upper, xx));
    }

    private MyPoint2f getClosestRectanglePoint(float x, float y) {
        float l = hitBox.x;
        float t = hitBox.y - hitBox.height;
        float w = hitBox.width;
        float h = hitBox.height;
        float r = l + w;
        float b = hitBox.y;
        float xxx = clamp(x, l, r);
        float yyy = clamp(y, t, b);
        float dl = Math.abs(xxx-l);
        float dr = Math.abs(xxx-r);
        float db = Math.abs(yyy-b);
        float dt = Math.abs(yyy-t);
        float m = Math.min(Math.min(dl, dr), Math.min(dt, db));
        if (m == dt) {
            //sr.line(l * 2 - 13, 720 - 2 * t, r * 2 - 13, 720 - 2 * t);

            return new MyPoint2f(xxx, t);
        }
        if (m == db) {
            //sr.line(l * 2 - 13, 720 - 2 * b, r * 2 - 13, 720 - 2 * b);
            return new MyPoint2f(xxx, b);
        }
        if (m == dl) {
            //sr.line(l * 2 - 13, 720 - 2 * t, l * 2 - 13, 720 - 2 * b);
            return new MyPoint2f(l, yyy);
        }
        //sr.line(r * 2 - 13, 720 - 2 * t, r * 2 - 13, 720 - 2 * b);
        return new MyPoint2f(r, yyy);
    }

    public Rectangle pushOutSolidObjects(HittableEntity he) {
        if (!collidable) return he.getRect();
      /* ShapeRenderer sr = new ShapeRenderer();
        sr.setColor(1, 0, 0, 1);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(1, 0, 0, 1);*/
        Rectangle rect = he.getRect();
        if (isPlatform) {
            if (he.pSpeed < 0) {
                return rect;
            } else if (he.getClass() == Player.class && ((Player)he).movingConfiguration.movingDown > 0) {
                if (rect.overlaps(hitBox)) {
                    //((Player)he).falling = true;
                    ((Player)he).jumping = true;
                }
                return rect;
            }
        }
        if (he.getClass() == Player.class) {
            hitBox.height -= 4;
        }
        point1 = getClosestRectanglePoint(rect.x + rect.width/2, rect.y-rect.height/2);
        if (he.getClass() == Player.class) {
            hitBox.height += 4;
        }
        //point1.y += rect.height/2-3;
        point2 = he.getClosestRectanglePoint(point1.x, point1.y);
        //point1.y += rect.height/2;

        //sr.end();
        float dist1 = (float)Math.hypot(hitBox.x + hitBox.width/2.0f - point1.x, hitBox.y - hitBox.height/2.0f - point1.y);
        float dist2 = (float)Math.hypot(hitBox.x + hitBox.width/2.0f - point2.x, hitBox.y - hitBox.height/2.0f - point2.y);
        if (dist2 < dist1) {
            float diffX = point1.x - point2.x;
            float diffY = point1.y - point2.y;
            if (isPlatform) diffX = 0;
            if (movable && !he.movable) {
                hitBox.x -= diffX;
                hitBox.y -= diffY;
            } else if (!movable && he.movable) {
                rect.x += diffX;
                rect.y += diffY;
            } else if (movable && he.movable) {
                hitBox.x -= diffX/2.0f;
                hitBox.y -= diffY/2.0f;
                rect.x += diffX/2.0f;
                rect.y += diffY/2.0f;
            }

        }

        /*if (he.getClass() == NPC.class) {
            he.x = he.hitBox.x;
            he.y = he.hitBox.y;
            he.graphicX = he.hitBox.x;
            he.graphicY = he.hitBox.y;
        }*/

        return rect;
    }

    public Rectangle pushOutSolidObjects(HittableEntity he, Area area, float oldX, float oldY) {
        if (!collidable) return he.getRect();
        if (this == he) return hitBox;
        type = 1;
        boolean overlapX = false, overlapY = false;
        boolean platformMode = area.platformMode;
        Rectangle rect = he.getRect();
        float oldYY = he.hitBox.y;
        Rectangle oldRect = new Rectangle(he.hitBox);
        Rectangle oldThisRect = new Rectangle(hitBox);
        boolean objectIsPlayer = (he.getClass() == Player.class);
        if (rect.x+rect.width > hitBox.x+0.2f && rect.x < hitBox.x+hitBox.width-0.2f) {
            overlapX = true;
        }
        if (rect.y+rect.height > hitBox.y+0.3f && rect.y < hitBox.y+hitBox.height-0.3f) {
            overlapY = true;
        }



        if (deadEndObjectLeft != null || deadEndObjectRight != null ||
                deadEndObjectUp != null || deadEndObjectDown != null) {
            if (deadEndObjectLeft != null && deadEndObjectLeft.movable) {
                if (deadEndObjectLeft.canLeft || deadEndObjectLeft.falling) {
                    canLeft = true;
                    deadEndObjectLeft = null;
                }
            }
            if (deadEndObjectRight != null && deadEndObjectRight.movable) {
                if (deadEndObjectRight.canRight || deadEndObjectRight.falling) {
                    canRight = true;
                    deadEndObjectRight = null;
                }
            }
            if (deadEndObjectUp != null && deadEndObjectUp.movable) {
                if (deadEndObjectUp.canUp || deadEndObjectUp.falling) {
                    canUp = true;
                    deadEndObjectUp = null;
                }
            }
            if (deadEndObjectDown != null && deadEndObjectDown.movable) {
                if (deadEndObjectDown.canDown || deadEndObjectDown.falling) {
                    canDown = true;
                    deadEndObjectDown = null;
                } else if (hitBox.x + hitBox.width - deadEndObjectDown.hitBox.x <= hitBox.getWidth()/4
                        || deadEndObjectDown.hitBox.x+deadEndObjectDown.hitBox.width-hitBox.x <= hitBox.getWidth()/4) {
                    canDown = true;
                }
            }
        }

        float diffX=0, diffY=0;
        if (overlapX && overlapY && hitBox.overlaps(rect) && hitBox != rect) {
            if (deadEndX != hitBox.x) {
                canDown = true;
                canUp = true;
                //deadEndY = 0;
            }
            if (deadEndY != hitBox.y) {
                canLeft = true;
                canRight = true;
                //deadEndX = 0;
            }


            oldY2 = hitBox.y;
            if (!movable && !he.movable) {
                //he.deltaX = 0;
                //he.deltaY = 0;
                return rect;
            }
            Rectangle newHitBox = hitBox;
            hitBox = newHitBox;
            float centerX = hitBox.x + hitBox.width/2;
            float centerY = hitBox.y + hitBox.height/2;
            float center2X = rect.getX() + rect.getWidth()/2;
            float center2Y = rect.getY() + rect.getHeight()/2;




            if (rect.getX() > centerX && rect.getX() < centerX+hitBox.getWidth()/2) {
                if (rightSide && oldX > rect.x) {
                    diffX =  hitBox.x+hitBox.width-rect.x;
                }
            } else if (rect.getX()+rect.getWidth() < centerX && rect.getX()+rect.getWidth() > hitBox.getX()) {
                if (leftSide && oldX < rect.x) {
                    diffX = hitBox.x-rect.width - rect.x;
                }
            } if (rect.getY() > centerY && rect.getY() < centerY+hitBox.getHeight()/2) {
                if (upSide && oldY >= rect.y) {
                    diffY = hitBox.y+hitBox.height - rect.y;
                }
            } else if (rect.getY()+rect.getHeight() < centerY && rect.getY()+rect.getHeight() > hitBox.getY()) {
                if (downSide && oldY <= rect.y) {
                    diffY = hitBox.y-rect.height - rect.y;
                }
            }

            boolean canMoveHor = ((diffX < 0 && canLeft) || (diffX > 0 && canRight)||diffX==0);
            boolean canMoveVer = ((diffY < 0 && canDown) || (diffY > 0 && canUp)||diffY==0);


            if (Math.abs(diffX) > 5) {
                diffX = 0;
            }

            if (Math.abs(diffY) > 6) {
                diffY = 0;
            }
            if (objectIsPlayer) {


                if (diffX < 0) {
                    ((Player)he).pushRight = true;
                    ((Player)he).pushLeft = false;
                } else if (diffX > 0) {
                    ((Player)he).pushRight = false;
                    ((Player)he).pushLeft = true;
                } else {
                    ((Player)he).pushRight = false;
                    ((Player)he).pushLeft = false;
                }
                if (diffY > 0) {
                    ((Player)he).pushUp = true;
                    ((Player)he).pushDown = false;
                } else if (diffY < 0) {
                    ((Player)he).pushUp = false;
                    ((Player)he).pushDown = true;
                } else {
                    ((Player)he).pushUp = false;
                    ((Player)he).pushDown = false;
                }
                if (movable && ((Player)he).pushCount > -1) {
                    ((Player)he).pushCount=3;
                } else {
                    ((Player)he).pushCount++;
                }
                //}

            }

            if (movable && he.movable && (canMoveHor || canMoveVer)) {
                if (diffX != 0 && diffY != 0) {
                    if (Math.abs(diffX) < Math.abs(diffY) && canMoveHor) {
                        rect.x += diffX/2;
                        hitBox.x -= diffX/2;
                    } else if (canMoveVer) {
                        rect.y += diffY/2;
                        hitBox.y -= diffY/2;

                        //hitBox.x -= diffX/10;
                    }
                } else {
                    if (canMoveHor) {
                        if (he.getClass() != Player.class) {
                            if (!canRight && diffX > 0) {
                                rect.x += diffX;
                                he.canLeft = false;
                                he.canRight = true;
                                he.deadEndY = rect.y;
                            } else if (!canLeft && diffX < 0) {
                                rect.x += diffX;
                                he.canRight = false;
                                he.canLeft = true;
                                he.deadEndY = rect.y;
                            }
                            else {
                                rect.x += diffX/2;
                                hitBox.x -= diffX/2;
                            }
                        } else {
                            rect.x += diffX/2;
                            hitBox.x -= diffX/2;
                        }
                    }
                    if (canMoveVer) {
                        rect.y += diffY/2;
                        hitBox.y -= diffY/2;
                    }
                    if (diffY != 0 && (downSide || upSide) && canMoveHor){
                        if (rightSide) {
                            if (centerX <= center2X && hitBox.x + hitBox.width - rect.x <= SLIP_EDGE_X && he.canRight) {
                                if (objectIsPlayer) {
                                    if (((Player)he).speedX >= 0) ((Player)he).speedX += 3;
                                } else {
                                    rect.x += 0.3f;
                                }
                                hitBox.x -= 0.3f;
                                canLeft = true;
                            } else if (oldX != rect.x && objectIsPlayer && !platformMode) {
                                hitBox.x += (rect.x-oldX)/22;
                            }
                        }
                        if (leftSide) {
                            if (centerX >= center2X && rect.x + rect.width - hitBox.x <= SLIP_EDGE_X && he.canLeft) {
                                if (objectIsPlayer) {
                                    if (((Player)he).speedX <= 0) ((Player)he).speedX -= 3;
                                } else {
                                    rect.x -= 0.3f;
                                }
                                hitBox.x += 0.3f;
                                canRight = true;
                            }else if (oldX != rect.x && objectIsPlayer && !platformMode) {
                                hitBox.x += (rect.x-oldX)/22;
                            }
                        }
                    } else if (diffX != 0 && (leftSide || rightSide) && canMoveVer) {
                        if (upSide) {
                            if (centerY <= center2Y && hitBox.y+hitBox.height-rect.y <= SLIP_EDGE_Y) {
                                if (objectIsPlayer) {
                                    if (((Player)he).speedY >= 0) ((Player)he).speedY += 2;
                                } else {
                                    rect.y += 0.2f;
                                }
                                hitBox.y -= 0.2f;
                            }else if (oldY != rect.y && objectIsPlayer && !platformMode && (canUp && (rect.y-oldY) < 0 || canDown && (rect.y-oldY) > 0)) {
                                hitBox.y += (rect.y-oldY)/15;
                            }
                        }
                        if (downSide) {
                            if (centerY >= center2Y && rect.y + rect.height - hitBox.y <= SLIP_EDGE_Y) {
                                if (objectIsPlayer) {
                                    if (((Player)he).speedY <= 0) ((Player)he).speedY -= 2;
                                } else {
                                    rect.y -= 0.2f;
                                }
                                hitBox.y += 0.2f;
                            } else if (oldY != rect.y && objectIsPlayer && !platformMode && (canUp && (rect.y-oldY) < 0 || canDown && (rect.y-oldY) > 0)) {
                                hitBox.y += (rect.y - oldY) / 15;
                            }
                        }
                    }
                }

            }
            if (he.movable && (!movable || (!canMoveHor || !canMoveVer))) {
                if (rect.width == 64 && diffX != 0) {
                    //System.out.println();
                }
                canMoveHor = movable&&((diffX < 0 && canLeft) || (diffX > 0 && canRight)||diffX==0);
                canMoveVer = movable&&((diffY < 0 && canDown) || (diffY > 0 && canUp)||diffY==0);
                //if (this.getClass() != Player.class) {
                    //if (!platformMode) {
                        if (diffX < 0 && (!canLeft||!movable)) {
                            he.canLeft = false;
                            he.canDown = true;
                            he.canUp = true;
                            he.canRight = true;
                            he.deadEndY = rect.y;
                            setCheck(he, 1);
                            if (objectIsPlayer) {
                                ((Player)he).speedX=0;
                            }
                            //he.deadEndObject.canLeft = false;
                        }
                        else if (diffX > 0 && (!canRight||!movable)) {
                            he.canRight = false;
                            he.canDown = true;
                            he.canUp = true;
                            he.canLeft = true;
                            he.deadEndY = rect.y;
                            setCheck(he, 2);
                            if (objectIsPlayer) {
                                ((Player)he).speedX=0;
                            }
                            //he.deadEndObject.canRight = false;
                        }
                    //}
                    if (diffY > 0) {
                        he.canUp = false;
                        he.deadEndX = rect.x;
                        if (objectIsPlayer && !platformMode) {
                            ((Player)he).speedY=0;
                        }
                        setCheck(he, 3);
                    }
                    else if (diffY < 0) {

                        he.canDown = false;
                        he.deadEndX = rect.x;
                        if (objectIsPlayer && !platformMode) {
                            ((Player)he).speedY=0;
                        }
                        setCheck(he, 4);
                        //he.deadEndObject.canDown = false;
                    }
                //}
                if (diffX != 0 && diffY != 0) {
                    if (Math.abs(diffX) < Math.abs(diffY) && !canMoveHor) {
                        if (objectIsPlayer) {
                            ((Player)he).speedX=0;
                        }
                        rect.x += diffX;
                    } else if (!canMoveVer) {
                        if (objectIsPlayer && !platformMode) {
                            ((Player)he).speedY=0;
                        }
                        if ((!objectIsPlayer || Math.abs(diffY) <= 3) || !platformMode) {
                            rect.y += diffY;
                        } else {
                            rect.x += diffX;
                        }
                    }
                } else if ((diffX != 0 || diffY != 0) && (!canMoveHor || !canMoveVer)) {
                    if (!canMoveHor && diffX != 0) {
                        if (objectIsPlayer) {
                            ((Player)he).speedX=0;
                        }
                        rect.x += diffX;
                    }
                    if (!canMoveVer && diffY != 0) {
                        if (objectIsPlayer && !platformMode) {
                            ((Player)he).speedY=0;
                        }
                        rect.y += diffY;
                    }
                    //if (objectIsPlayer) {
                    if (!platformMode || !objectIsPlayer) {
                        if (diffY != 0 && (downSide || upSide) && !canMoveHor){
                            if (rightSide && centerX <= center2X && hitBox.x+hitBox.width-rect.x <= SLIP_EDGE_X) {
                                if (objectIsPlayer) {
                                    if (((Player)he).speedX >= 0) ((Player)he).speedX+=4;
                                } else {
                                    he.hitBox.x += 0.4f;
                                }
                                he.canUp = true;
                                he.canDown = true;

                            } else if (leftSide && centerX >= center2X && rect.x+rect.width-hitBox.x <= SLIP_EDGE_X) {
                                if (objectIsPlayer) {
                                    if (((Player)he).speedX <= 0) ((Player)he).speedX-=4;
                                } else {
                                    he.hitBox.x -= 0.4f;
                                }
                                he.canUp = true;
                                he.canDown = true;

                            }
                        } else if (diffX != 0 && (leftSide || rightSide) && !canMoveVer) {
                            if (upSide && centerY <= center2Y && hitBox.y+hitBox.height-rect.y <= SLIP_EDGE_Y) {
                                if (objectIsPlayer) {
                                    if (((Player)he).speedY == 0) ((Player)he).speedY+=3;
                                } else {
                                    he.hitBox.y += 0.6f;
                                }
                                he.canLeft = true;
                                he.canRight = true;
                            } else if (downSide && centerY >= center2Y && rect.y+rect.height-hitBox.y <= SLIP_EDGE_Y) {
                                if (!objectIsPlayer) rect.y -= (SLIP_EDGE_Y-(rect.y+rect.height-hitBox.y)) / 10;
                                if (objectIsPlayer) {
                                    if (((Player)he).speedY == 0) ((Player)he).speedY-=3;
                                } else {
                                    he.hitBox.y -= 0.6f;
                                }
                                he.canLeft = true;
                                he.canRight = true;
                            }
                        }
                    }
                    //}
                }
            }

            deltaX = deltaX || (hitBox.x - oldThisRect.x) != 0;
            deltaY = deltaY || (hitBox.y - oldThisRect.y) != 0;
            he.deltaX = deltaX || (rect.x - oldRect.x) != 0;
            he.deltaY = deltaY || (rect.y - oldRect.y) != 0;
            if (he.deltaX != false) {
                //System.out.println();
            }

        }
        x = hitBox.x;
        y = hitBox.y;
        //this.oldX = x;
        //this.oldY = y;
        /*if (he.getClass() == NPC.class && this.getClass() == NPC.class) {
            he.x = he.hitBox.x;
            he.y = he.hitBox.y;
            he.graphicX = he.hitBox.x;
            he.graphicY = he.hitBox.y;
        }*/
        if (Math.abs(oldYY-rect.y)>3) {
            //System.out.println();
        }
        return rect;
    }

    private void makeFree() {
        canDown = true;
        canRight = true;
        canLeft = true;
        canUp = true;
    }

    private void setCheck(HittableEntity he, int dir) {
        he.deadEndObjectX = hitBox.x;
        he.deadEndObjectY = hitBox.y;
        if (dir == 1) he.deadEndObjectLeft = this;
        else if (dir == 2) he.deadEndObjectRight = this;
        else if (dir == 3) he.deadEndObjectUp = this;
        else if (dir == 4) {
            he.deadEndObjectDown = this;
            //he.platformOffset = he.deadEndObjectDown.x -  hitBox.x;
        }
        checksum = dir;
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



    public void platformFall() {
        if (!movable) return;
        if (canDown || pSpeed < 0) {
            hitBox.y += pSpeed/10;
            if (pSpeed == 0) pSpeed += 1;
            else {
                pSpeed += 4;            }
            if (!inWater && !inGoo) {
                if (pSpeed > 40) {
                    pSpeed = 40;
                }
            } else if (inGoo) {
                if (pSpeed > 6) {
                    pSpeed = 6;
                }
            } else {
                if (pSpeed > 10) {
                    pSpeed = 10;
                }
            }
            canLeft = true;
            canRight = true;
        } else if (this.getClass() != Player.class) {
            pSpeed = 0;
        }
        //canDown = true;
    }

    @Override
    public float getPreviousY() {
        return hitBox.y - pSpeed/10;
    }

    @Override
    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight, boolean platformMode, boolean active, int activeX, int activeY) {
        oldX = x;
        oldY = y;
        x = hitBox.x;
        y = hitBox.y;
        if (!floor) {
            h = y+floorHeight;
        } else {
            h = 999999;
        }

        /*if (Math.abs(hitBox.x-oldX)>0.4f) {
            graphicX = x;
        }*/

        if (deltaX == false) {
            graphicX = (float)Math.round(hitBox.x);
        } else {
            graphicX = hitBox.x;
        }
        if (deltaY == false) {
            graphicY = (float)Math.round(hitBox.y);
        } else {
            graphicY = hitBox.y;
        }
        deltaX = false;
        deltaY = false;

       /* if (!canDown) batch.setColor(new Color(1, 0, 1, 0.25f));
        if (!canRight) batch.setColor(new Color(0, 1, 0, 0.25f));
        if (!canLeft) batch.setColor(new Color(0, 0, 1, 0.25f));*/

        super.initialiseIfNeeded();
        if (active) {
            this.active = assets.get("active.png");
            int height = 0;
            if (anim != null) {
                height = anim.getFirstFrame().getRegionHeight();
            } else if (tex != null) {
                height = tex.getHeight();
            } else if (texR != null) {
                height = texR.getRegionHeight();
            }
            batch.draw(this.active, offsetX + x + hitBox.getWidth()/2 - this.active.getWidth()/2 + activeX, offsetY - y + 3 + height - activeY);
        }

        float anglee = 0;
        if (angle == 1) {
            anglee = -90;
        } else if (angle == 2) {
            anglee = -180;
        } else if (angle == 3) {
            anglee = -270;
        }

        if (anim != null) {
            texR = anim.getCurrentFrame(false);
        }

        if (tex != null) {
            if (!platformMode) {
                batch.draw(tex, offsetX+graphicX+hitBox.getWidth()/2-tex.getWidth()/2, offsetY - graphicY-floorHeight-z, tex.getWidth(), tex.getHeight());
                TextureRegion drawTex2 = maybeDrawChar(batch, offsetX+graphicX+hitBox.getWidth()/2-tex.getWidth()/2, offsetY - graphicY-floorHeight-z);
                if (drawTex2 != null) batch.draw(drawTex2, offsetX+graphicX+hitBox.getWidth()/2-drawTex2.getRegionWidth()/2, offsetY - graphicY-floorHeight-z, drawTex2.getRegionWidth(), drawTex2.getRegionHeight());
            }
            else {
                //batch.draw(tex, offsetX+hitBox.x, offsetY - hitBox.y-2-hitBox.height+tileHeight, tex.getWidth(), tex.getHeight());
                if (angle%2 == 1) {
                    batch.draw(tex, offsetX+hitBox.x+tex.getWidth()/2, offsetY - hitBox.y-2-hitBox.height+tileHeight-tex.getWidth()/2, tex.getWidth()/2, tex.getHeight()/2, tex.getWidth()*scale, tex.getHeight()*scale, 1.0f, 1.0f, anglee, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
                } else {
                    batch.draw(tex, offsetX+hitBox.x, offsetY - hitBox.y-2-hitBox.height+tileHeight, tex.getWidth()/2, tex.getHeight()/2, tex.getWidth()*scale, tex.getHeight()*scale, 1.0f, 1.0f, anglee, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
                }
                //maybeDrawChar(batch, offsetX+hitBox.x, offsetY - hitBox.y-2-hitBox.height+tileHeight);
            }
        } else if (texR != null) {
            if (!platformMode) {
                batch.draw(texR, offsetX+graphicX+hitBox.getWidth()/2-texR.getRegionWidth()/2, offsetY - graphicY-floorHeight-z, texR.getRegionWidth(), texR.getRegionHeight());
                TextureRegion drawTex2 = maybeDrawChar(batch, offsetX+graphicX+hitBox.getWidth()/2-texR.getRegionWidth()/2, offsetY - graphicY-floorHeight-z);
                if (drawTex2 != null) batch.draw(drawTex2, offsetX+graphicX+hitBox.getWidth()/2-drawTex2.getRegionWidth()/2, offsetY - graphicY-floorHeight-z, drawTex2.getRegionWidth(), drawTex2.getRegionHeight());
            }
            else {
                batch.draw(texR, offsetX+hitBox.x, offsetY - hitBox.y-2-hitBox.height+tileHeight, texR.getRegionWidth()/2, texR.getRegionHeight()/2,
                        texR.getRegionWidth()*scale, texR.getRegionHeight()*scale, 1.0f, 1.0f, anglee, false);
            }
        }

        //batch.setColor(Color.WHITE);
    }

    /*@Override
    public Rectangle getTexRect() {
        Rectangle r = super.getTexRect();
        r.x += super.getTexRect().width/2;
        return r;
    }*/

}
