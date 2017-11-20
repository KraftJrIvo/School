package com.mygdx.schoolRPG.tools;

public class IntCoords {
	public int x;
	public int y;
	IntCoords previous;
	float hCosts = 0;
	float gCosts = 0;

    public IntCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

	public IntCoords() {
		this.x = 0;
		this.y = 0;
	}
	
	public void setPrevious(IntCoords previous) {
    	this.previous = previous;
	}

	public IntCoords getPrevious() {
		return previous;
	}

	public void sethCosts(IntCoords goal) {
		hCosts = (float)Math.sqrt((goal.x - this.x) * (goal.x - this.x) + (goal.y - this.y) * (goal.y - this.y));
	}

	public void setgCosts(IntCoords prevNode) {
        gCosts = prevNode.gCosts + 1;
        /*if (prevNode.x != x && prevNode.y != y) {
            gCosts = prevNode.gCosts + 0.7f;
        } else {
            gCosts = prevNode.gCosts + 1;
        }*/
	}

	public float calculategCosts(IntCoords prevNode) {
    	if (prevNode != null) {
    		/*if (prevNode.x != x && prevNode.y != y) {
				return prevNode.gCosts + 0.7f;
			}*/
    		return prevNode.gCosts + 1;
		}
		return -1;
	}

	public float hypot(IntCoords coords) {
    	return (float)Math.sqrt((coords.x - x) * (coords.x - x) + (coords.y - y)*(coords.y - y));
	}
}
