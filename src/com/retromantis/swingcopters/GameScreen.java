package com.retromantis.swingcopters;

import com.retromantis.nikuniwe.NikApplication;
import com.retromantis.nikuniwe.NikBackground;
import com.retromantis.nikuniwe.NikButton;
import com.retromantis.nikuniwe.NikLayer;
import com.retromantis.nikuniwe.NikNumber;
import com.retromantis.nikuniwe.NikPlayer;
import com.retromantis.nikuniwe.NikRunnable;
import com.retromantis.nikuniwe.NikScreen;
import com.retromantis.nikuniwe.NikSprite;
import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Player;

/**
 * @author Retromantis
 */
public final class GameScreen extends NikScreen implements NikRunnable {
    
    protected final int GAME_READY = 0;
    protected final int GAME_PLAY  = 1;
    protected final int GAME_PAUSE = 2;
    protected final int GAME_DOWN  = 3;
    protected final int GAME_OVER  = 4;
    
    private final byte MEDAL_PLATINUM = 80;
    private final byte MEDAL_GOLD     = 60;
    private final byte MEDAL_SILVER   = 40;
    private final byte MEDAL_BRONZE   = 20;
    
    private final int BT_WIDTH = 248;
    private final int BT_WDT = 124;
    private final int BT_HGT = 43;
    private final int BT_RESUME_Y = 10;
    private final int BT_MENU_Y   = 10;
    
    private Clouds clouds;
    private NikSprite building;
    private NikSprite plants;
    
    protected Obstacle[] obstacles;
    protected Obstacle firstObstacle;
    
    protected Prop prop;
    private Copter copter;
    
    protected int gameSection;
    private NikSprite get_ready;
    private NikSprite game_over;
    private NikSprite tap;
    private NikNumber bigScore;
    private NikSprite new_score;
    
    private final int BT_PAUSE  = 0;
    private final int BT_RESUME = 1;
    private final int BT_MENU   = 2;
    private final int BT_OK     = 3;
    
    private boolean bTouch;
    private Image game_buttons;
    private NikButton btPause;
    private NikButton btResume;
    private NikButton btMenu;
    private NikButton btOk;
    private boolean bResume;
    
    private NikApplication app;
    private int score;
    protected static int bestScore;
    private NikSprite medal;
    private NikNumber tinyScore;
    private NikNumber tinyBestScore;
    
    private NikLayer layerReady;
    private NikLayer layerPause;
    private NikLayer layerOver;
    
    protected NikPlayer player;
    protected Player sfxProp;
    protected Player sfxPoint;
    protected Player sfxHit;
    
