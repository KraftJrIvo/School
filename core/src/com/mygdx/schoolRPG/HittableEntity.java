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
    boolean deltaX=false, deltaY=false;
    float z = 0, zSpeed = 0;
    int pSpeed = 0;
    boolean falling;
    boolean canUp = true, canDown = true, canLeft = true, canRight = true;
    float deadEndX=0, deadEndY=0;

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
    public Rectangle invalidate(HittableEntity he, Area area, float oldX, float oldY) {

        boolean overlapX = false, overlapY = false;
        boolean platformMode = area.platformMode;
       //he.deltaX = he.hitBox.x;
        //he.deltaY = he.hitBox.y;
        Rectangle rect = he.getRect();
        Rectangle oldRect = new Rectangle(he.hitBox);
        boolean objectIsPlayer = (he.getClass() == Player.class);
        if (rect.x+rect.width > hitBox.x+0.2f && rect.x < hitBox.x+hitBox.width-0.2f) {
            overlapX = true;
        }

        if (rect.y+rect.height > hitBox.y+0.3f && rect.y < hitBox.y+hitBox.height-0.3f) {
            overlapY = true;
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
                        rect.x += diffX/2;
                        hitBox.x -= diffX/2;
                    }
                    if (canMoveVer) {
                        rect.y += diffY/2;
                        hitBox.y -= diffY/2;
                    }
                    if (diffY != 0 && (downSide || upSide) && (platformMode || objectIsPlayer) && canMoveHor){
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
                    } else if (diffX != 0 && (leftSide || rightSide) && (!platformMode || !objectIsPlayer) && canMoveVer) {
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
            }
            if (he.movable && (!movable || (!canMoveHor || !canMoveVer))) {
                //System.out.println(diffX + " " + diffY + " " + rect.x + " " + (hitBox.x+hitBox.width));
                canMoveHor = !(!movable || !canMoveHor);
                canMoveVer = !(!movable || !canMoveVer);
                if (diffX != 0 && diffY != 0) {
                    if (Math.abs(diffX) < Math.abs(diffY) && !canMoveHor) {
                        rect.x += diffX;
                    } else if (!canMoveVer) {
                        rect.y += diffY;
                    }
                } else {
                    if (!canMoveHor) rect.x += diffX;
                    if (!canMoveVer) rect.y += diffY;
                    //if (objectIsPlayer) {
                    if (!platformMode || !objectIsPlayer) {
                        if (diffY != 0 && (downSide || upSide) && !canMoveHor){
                            if (rightSide && centerX < center2X && hitBox.x + hitBox.width - rect.x < 8) {
                                if (rect.x + (8 - (hitBox.x + hitBox.width - rect.x)) / 5 < hitBox.x+hitBox.width) rect.x += (8 - (hitBox.x + hitBox.width - rect.x)) / 5;
                                else rect.x = hitBox.x + hitBox.width;
                            } else if (leftSide && centerX > center2X && rect.x + rect.width - hitBox.x < 8) {
                                if (rect.x - (8 - (rect.x + rect.width - hitBox.x)) / 5 + rect.width > hitBox.x) rect.x -= (8 - (rect.x + rect.width - hitBox.x)) / 5;
                                else rect.x = hitBox.x - rect.width;
                            }
                        } else if (diffX != 0 && (leftSide || rightSide) && !canMoveVer) {
                            if (upSide && centerY < center2Y && hitBox.y+hitBox.height-rect.y < 5) {
                                rect.y += (5-(hitBox.y+hitBox.height-rect.y)) / 5;
                            } else if (downSide && centerY > center2Y && rect.y+rect.height-hitBox.y < 5) {
                                rect.y -= (5-(rect.y+rect.height-hitBox.y)) / 5;
                            }
                        }
                    }
                    //}
                }
                if (diffX > 0) {
                    he.canLeft = false;
                    he.deadEndY = rect.y;
                }
                else if (diffX < 0) {
                    he.canRight = false;
                    he.deadEndY = rect.y;
                }
                if (diffY > 0) {
                    he.canUp = false;
                    he.deadEndX = rect.x;
                }
                else if (diffY < 0) {
                    he.canDown = false;
                    he.deadEndX = rect.x;
                }
            }

            he.deltaX = deltaX || (rect.x - oldRect.x) != 0;
            he.deltaY = deltaY || (rect.y - oldRect.y) != 0;
            if (he.deltaX != false) {
                System.out.println();
            }
        }
        x = hitBox.x;
        y = hitBox.y;
        //this.oldX = x;
        //this.oldY = y;
        /*if (platformMode && Math.abs(hitBox.y - oldY2) < 0.3f && pSpeed == 0) {
            hitBox.y = oldY2;
        }*/
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

        if (deltaX == false) {
            graphicX = Math.round(hitBox.x);
        } else {
            graphicX = hitBox.x;
        }
        if (deltaY == false) {
            graphicY = Math.round(hitBox.y);
        } else {
            graphicY = hitBox.y;
        }
        deltaX = false;
        deltaY = false;

        super.initialiseIfNeeded();
        if (tex != null) {
            batch.draw(tex, offsetX+graphicX+hitBox.getWidth()/2-tex.getWidth()/2, offsetY - graphicY-floorHeight-z, tex.getWidth(), tex.getHeight());
        } else {
            batch.draw(texDef, offsetX+graphicX+hitBox.getWidth()/2-texDef.getWidth()/2, offsetY - graphicY-floorHeight-z, texDef.getWidth(), texDef.getHeight());
        }
    }
}
