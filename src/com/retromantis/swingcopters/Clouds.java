package com.retromantis.swingcopters;

import com.retromantis.nikuniwe.NikApplication;
import com.retromantis.nikuniwe.NikDrawable;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * @author Retromantis
 */
public final class Clouds extends NikDrawable {
    
    private final int BLUE_COLOR   = 0x49C4E6;
    private final int PURPLE_COLOR = 0x8781BE;
    
    private static GameScreen game;
    private Image blue_clouds;
    private Image purple_clouds;
    private Image clouds;
    private final int nClouds;
    private boolean bPurple;
    private int color;
    private NikApplication app;
    
    public Clouds(GameScreen screen) {
        game = screen;
        app = NikApplication.getInstance(null);
        try {
            blue_clouds = Image.createImage("/blue_clouds.png");
            purple_clouds = Image.createImage("/purple_clouds.png");
        } catch (IOException ex) {}
        cheight = blue_clouds.getHeight();
        cx = (game.width - blue_clouds.getWidth()) >> 1;
        cy=-cheight;
        nClouds = (game.height / cheight) + 2;
    }
    
    protected final void reset() {
        cy=-cheight;
        if(app.random(10) > 4) {
            color = PURPLE_COLOR;
            clouds = purple_clouds;
        } else {
            color = BLUE_COLOR;
            clouds = blue_clouds;
        }
    }
    
    public final void onDraw(Graphics g) {
        g.setColor(color);
        g.fillRect(0,0,parent.width,parent.height);
        
        for(int n=0, py=cy; n < nClouds; n++, py+=cheight) {
            g.drawImage(clouds, cx, py, Graphics.TOP|Graphics.LEFT);
        }
    }

    public final void onUpdate() {
        if(cy < 0) {
            cy += 4;
        } else {
            cy = -cheight;
        }
    }
    
}
