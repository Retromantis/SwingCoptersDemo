package com.retromantis.swingcopters;

import com.retromantis.nikuniwe.NikApplication;
import com.retromantis.nikuniwe.NikBackground;
import com.retromantis.nikuniwe.NikButton;
import com.retromantis.nikuniwe.NikLayer;
import com.retromantis.nikuniwe.NikRunnable;
import com.retromantis.nikuniwe.NikScreen;
import com.retromantis.nikuniwe.NikSprite;
import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

/**
 * @author Retromantis
 */
public final class MenuScreen extends NikScreen implements NikRunnable {
    
    private int SCR_WDT;
    private int SCR_HGT;
    private int WIN_HGT;
    private int MAIN_Y;// 320x200 playground centered x position
    private int PADDING;
    
    private Image menu_buttons;
    private boolean bTouch;
    
    private NikBackground layerBg;
    private NikLayer layerMenu;
    private NikLayer layerHelp;
    private NikLayer layerAbout;
    
    private NikSprite spTitle;
    private NikSprite spHelp;
    private NikSprite spAbout;

    private final int MAX_HGT = 320;
    private final int BT_WDT  = 124;
    private final int BT_HGT  = 43;
    
    private final int BT_PLAY  = 0;
    private final int BT_HELP  = 1;
    private final int BT_ABOUT = 2;
    private final int BT_EXIT  = 3;
    private final int BT_BACK  = 4;
    
    // Play, Help, About, Exit, Back
    private NikButton[] buttons;
    private int lastButton, currButton;
    
    private NikApplication app;
    
    public final void onCreate() {
        app = NikApplication.getInstance(null);
        bTouch = app.canvas.hasPointerEvents();
        try {
            menu_buttons = Image.createImage("/menu_buttons.png");
        } catch (IOException ex) {}
        
        SCR_WDT  = app.canvas.getWidth();
        SCR_HGT = app.canvas.getHeight();
        
        WIN_HGT = MAX_HGT;
        if(SCR_HGT > WIN_HGT) {
            MAIN_Y = (SCR_HGT - WIN_HGT) >> 1;
        } else {
            WIN_HGT = SCR_HGT;
        }
        
        layerBg = new NikBackground("/menu_bg.png",1,-1);
        layerMenu = new NikLayer();
        layerHelp = new NikLayer();
        layerHelp.isDrawable = false;
        layerAbout = new NikLayer();
        layerAbout.isDrawable = false;

        spTitle = new NikSprite("/menu_title.png", 0,0);
        layerMenu.addChild(spTitle);
        
        spHelp  = new NikSprite("/menu_help.png", 0,0);
        layerHelp.addChild(spHelp);
        
        spAbout = new NikSprite("/menu_about.png", 0,0);
        layerAbout.addChild(spAbout);
        
        PADDING = (WIN_HGT - (spTitle.height + (BT_HGT * 4))) / 6;
        int by = MAIN_Y + PADDING;
        
        spTitle.posXY((SCR_WDT - spTitle.width) >> 1, by);
        spHelp.posXY((SCR_WDT - spHelp.width) >> 1, by);
        spAbout.posXY((SCR_WDT - spAbout.width) >> 1, by);
        
        buttons = new NikButton[5];
        // Play, Help, About, Exit
        int bx = (SCR_WDT - BT_WDT) >> 1;
        int sy = 0;
        int sw = BT_WDT << 1;
        by += spTitle.height + PADDING;
                
        for(int i=BT_PLAY; i < BT_BACK; i++, by += BT_HGT + PADDING, sy += BT_HGT) {
            buttons[i] = new NikButton(menu_buttons, 0, sy, sw, BT_HGT, BT_WDT, BT_HGT);
            buttons[i].id = i;
            buttons[i].runnable = this;
            buttons[i].posXY(bx, by);
            layerMenu.addChild(buttons[i]);
        }
        
        // Button Back
        buttons[BT_BACK] = new NikButton(menu_buttons, 0, sy, sw, BT_HGT, BT_WDT, BT_HGT);
        buttons[BT_BACK].id = BT_BACK;
        buttons[BT_BACK].runnable = this;
        buttons[BT_BACK].posXY(bx, buttons[BT_EXIT].y);
        layerHelp.addChild(buttons[BT_BACK]);
        layerAbout.addChild(buttons[BT_BACK]);
        
        addChild(layerBg);
        addChild(layerMenu);
        addChild(layerHelp);
        addChild(layerAbout);
        
        if(!bTouch) {
            buttons[BT_PLAY].setFrame(1, false);
        }
        
        loadBestScore();
    }
    
    public final void onUpdate() {
        layerBg.onUpdate();
    }
    
    public final void run(final Object sender) {
        switch( ((NikButton)sender).id ) {
            case BT_PLAY:
                app.gotoScreen(Midlet.gameScreen);
                break;
            case BT_HELP:
                layerMenu.isDrawable = false;
                layerHelp.isDrawable = true;
                break;
            case BT_ABOUT:
                layerMenu.isDrawable = false;
                layerAbout.isDrawable = true;
                break;
            case BT_EXIT:
                exitApp();
                break;
            case BT_BACK:
                layerMenu.isDrawable = true;
                layerHelp.isDrawable = false;
                layerAbout.isDrawable = false;
                break;
        }
    }
    
    public final void onBack() {
        if(layerMenu.isDrawable) {
            exitApp();
        } else {
            layerMenu.isDrawable = true;
            layerHelp.isDrawable = false;
            layerAbout.isDrawable = false;
        }
    }
    
    private void exitApp() {
        saveBestScore();
        app.closeApp();
    }
    
    public final void onKey(final int event, final int code) {
        if(!bTouch) {
            switch(app.canvas.getGameAction(code)) {
                case Canvas.UP:
                    if(currButton > BT_PLAY) {
                        currButton--;
                    } else {
                        currButton = BT_EXIT;
                    }
                    buttons[currButton].setFrame(1, false);
                    buttons[lastButton].setFrame(0, false);
                    lastButton = currButton;
                    break;
                case Canvas.DOWN:
                    if(currButton < BT_EXIT) {
                        currButton++;
                    } else {
                        currButton = BT_PLAY;
                    }
                    buttons[currButton].setFrame(1, false);
                    buttons[lastButton].setFrame(0, false);
                    lastButton = currButton;
                    break;
                case Canvas.FIRE:
                    if(layerMenu.isDrawable) {
                        run(buttons[currButton]);
                    } else {
                        run(buttons[BT_BACK]);
                    }
                    break;
                    
            }
        }
    }
    
    private void loadBestScore() {
        try {
            RecordStore rs = RecordStore.openRecordStore("bestscore", true);
            byte[] data = rs.getRecord(1);
            GameScreen.bestScore = data[0];
            rs.closeRecordStore();
        } catch (Exception ex) {}
    }
    
    private void saveBestScore() {
        try {
            byte[] data = new byte[1];
            data[0] = (byte)GameScreen.bestScore;
            RecordStore rs = RecordStore.openRecordStore("bestscore", true);
            if(rs.getNumRecords() == 0) {
                rs.addRecord(data, 0, 1);
            } else {
                rs.setRecord(1, data, 0, 1);
            }
            rs.closeRecordStore();
        } catch (Exception ex) {}
    }

}
