package com.retromantis.swingcopters;

import com.retromantis.nikuniwe.NikSprite;

/**
 * @author Retromantis
 */
public final class Prop extends NikSprite {
    
    protected final int[] SEQ_READY = {0};
    protected final int[] SEQ_LEFT  = {4,5,6,7};
    protected final int[] SEQ_RIGHT = {8,9,10,11};
    
    public Prop() {
        constructor("/prop.png", 0,0,0,0, 52,14);
    }
    
}
