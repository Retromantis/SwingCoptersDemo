package com.retromantis.swingcopters;

import com.retromantis.nikuniwe.NikSprite;

/**
 * @author Retromantis
 */
public final class Copter extends NikSprite {
    
    private final GameScreen game;
    
    protected final int[] SEQ_LEFT  = {0,0,0,1,1,1,2,1,1,1};
    protected final int[] SEQ_RIGHT = {7,7,7,8,8,8,9,8,8,8};
    protected final int[] SEQ_LFLY  = {3};
    protected final int[] SEQ_RFLY  = {10};
    protected final int[] SEQ_LDOWN = {0,4,5,6};
    protected final int[] SEQ_RDOWN = {7,11,12,13};
    protected final int[] SEQ_LFLIP = {5};
    protected final int[] SEQ_RFLIP = {12};
    protected final int[] VELOCITY  = {2,3,5,8,13,21};
    
    private int velocity;
    private int MAX_VELOCITY;
    
    public Copter(GameScreen screen) {
        game = screen;
        constructor("/copter.png", 0,0,0,0, 32,33);
    }
    
    public final void onCreate() {
        setCollisiontRect(4,4,24,32);
        MAX_VELOCITY = VELOCITY.length - 1;
        velocity = 0;
        vy = 4;
    }
    
    public final void onUpdate() {
        if(game.gameSection == game.GAME_PLAY) {
            if(dir == NikSprite.DIR_LEFT) {
                vx = -VELOCITY[velocity];
            } else {
                vx = VELOCITY[velocity];
            }
            if(velocity < MAX_VELOCITY) {
                velocity++;
            }
            moveXY(vx,0);
            game.prop.moveXY(vx,0);
            if(x <= 0) {
                game.downGame();
            }
            if((x + getFrameWidth()) >= game.width) {
                game.downGame();
            }
        } else if(game.gameSection == game.GAME_DOWN) {
            if(y < (game.height - 32)) {
                moveXY(0,vy);
            } else {
                game.scoreGame();
            }
        }
        if(game.gameSection != game.GAME_PAUSE && game.gameSection != game.GAME_OVER) {
            nextFrame();
        }
    }
    
    protected final void readyLeft() {
        dir = NikSprite.DIR_LEFT;
        velocity = 0;
        setFrameSequence(SEQ_LEFT, 4, true);
    }
    
    protected final void readyRight() {
        dir = NikSprite.DIR_RIGHT;
        velocity = 0;
        setFrameSequence(SEQ_RIGHT, 4, true);
    }
    
    protected final void flyLeft() {
        dir = NikSprite.DIR_LEFT;
        velocity = 0;
        setFrameSequence(SEQ_LFLY, 0, false);
        game.prop.setFrameSequence(game.prop.SEQ_LEFT, 0, true);
    }
    
    protected final void flyRight() {
        dir = NikSprite.DIR_RIGHT;
        velocity = 0;
        setFrameSequence(SEQ_RFLY, 0, false);
        game.prop.setFrameSequence(game.prop.SEQ_RIGHT, 0, true);
    }
    
}