    public final void onCreate() {
        app = NikApplication.getInstance(null);
        bTouch = app.canvas.hasPointerEvents();
        try {
            game_buttons = Image.createImage("/game_buttons.png");
        } catch (IOException ex) {}
        
        player = NikPlayer.getInstance();
        sfxProp = player.createPlayer("/sfx_prop.mp3");
        sfxPoint = player.createPlayer("/sfx_point.mp3");
        sfxHit = player.createPlayer("/sfx_hit.mp3");
        
        clouds = new Clouds(this);
        building = new NikSprite("/building.png",0,0);
        building.centerX = true;
        plants = new NikSprite("/plants.png",0,0);
        plants.centerX = true;
        
        bigScore = new NikNumber("/big_number.png", 24, 36) {
            public void onCreate() {
                frames[1][2] = 16;
            }
        };
        bigScore.x = parent.width >> 1;
        bigScore.y = 16;
        
        obstacles = new Obstacle[3];
        obstacles[0] = new Obstacle(this);
        obstacles[1] = new Obstacle(this);
        obstacles[2] = new Obstacle(this);
        
        prop = new Prop();
        copter = new Copter(this);
        
        get_ready =  new NikSprite("/get_ready.png",0,0);
        get_ready.centerX = true;
        tap =  new NikSprite("/tap.png",0,0);
        tap.centerX = true;
        
        int bx = (parent.width - BT_WDT) >> 1;
        btResume = new NikButton(game_buttons, 0,0,BT_WIDTH,BT_HGT, BT_WDT,BT_HGT);
        btResume.posXY(bx, (parent.height >> 1) - BT_RESUME_Y - btResume.height);
        btResume.id = BT_RESUME;
        btResume.runnable = this;
        
        btMenu = new NikButton(game_buttons, 0,BT_HGT,BT_WIDTH,BT_HGT, BT_WDT,BT_HGT);
        btMenu.posXY(bx, (parent.height >> 1) + BT_MENU_Y);
        btMenu.id = BT_MENU;
        btMenu.runnable = this;
        
        btOk = new NikButton(game_buttons, 0,BT_HGT+BT_HGT,BT_WIDTH,BT_HGT, BT_WDT,BT_HGT);
        btOk.id = BT_OK;
        btOk.runnable = this;
        
        btPause = new NikButton(game_buttons, BT_WIDTH,0,44,86, 44,43);
        btPause.id = BT_PAUSE;
        btPause.runnable = this;
        
        int center = parent.width >> 1;
        if(parent.height > 240) {
            building.posXY(center, parent.height - 100);
            if(parent.height > 320) {
                get_ready.posXY(center, 64);
            } else {
                get_ready.posXY(center, 16);
            }
            tap.posXY(center, get_ready.y + 52);
            btOk.posXY(bx, parent.height - 24 - BT_HGT);
            btPause.posXY(12, parent.height - 46);
        } else {
            building.posXY(center, parent.height - 56);
            get_ready.posXY(center, 2);
            tap.posXY(center, get_ready.y + 44);
            btOk.posXY(bx, parent.height - 8 - BT_HGT);
            btPause.posXY(12, parent.height - 54);
        }
        plants.posXY(center, building.y + 40);
        copter.posXY((width >> 1) - (copter.getFrameWidth() >> 1), building.y + 16);
        
        game_over = new NikSprite("/game_over.png",0,0);
        game_over.centerX = true;
        game_over.posXY(center, get_ready.y);
        
        medal = new NikSprite("/medals.png", 44, 44);
        medal.posXY(game_over.x + 26, game_over.y + 104);
        medal.setFrame(-1, false);
        
        new_score = new NikSprite(game_buttons, 260,115,32,14, 32,14);
        new_score.posXY(game_over.x + 135, game_over.y + 120);
        new_score.setFrameSequence(new int[]{0,-1}, 2, true);
        
        Image tiny = null;
        try {
            tiny = Image.createImage("/tiny_number.png");
        } catch (IOException ex) {}
        tinyScore = new NikNumber(tiny, 14, 20) {
            public void onInit() {
                frames[1][2] = 10;
            }
        };
        tinyScore.posXY(game_over.x + 204, game_over.y + 93);
        
        tinyBestScore = new NikNumber(tiny, 14, 20) {
            public void onInit() {
                frames[1][2] = 10;
            }
        };
        tinyBestScore.posXY(game_over.x + 204, game_over.y + 135);
        
        addChild(clouds);
        addChild(building);
        addChild(obstacles[0]);
        addChild(obstacles[1]);
        addChild(obstacles[2]);
        addChild(prop);
        addChild(copter);
        addChild(plants);
        addChild(bigScore);
        addChild(btPause);
        
        layerReady = new NikLayer();
        layerReady.addChild(get_ready);
        layerReady.addChild(tap);
        
        layerPause = new NikLayer();
        layerPause.addChild(btResume);
        layerPause.addChild(btMenu);
        
        layerOver = new NikLayer();
        layerOver.addChild(new NikBackground("/bg_shading.png",0,0));
        layerOver.addChild(game_over);
        layerOver.addChild(medal);
        layerOver.addChild(tinyScore);
        layerOver.addChild(tinyBestScore);
        layerOver.addChild(new_score);
        layerOver.addChild(btOk);
        
        addChild(layerReady);
        addChild(layerPause);
        addChild(layerOver);
    }
    
    public final void onStart() {
        gameSection = GAME_READY;
        score = 0;
        new_score.isDrawable = false;
        bigScore.setValue(score, 1);
        
        building.isDrawable = true;
        plants.isDrawable = true;
        
        layerReady.isDrawable = true;
        layerPause.isDrawable = false;
        layerOver.isDrawable = false;
        
        clouds.reset();
        building.y = building.cy;
        plants.y = plants.cy;
        
        obstacles[0].posXY(obstacles[0].nextPosX(), building.y - 186);
        obstacles[1].posXY(obstacles[0].nextPosX(), obstacles[0].y - 200);
        obstacles[2].posXY(obstacles[0].nextPosX(), obstacles[1].y - 200);
        firstObstacle = obstacles[0];
        
        copter.posXY((width >> 1) - (copter.getFrameWidth() >> 1), building.y + 14);
        prop.posXY(copter.x - 10, copter.y - 14);
        prop.setFrameSequence(prop.SEQ_READY, 0, false);
        prop.isDrawable = true;
        
        if(app.random(2) == 0) {
            copter.readyLeft();
        } else {
            copter.readyRight();
        }
        
        tinyScore.setValue(score, 2);
        tinyBestScore.setValue(bestScore, 2);
        
        if(bestScore >= MEDAL_PLATINUM ) {
            medal.setFrame(3, false);
        } else if(bestScore >= MEDAL_GOLD) {
            medal.setFrame(2, false);
        } else if(bestScore >= MEDAL_SILVER) {
            medal.setFrame(1, false);
        } else if(bestScore >= MEDAL_BRONZE) {
            medal.setFrame(0, false);
        }
        btPause.isDrawable = true;
    }
    
    private void playGame() {
        player.start(sfxProp);
        gameSection = GAME_PLAY;
        layerReady.isDrawable = false;
        layerPause.isDrawable = false;
        btPause.isDrawable = true;
        
        if(copter.dir == NikSprite.DIR_LEFT) {
            copter.flyLeft();
        } else {
            copter.flyRight();
        }
    }
    
    private void pauseGame() {
        gameSection = GAME_PAUSE;
        btPause.isDrawable = false;
        if(!bTouch) {
            bResume = true;
            btResume.setFrame(1, false);
            btMenu.setFrame(0, false);
        }
        layerReady.isDrawable = false;
        layerPause.isDrawable = true;
    }
    
