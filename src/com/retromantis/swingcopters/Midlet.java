package com.retromantis.swingcopters;

import com.retromantis.nikuniwe.NikApplication;
import javax.microedition.lcdui.Command;
import javax.microedition.midlet.MIDlet;

public final class Midlet extends MIDlet {
    
    protected static MenuScreen menuScreen;
    protected static GameScreen gameScreen;
    
    protected final void startApp() {
        if(gameScreen == null) {
            NikApplication app = NikApplication.getInstance(this);
            if(app.canvas.hasPointerEvents()) {
                app.canvas.addCommand( new Command("Back", Command.BACK, 1) );
            }
            menuScreen = new MenuScreen();
            gameScreen = new GameScreen();
            app.gotoScreen(menuScreen);
        }
    }

    protected final void pauseApp() {}

    protected final void destroyApp(boolean unconditional) {}

}