package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

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

    public Rectangle pushOutSolidObjects(HittableEntity he, Area area, float oldX, float oldY) {

        type = 1;
        boolean overlapX = false, overlapY = false;
        boolean platformMode = area.platformMode;
       //he.deltaX = he.hitBox.x;
        //he.deltaY = he.hitBox.y;
        Rectangle rect = he.getRect();
        float oldYY = he.hitBox.y;
        Rectangle oldRect = new Rectangle(he.hitBox);
        Rectangle oldThisRect = new Rectangle(hitBox);
        boolean objectIsPlayer = (he.getClass() == Player.class);
        if (rect.x+rect.width > hitBox.x+0.2f && rect.x < hitBox.x+hitBox.width-0.2f) {
            overlapX = true;
        }
        /*if (rect.width == 64 && !movable && rect.width == 64) {
            float a1  = rect.x+rect.width;
            float a2  = hitBox.x+0.2f;
            float a3  = rect.x;
            float a4  = hitBox.x+hitBox.width-0.2f;
            boolean a5  = rect.x+rect.width > hitBox.x+0.2f;
            boolean a6  = rect.x < hitBox.x+hitBox.width-0.2f;
            overlapX = false;
        }*/

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
        /*if (platformMode && deadEndObjectDown != null && deadEndObjectDown.movable) {
            if (!deadEndObjectDown.canDown) {
                hitBox.x = deadEndObjectX + platformOffset;
            }
        }*/
        /*if ((deadEndObject == null || (deadEndObjectX != deadEndObject.hitBox.x || deadEndObjectY != deadEndObject.hitBox.y))) {
            deadEndObjectX = 0;
            makeFree();
        }*/ /*else if (deadEndObject != null && checksum != 0 && (deadEndObjectX != 0 && deadEndObjectY != 0)) {
            if (deadEndObject.canLeft && checksum == 1 || deadEndObject.canRight && checksum == 2 ||
                deadEndObject.canUp && checksum == 3 || deadEndObject.canDown && checksum == 4) {
                makeFree();
            }
        }*/

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
                if (upSide && oldY > rect.y) {
                    diffY = hitBox.y+hitBox.height - rect.y;
                }
            } else if (rect.getY()+rect.getHeight() < centerY && rect.getY()+rect.getHeight() > hitBox.getY()) {
                if (downSide && oldY < rect.y) {
                    diffY = hitBox.y-rect.height - rect.y;
                }
            }

            boolean canMoveHor = ((diffX < 0 && canLeft) || (diffX > 0 && canRight)||diffX==0);
            boolean canMoveVer = ((diffY < 0 && canDown) || (diffY > 0 && canUp)||diffY==0);



            /*if (this.getClass() != Player.class && platformMode && Math.abs(diffY) > 3) {
                diffY = 0;
            }*/
            if (Math.abs(diffX) > 5) {
                diffX = 0;
            }

            if (Math.abs(diffY) > 6) {
                diffY = 0;
            }
            //}

            if (objectIsPlayer) {
                /*if (((Player)he).pushCount >= 0) {
                    ((Player)he).pushCount = 3;
                } else {*/
                if (platformMode && isPlatform && Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    return rect;
                }

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
                                rect.x += 0.3f;
                                hitBox.x -= 0.3f;
                                canLeft = true;
                            } else if (oldX != rect.x && objectIsPlayer && !platformMode) {
                                hitBox.x += (rect.x-oldX)/22;
                            }
                        }
                        if (leftSide) {
                            if (centerX >= center2X && rect.x + rect.width - hitBox.x <= SLIP_EDGE_X && he.canLeft) {
                                rect.x -= 0.3f;
                                hitBox.x += 0.3f;
                                canRight = true;
                            }else if (oldX != rect.x && objectIsPlayer && !platformMode) {
                                hitBox.x += (rect.x-oldX)/22;
                            }
                        }
                    } else if (diffX != 0 && (leftSide || rightSide) && canMoveVer) {
                        if (upSide) {
                            if (centerY <= center2Y && hitBox.y+hitBox.height-rect.y <= SLIP_EDGE_Y) {
                                rect.y += 0.2f;
                                hitBox.y -= 0.2f;
                            }else if (oldY != rect.y && objectIsPlayer && !platformMode && (canUp && (rect.y-oldY) < 0 || canDown && (rect.y-oldY) > 0)) {
                                hitBox.y += (rect.y-oldY)/15;
                            }
                        }
                        if (downSide) {
                            if (centerY >= center2Y && rect.y + rect.height - hitBox.y <= SLIP_EDGE_Y) {
                                rect.y -= 0.2f;
                                hitBox.y += 0.2f;
                            } else if (oldY != rect.y && objectIsPlayer && !platformMode && (canUp && (rect.y-oldY) < 0 || canDown && (rect.y-oldY) > 0)) {
                                hitBox.y += (rect.y - oldY) / 15;
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
            }
            if (he.movable && (!movable || (!canMoveHor || !canMoveVer))) {
                if (rect.width == 64 && diffX != 0) {
                    System.out.println();
                }
                //System.out.println(diffX + " " + diffY + " " + rect.x + " " + (hitBox.x+hitBox.width));
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
                        if (platformMode && isPlatform && objectIsPlayer && ((Player)he).pSpeed < 0) {
                            return rect;
                        }
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
                                he.hitBox.x += 0.4f;
                                if (objectIsPlayer) {
                                    ((Player)he).speedX+=4;
                                }
                                he.canUp = true;
                                he.canDown = true;

                            } else if (leftSide && centerX >= center2X && rect.x+rect.width-hitBox.x <= SLIP_EDGE_X) {
                                he.hitBox.x -= 0.4f;
                                if (objectIsPlayer) {
                                    ((Player)he).speedX-=4;
                                }
                                he.canUp = true;
                                he.canDown = true;

                            }
                            /*if (rightSide && centerX < center2X && hitBox.x + hitBox.width - rect.x < hitBox.getWidth()/4) {
                                if (rect.x + (hitBox.getWidth()/4 - (hitBox.x + hitBox.width - rect.x)) / 5 < hitBox.x+hitBox.width) rect.x += (hitBox.getWidth()/4 - (hitBox.x + hitBox.width - rect.x)) / 20;
                                else rect.x = hitBox.x + hitBox.width;
                            } else if (leftSide && centerX > center2X && rect.x + rect.width - hitBox.x < hitBox.getWidth()/4) {
                                if (rect.x - (hitBox.getWidth()/4 - (rect.x + rect.width - hitBox.x)) / 5 + rect.width > hitBox.x) rect.x -= (hitBox.getWidth()/4 - (rect.x + rect.width - hitBox.x)) / 20;
                                else rect.x = hitBox.x - rect.width;
                            }*/
                        } else if (diffX != 0 && (leftSide || rightSide) && !canMoveVer) {
                            if (upSide && centerY <= center2Y && hitBox.y+hitBox.height-rect.y <= SLIP_EDGE_Y) {
                                he.hitBox.y += 0.6f;
                                if (objectIsPlayer) {
                                    ((Player)he).speedY+=3;
                                }
                                he.canLeft = true;
                                he.canRight = true;
                            } else if (downSide && centerY >= center2Y && rect.y+rect.height-hitBox.y <= SLIP_EDGE_Y) {
                                if (!objectIsPlayer) rect.y -= (SLIP_EDGE_Y-(rect.y+rect.height-hitBox.y)) / 10;
                                he.hitBox.y -= 0.6f;
                                if (objectIsPlayer) {
                                    ((Player)he).speedY-=3;
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
                System.out.println();
            }
            //if (platformMode) {
                /*if ((!canLeft || !canRight)){
                    canDown = true;
                    canUp = true;
                }
                if ((!canUp || !canDown)){
                    canLeft = true;
                    canRight = true;
                }*/
            //}

        }
        x = hitBox.x;
        y = hitBox.y;
        //this.oldX = x;
        //this.oldY = y;
        /*if (platformMode && Math.abs(hitBox.y - oldY2) < 0.3f && pSpeed == 0) {
            hitBox.y = oldY2;
        }*/
        if (Math.abs(oldYY-rect.y)>3) {
            System.out.println();
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
                pSpeed += 4;
            }
            if (!inWater && !inGoo) {
                if (pSpeed > 40) {
                    pSpeed = 40;
                }
            } else if (inGoo) {
                if (pSpeed > 10) {
                    pSpeed = 10;
                }
            } else {
                if (pSpeed > 20) {
                    pSpeed = 20;
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
    public void draw(SpriteBatch batch, float offsetX, float offsetY, int tileWidth, int tileHeight, boolean platformMode) {
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
        float anglee = 0;
        if (angle == 1) {
            anglee = -90;
        } else if (angle == 2) {
            anglee = -180;
        } else if (angle == 3) {
            anglee = -270;
        }
        if (tex != null) {
            if (!platformMode) batch.draw(tex, offsetX+graphicX+hitBox.getWidth()/2-tex.getWidth()/2, offsetY - graphicY-floorHeight-z, tex.getWidth(), tex.getHeight());
            else {
                //batch.draw(tex, offsetX+hitBox.x, offsetY - hitBox.y-2-hitBox.height+tileHeight, tex.getWidth(), tex.getHeight());
                if (angle%2 == 1) {
                    batch.draw(tex, offsetX+hitBox.x+tex.getWidth()/2, offsetY - hitBox.y-2-hitBox.height+tileHeight-tex.getWidth()/2, tex.getWidth()/2, tex.getHeight()/2, tex.getWidth()*scale, tex.getHeight()*scale, 1.0f, 1.0f, anglee, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
                } else {
                    batch.draw(tex, offsetX+hitBox.x, offsetY - hitBox.y-2-hitBox.height+tileHeight, tex.getWidth()/2, tex.getHeight()/2, tex.getWidth()*scale, tex.getHeight()*scale, 1.0f, 1.0f, anglee, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
                }
            }
        } else if (texR != null) {
            if (!platformMode) batch.draw(texR, offsetX+graphicX+hitBox.getWidth()/2-texR.getRegionWidth()/2, offsetY - graphicY-floorHeight-z, texR.getRegionWidth(), texR.getRegionHeight());
            else {
                batch.draw(texR, offsetX+hitBox.x, offsetY - hitBox.y-2-hitBox.height+tileHeight, texR.getRegionWidth()/2, texR.getRegionHeight()/2,
                        texR.getRegionWidth()*scale, texR.getRegionHeight()*scale, 1.0f, 1.0f, anglee, false);
                //batch.draw(texR, offsetX+hitBox.x, offsetY - hitBox.y-2-hitBox.height+tileHeight, texR.getRegionWidth(), texR.getRegionHeight());
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