    protected final void downGame() {
        gameSection = GAME_DOWN;
        prop.isDrawable = false;
        btPause.isDrawable = false;
        if(copter.dir == NikSprite.DIR_LEFT) {
            copter.setFrameSequence(copter.SEQ_LDOWN, 0, true);
        } else {
            copter.setFrameSequence(copter.SEQ_RDOWN, 0, true);
        }
        player.start(sfxHit);
    }
    
    protected final void scoreGame() {
        if(copter.dir == NikSprite.DIR_LEFT) {
            copter.setFrameSequence(copter.SEQ_LFLIP, 0, false);
        } else {
            copter.setFrameSequence(copter.SEQ_RFLIP, 0, false);
        }
        gameSection = GAME_OVER;
        layerOver.isDrawable = true;
    }
    
    protected final void increaseScore() {
        score++;
        bigScore.setValue(score, 0);
        tinyScore.setValue(score, 2);
        if(score > bestScore) {
            new_score.isDrawable = true;
            bestScore = score;
            tinyBestScore.setValue(bestScore, 2);
            if(bestScore >= MEDAL_GOLD ) {
                medal.setFrame(2, false);
            } else if(bestScore >= MEDAL_SILVER) {
                medal.setFrame(1, false);
            } else if(bestScore >= MEDAL_BRONZE) {
                medal.setFrame(0, false);
            }
        }
        player.start(sfxPoint);
    }
    
    public final void onUpdate() {
        obstacles[0].onUpdate();
        obstacles[1].onUpdate();
        obstacles[2].onUpdate();
        copter.onUpdate();
        
        switch(gameSection) {
            case GAME_PLAY:
                clouds.onUpdate();
                prop.nextFrame();
                if(building.y < height) {
                    building.y += 4;
                } else {
                    building.isDrawable = false;
                }
                if(plants.y < height) {
                    plants.y += 4;
                } else {
                    plants.isDrawable = false;
                }
                if(firstObstacle.collidesWith(copter)) {
                    downGame();
                }
                break;
            case GAME_OVER:
                if(new_score.isDrawable) {
                    new_score.nextFrame();
                }
                break;
        }
    }
    
    public final boolean onTouch(Object sender, int event, int x, int y) {
        if(!super.onTouch(sender, event, x, y)) {
            if(event == NikApplication.PRESSED) {
                switch(gameSection) {
                    case GAME_READY:
                        playGame();
                        break;
                    case GAME_PLAY:
                        if(copter.dir == NikSprite.DIR_RIGHT) {
                            copter.flyLeft();
                        } else {
                            copter.flyRight();
                        }
                        break;
                }
            }
        }
        return true;
    }
    
    public final void onKey(final int event, final int code) {
        if(event == NikApplication.PRESSED) {
            int action = app.canvas.getGameAction(code);
            switch(gameSection) {
                case GAME_READY:
                    switch(action) {
                        case Canvas.LEFT:
                        case Canvas.RIGHT:
                        case Canvas.UP:
                        case Canvas.DOWN:
                        case Canvas.FIRE:
                            playGame();
                            break;
                        default:
                            pauseGame();
                    }
                    break;
                case GAME_PLAY:
                    switch(action) {
                        case Canvas.LEFT:
                        case Canvas.RIGHT:
                        case Canvas.UP:
                        case Canvas.DOWN:
                        case Canvas.FIRE:
                            if(copter.dir == NikSprite.DIR_RIGHT) {
                                copter.flyLeft();
                            } else {
                                copter.flyRight();
                            }
                            break;
                        default:
                            pauseGame();
                    }
                    break;
                case GAME_PAUSE:
                    switch(action) {
                        case Canvas.UP:
                        case Canvas.DOWN:
                            if(bResume) {
                                btResume.setFrame(0, false);
                                btMenu.setFrame(1, false);
                            } else {
                                btResume.setFrame(1, false);
                                btMenu.setFrame(0, false);
                            }
                            bResume = !bResume;
                            break;
                        case Canvas.FIRE:
                            if(bResume) {
                                playGame();
                            } else {
                                app.gotoScreen(Midlet.menuScreen);
                            }
                            break;
                        default:
                            playGame();
                            break;
                    }
                    break;
                case GAME_OVER:
                    onStart();
                    break;
            }
        }
    }
    
    public final void onBack() {
        switch(gameSection) {
            case GAME_READY:
                app.gotoScreen(Midlet.menuScreen);
                break;
            case GAME_PLAY:
                pauseGame();
                break;
            case GAME_PAUSE:
                playGame();
                break;
            case GAME_OVER:
                onStart();
                break;
        }
    }

    public final void run(final Object sender) {
        switch(((NikButton)sender).id) {
            case BT_PAUSE:
                pauseGame();
                break;
            case BT_RESUME:
                playGame();
                break;
            case BT_MENU:
                app.gotoScreen(Midlet.menuScreen);
                break;
            case BT_OK:
                onStart();
                break;
        }
    }
}
