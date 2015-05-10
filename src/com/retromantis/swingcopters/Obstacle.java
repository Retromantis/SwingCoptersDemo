package com.retromantis.swingcopters;

import com.retromantis.nikuniwe.NikApplication;
import com.retromantis.nikuniwe.NikDrawable;
import com.retromantis.nikuniwe.NikSprite;
import javax.microedition.lcdui.Graphics;

/**
 * @author Retromantis
 */
public final class Obstacle extends NikSprite {
    
    private final int BEAM_VSPACING = 200;
    private final int RIGHT_BEAM_X = 300;
    private final int HAMMER_Y = 24;
    private final int LEFT_HAMMER_X = 99;
    private final int RIGHT_HAMMER_X = 273;
    
    private final NikSprite leftBeam;
    private final NikSprite rightBeam;
    private final Hammer leftHammer;
    private final Hammer rightHammer;
    private final NikSprite rect;
    private boolean bColl;
    
    private static int BEAM_X;
    private static GameScreen game;
    private static NikApplication app;
    
    public Obstacle(GameScreen screen) {
        game = screen;
        app = NikApplication.getInstance(null);
                
        leftBeam = new NikSprite("/beam.png",0,0);
        rightBeam = new NikSprite(leftBeam);
        
        leftHammer = new Hammer();
        rightHammer = new Hammer();
        
        rect = new NikSprite();
        rect.cwidth = 140;
        rect.cheight = 32;
        
        BEAM_X = (game.width >> 1) - 198;
        vy = 4;
    }
    
    public final void posXY(int px, int py) {
        x = px;
        y = py;
        rect.cx = x + 160;
        rect.cy = y - 32;
        leftBeam.posXY(px,py);
        rightBeam.posXY(px + RIGHT_BEAM_X, py);
        leftHammer.posXY(px + LEFT_HAMMER_X, py + HAMMER_Y);
        rightHammer.posXY(px + RIGHT_HAMMER_X, leftHammer.y);
    }
    
    public final void onDraw(Graphics g) {
        leftBeam.onDraw(g);
        rightBeam.onDraw(g);
        leftHammer.onDraw(g);
        rightHammer.onDraw(g);
    }

    public final void onUpdate() {
        if(game.gameSection != game.GAME_PAUSE && game.gameSection != game.GAME_OVER) {
            leftHammer.nextFrame();
            rightHammer.nextFrame();
        }
        if(game.gameSection == game.GAME_PLAY) {
            if(y < parent.height) {
                posXY(x,y + vy);
            } else {
                game.firstObstacle.posXY(nextPosX(), game.obstacles[2].y - BEAM_VSPACING);
                game.obstacles[0] = game.obstacles[1];
                game.obstacles[1] = game.obstacles[2];
                game.obstacles[2] = game.firstObstacle;
                game.firstObstacle = game.obstacles[0];
            }
        }
    }
    
    protected final int nextPosX() {
        return BEAM_X - (app.random(8) << 3);
    }
    
    public final boolean collidesWith(NikDrawable copter) {
        if(rect.collidesWith(copter)) {
            if(!bColl) {
                bColl = true;
                game.increaseScore();
            }
        } else {
            bColl = false;
        }
        boolean res = leftHammer.collidesWith(copter);
        if(!res) {
            res = rightHammer.collidesWith(copter);
        } if(!res) {
            res = leftBeam.collidesWith(copter);
        } if(!res) {
            res = rightBeam.collidesWith(copter);
        }
        return res;
    }
    
}
