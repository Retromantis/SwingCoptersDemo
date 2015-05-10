package com.retromantis.swingcopters;

import com.retromantis.nikuniwe.NikSprite;
import javax.microedition.lcdui.Image;

/**
 * @author Restromantis
 */
public final class Hammer extends NikSprite {
    
    private static Image hammer;
    
    public Hammer() {
        if(hammer instanceof Image) {
            constructor(hammer, 0,0,0,0, 88,68);
        } else {
            constructor("/hammer.png", 0,0,0,0, 88,68);
        }
    }
    
    public final void onCreate() {
        setFrameSequence(new int[]{0,1,2,3,4,3,2,1,0,5,6,7,8,7,6,5}, 2, true);
        setCollisiontRect(32,39,25,23);
        
        frames[1][4] = 26;
        frames[1][5] = 39;
        
        frames[2][4] = 20;
        frames[2][5] = 38;

        frames[3][4] = 14;
        frames[3][5] = 37;
        
        frames[4][4] = 7;
        frames[4][5] = 36;
        
        frames[5][4] = 37;
        frames[5][5] = 39;
        
        frames[6][4] = 43;
        frames[6][5] = 38;
        
        frames[7][4] = 49;
        frames[7][5] = 37;
        
        frames[8][4] = 56;
        frames[8][5] = 36;
    }

}
