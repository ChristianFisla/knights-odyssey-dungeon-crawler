// Christian Fisla
// January 28th, 2022
// You are a Knight, and have descended into a cramped dungeon unexpectedly. You talk to an old man who gives you a throwing axe, and he tasks you with fighting your way out of the dungeon. Maneuver through the narrow walls, defeat enemies, and find chests to get extra items to help you on your journey. Good luck!

package com.fisla.ISU;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.StringTokenizer;

public class Start extends JPanel implements Runnable {

    // Initliaze JFrame and important game variables. These control the time, like FPS tickrate, and how often chests reset and enemies spawn.
    private static JFrame frame = new JFrame("ISU");
    private final int FPS = 35;
    private static int tick = 0;
    private static int score = 0;
    private static int spawnRate = 800;
    private static int resetRateChests = 1100;

    // Boolean values for the players monologue after picking up the axe
    private static boolean monologue;
    private static boolean monologueFinished;

    // Boolean values for which menu the player is in
    private static boolean inMenu = true;
    private static boolean startScreen = true;
    private static boolean howToPlayScreen = false;
    private static boolean aboutScreen = false;
    private static boolean endScreen = false;

    private static boolean endCredits = false;
    private static int endCreditsIncrement = 0;

    // Images of the menus
    private static BufferedImage startScreenImage;
    private static BufferedImage howToPlayScreenImage;
    private static BufferedImage aboutScreenImage;
    private static BufferedImage endScreenImage;

    static {
        try {
            startScreenImage = ImageIO.read(new File("assets/menu/1.png"));
            howToPlayScreenImage = ImageIO.read(new File("assets/menu/2.png"));
            aboutScreenImage = ImageIO.read(new File("assets/menu/3.png"));
            endScreenImage = ImageIO.read(new File("assets/menu/deathscreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isDead = false;

    // Boolean values for the player's movement
    private static boolean keys[] = {false, false, false, false, false};
    private static boolean blockInput = false;

    // Boolean values for zooming in and out
    private static boolean zoomingIn;
    private static boolean zoomingOut;

    private static boolean zoomAnimate;
    private static boolean playerAnimate;

    private static Player player;

    // Constructor for the game
    public Start() {

        // Sepcify the size of the JFrame
        setPreferredSize(new Dimension(608, 608));
        setBackground(new Color(118, 59, 54));
        setDoubleBuffered(true);

        // Init map and player
        Map map = new Map("assets/maps/ISUArtFinal.png", 1);
        player = new Player("assets/characters/knight/");
        new Dialogue();

        Thread thread = new Thread(this);
        thread.start();
    }

    // Start game loop
    @Override
    public void run() {
        while (true) {

            Mouse.update();

            if (inMenu) {

                if (endCredits && endCreditsIncrement > -400) endCreditsIncrement -= 5;

            } else {

                if (isDead) {
                    inMenu = true;
                    endScreen = true;

                    endCredits = true;
                }

                // Resets all chests except for the axe chest at the start of the game
                if (tick % resetRateChests == 0 && monologueFinished) {

                    // Looks for the index of the axe chest so it is not reset
                    Collections.sort(Map.getChest(), new SortByContent());
                    int indexOfAxeChest = Collections.binarySearch(Map.getChest(), new Chest(-100, -100, "assets/items/objects/chest/chest_0.png", "throwing_axe"), new SortByContent());

                    for (int i = 0; i < Map.getChest().size(); i++) {
                        if (i != indexOfAxeChest) Map.getChest().get(i).setOpened(false);
                    }
                }

                // Spawns enemies every 800 ticks. This value is shortened as the game progresses.
                if (tick % spawnRate == 0 && monologueFinished) {
                    for (int i = 0; i < 10; i++) {
                        Enemies.spawnSpider((int)(Map.getTopLeftPixelX() + (Map.getWidth() * Math.random())), (int)(Map.getTopLeftPixelY() + (Map.getHeight() * Math.random())), 0.5, 60 + (int) (Math.random() * 20), 70 + (i * 2), "normal");
                    }
                    for (int i = 0; i < 2; i++) {
                        Enemies.spawnOgre((int)(Map.getTopLeftPixelX() + (Map.getWidth() * Math.random())), (int)(Map.getTopLeftPixelY() + (Map.getHeight() * Math.random())), 0.3, 60 + (int) (Math.random() * 20), 150, "normal");
                    }
                    // Increase the spawn rate
                    if (spawnRate > 600) {
                        spawnRate -= 10;
                    } else {
                        spawnRate -= 5;
                    }
                }

                // If the game is in any sort of animation block all inputs
                if (!Player.getStartingAnimationComplete() || !Map.getStartingAnimationComplete() || Player.getKeepPositionOnPlane() || monologue) {
                    blockInput = true;

                    for (int i = 0; i < keys.length; i++) {
                        keys[i] = false;
                    }
                } else {
                    blockInput = false;
                }

                // Update enemies
                Enemies.updateAllEntityRatios();

                Dialogue.update();

                Map.zoomOnTick();

                if (zoomAnimate) Map.cutsceneZoomOnTick();

                // Move the map if the player is moving
                if (!blockInput) {
                    Map.updateMapMovementOnTick();
                    Map.updateBounds();
                }

                // After the player is done the starting animation, allow movement animations
                if (Player.getStartingAnimationComplete()) {
                    if (!(keys[1] && keys[3]) || !(keys[0] && keys[2])) {
                        if (keys[0] && !(keys[1] || keys[3])) Player.wKeyPress();
                        if (keys[1]) Player.aKeyPress();
                        if (keys[2] && !(keys[1] || keys[3])) Player.sKeyPress();
                        if (keys[3]) Player.dKeyPress();
                    }
                }

                // Update all NPCs
                for (int i = 0; i < Map.getNpc().size(); i++) {
                    Map.getNpc().get(i).update();
                }
                // Update all chests
                for (int i = 0; i < Map.getChest().size(); i++) {
                    Map.getChest().get(i).update();
                }

                // Update all enemies
                Enemies.updateAllEntities();

                // Update all coins
                for (int i = 0; i < Coin.getCoins().size(); i++) {
                    Coin.getCoins().get(i).update();
                }

                // Check collisions between the player and all enemies
                if (Enemies.checkCollisionsWithPlayer()) {
                    isDead = true;
                }
            }

            tick++;
            this.repaint();
            try {
                Thread.sleep(1000 / FPS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // If the player is in the menu, decide which menu screen to display
        if (inMenu) {

            if (startScreen) g.drawImage(startScreenImage, 0, 0,608, 608, null);
            if (howToPlayScreen) g.drawImage(howToPlayScreenImage, 0, 0,608, 608, null);
            if (aboutScreen) g.drawImage(aboutScreenImage, 0, 0,608, 608, null);
            if (endScreen) {
                g.drawImage(endScreenImage, 0, 0,608, 608, null);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.WHITE);
                g.drawString(String.format("Spiders Killed: %-17d Ogres Killed: %-5d", Enemies.getStatCounter().get("spider"), Enemies.getStatCounter().get("ogre")), 100, 610 + endCreditsIncrement);
                g.drawString("Axe Thrown " + Enemies.getStatCounter().get("axe_thrown") + " Times         Damage Dealt: " + Enemies.getStatCounter().get("damage_dealt") , 100, 630 + endCreditsIncrement);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("Score: " + score, 100, 680 + endCreditsIncrement);
                g.drawString("Thank you for playing!", 100, 950 + endCreditsIncrement);
            }

        } else {

            // Main game loop, start with the starting map animation
            if (!Map.getStartingAnimationComplete()) {
                Map.startingZoom(g);
            }

            // Player animation
            if (!Player.getStartingAnimationComplete() && Map.getStartingAnimationComplete()) {
                Player.startAnimation();
                if (Player.getY() >= 296) Player.setStartingAnimationComplete(true);
            }

            // Only update the player once animation complete
            if (Player.getStartingAnimationComplete()) Player.update();

            g.drawImage(Map.getMap(), (int) Map.getTopLeftPixelX(), (int) Map.getTopLeftPixelY(), (int) Map.getWidth(), (int) Map.getHeight(), null);


            // Draw all chests
            for (int i = 0; i < Map.getChest().size(); i++) {
                Chest chest = Map.getChest().get(i);
                g.drawImage(chest.getImage(), (int) chest.getX(), (int) chest.getY(), (int) chest.getScale(), (int) chest.getScale(), null);
            }

            // Update all enemy entities that need to draw
            Enemies.drawAllEnemies(g);
            Enemies.drawAllHealthbars(g);

            // Draw all NPCs
            for (int i = 0; i < Map.getNpc().size(); i++) {
                Npc npc = Map.getNpc().get(i);
                g.drawImage(npc.getImage(), (int) npc.getX(), (int) npc.getY(), (int) npc.getScale(), (int) npc.getScale(), null);
            }

            // Draw the throwing axe
            if (ThrowingAxe.getThrowingAxes().size() != 0 || Player.getInventory().getInventory().size() != 0) {
                Weapon invWeapon;

                invWeapon = (ThrowingAxe.getThrowingAxes().size() != 0) ? ThrowingAxe.getThrowingAxes().get(0) : Player.getInventory().getInventory().get(0);

                if ((Player.getIsFiring() && Player.getInvWeaponEquipped()) || invWeapon.getPopping()) {
                    g.drawImage(invWeapon.getImage(), (int) invWeapon.getX(), (int) invWeapon.getY(), (int) (Player.getDimensionX() * 0.75), (int) (Player.getDimensionY() * 0.75), null);
                }
            }

            // Draw all coins
            for (int i = 0; i < Coin.getCoins().size(); i++) {
                Coin coin = Coin.getCoins().get(i);

                if (coin.getPopping()) {
                    g.drawImage(coin.getImage(), (int) coin.getX(), (int) coin.getY(), (int) (Player.getDimensionX() * 0.5), (int) (Player.getDimensionY() * 0.5), null);
                }
            }

            // Draw the player
            g.drawImage(Player.getImage(), (int) (Player.getX() + MapRange.mapRange(Player.getDimensionX(), 15.51, 40.15, 6, 0)), (int) (Player.getY() + MapRange.mapRange(Player.getDimensionX(), 15.51, 40.15, 4, -4)), (int) Player.getDimensionX(), (int) Player.getDimensionY(), null);

            boolean npcIsTalking = false;

            // Find out if any of the NPCs are engaged in a conversation
            for (int i = 0; i < Map.getNpc().size(); i++) {
                if (Map.getNpc().get(i).getIsTalking()) npcIsTalking = true;
            }

            if (npcIsTalking || monologue) {
                // Draw the dialogue box
                g.drawImage(Dialogue.getImage(), Dialogue.getX(), Dialogue.getY(), Dialogue.getWidth(), Dialogue.getHeight(), null);

                String[] param = null;
                String[] paramSpeakers = null;

                // Figure out which dialogue to display
                if (npcIsTalking) {
                    param = Dialogue.getFirstInteraction();
                    paramSpeakers = Dialogue.getFirstInteractionSpeakers();
                } else if (monologue) {
                    param = Dialogue.getMonologue();
                    paramSpeakers = Dialogue.getMonologueSpeakers();
                }

                // Determine accurate width
                FontMetrics fm = g.getFontMetrics();
                int width = fm.stringWidth(Dialogue.getCurrentText(param));

                g.setFont(new Font("Arial", Font.PLAIN, 12));

                // Break up the line and keep it within certain bounds
                if (width > 425) {

                    String text = Dialogue.getCurrentText(param);

                    int line = 1;

                    StringTokenizer st = new StringTokenizer(text, " ", true);

                    while (st.hasMoreTokens()) {

                        String words = st.nextToken();

                        while (fm.stringWidth(words) < 425 && st.hasMoreTokens()) {
                            words += st.nextToken();
                        }

                        g.drawString(words.trim(), Dialogue.getX() + 120, Dialogue.getY() + (line * 20) + 10);

                        line++;
                    }

                } else {
                    g.drawString(Dialogue.getCurrentText(param), Dialogue.getX() + 120, Dialogue.getY() + 30);
                }

                // Draw the speaker next to their speech
                if (Dialogue.getCurrentSpeaker(paramSpeakers).equalsIgnoreCase("Knight")) {
                    g.drawImage(Player.getMovementImages()[3], Dialogue.getX() + 35, Dialogue.getY() + 15, 60, 60, null);
                } else if (Dialogue.getCurrentSpeaker(paramSpeakers).equalsIgnoreCase("NPC")) {
                    g.drawImage(Map.getNpc().get(0).getImage(), Dialogue.getX() + 35, Dialogue.getY() + 15, 60, 60, null);
                } else if (Dialogue.getCurrentSpeaker(paramSpeakers).equalsIgnoreCase("ogre")) {

                    BufferedImage ogre = null;

                    try {
                        ogre = ImageIO.read(new File("assets/characters/enemies/ogre.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    g.drawImage(ogre, Dialogue.getX() + 35, Dialogue.getY() + 15, 60, 60, null);
                }

                g.setFont(new Font("Arial", Font.PLAIN, 10));

                g.drawString("Press space to continue", Dialogue.getX() + 435, Dialogue.getY() + 85);

            }

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("SCORE: " + score, 10, 25);
        }
    }

    public static void main(String[] args) {

        // Initialize the JFrame
        Start panel = new Start();

        frame.setSize(608, 608);
        frame.add(panel);

        // Add key listener
        frame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

                char key = e.getKeyChar();

                // Provide correct functionality for the key pressed
                if (!blockInput) {
                    if (key == 'q') {
                        Map.zoomIn();
                    } else if (key == 'e') {
                        Map.zoomOut();
                    } else if (key == 'w') {
                        Map.setShiftDown(true);
                        keys[0] = true;
                    } else if (key == 'a') {
                        Map.setShiftRight(true);
                        keys[1] = true;
                    } else if (key == 's') {
                        Map.setShiftUp(true);
                        keys[2] = true;
                    } else if (key == 'd') {
                        Map.setShiftLeft(true);
                        keys[3] = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                char key = e.getKeyChar();

                boolean npcIsTalking = false;

                for (int i = 0; i < Map.getNpc().size(); i++) {
                    if (Map.getNpc().get(i).getIsTalking()) npcIsTalking = true;
                }

                // Skip the dialogue if it is currently being displayed
                if (npcIsTalking || monologue) {
                    if (key == ' ') {
                        if (npcIsTalking) {
                            Npc.skipDialogue();
                        }

                        Dialogue.inrCurrentText();

                        if (monologue) {
                            // If the player monologue has finished
                            if (Dialogue.getCurrentTextIndex() == Dialogue.getMonologue().length) {
                                monologue = false;
                                monologueFinished = true;
                                blockInput = false;

                                Start.zoomingIn = false;
                            }
                        }
                    }
                }
                // Provide correct functionality for the key released
                if (key == 'q') {
                    Start.zoomingIn = false;
                } else if (key == 'e') {
                    Start.zoomingOut = false;
                } else if (key == 'w') {
                    Map.setShiftDown(false);
                    keys[0] = false;
                    Player.setMovementImage(0);
                } else if (key == 'a') {
                    Map.setShiftRight(false);
                    keys[1] = false;
                    Player.setMovementImage(1);
                } else if (key == 's') {
                    Map.setShiftUp(false);
                    keys[2] = false;
                    Player.setMovementImage(2);
                } else if (key == 'd') {
                    Map.setShiftLeft(false);
                    keys[3] = false;
                    Player.setMovementImage(3);
                }
            }
        });

        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int x = Mouse.getPoint().x;
                int y = Mouse.getPoint().y;

                // If the player is in the main menu
                if (inMenu) {
                    // If the player clicks on any button, take them to the correct destination
                    if (startScreen) {
                        if (x > 190 && x < 425 && y > 265 && y < 381) {
                            startScreen = false;
                            inMenu = false;
                        }
                        if (x > 182 && x < 432 && y > 510 && y < 550) {
                            startScreen = false;
                            howToPlayScreen = true;
                        }
                        if (x > 198 && x < 426 && y > 571 && y < 610) {
                            startScreen = false;
                            aboutScreen = true;
                        }
                    } else if (howToPlayScreen) {
                        if (x > 15 && x < 80 && y > 45 && y < 68) {
                            howToPlayScreen = false;
                            startScreen = true;
                        }
                    } else if (aboutScreen) {
                        if (x > 15 && x < 80 && y > 45 && y < 68) {
                            aboutScreen = false;
                            startScreen = true;
                        }
                    }

                } else {

                    // Loop through all chests and call checkclicks
                    for (int i = 0; i < Map.getChest().size(); i++) {
                        Map.getChest().get(i).checkClicks();
                    }

                    for (int i = 0; i < Map.getNpc().size(); i++) {
                        Map.getNpc().get(i).checkClicks();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

                int hoverCount = 0;

                // Check if any NPCs OR chests are being hovered over so the player cannot fire
                for (int i = 0; i < Map.getChest().size(); i++) {
                    if (Map.getChest().get(i).getHovering()) hoverCount++;
                }

                for (int i = 0; i < Map.getNpc().size(); i++) {
                    if (Map.getNpc().get(i).getHovering()) hoverCount++;
                }

                if (!Player.getIsFiring() && Player.getStartingAnimationComplete() && hoverCount == 0) {
                    Mouse.setFirePoint(Mouse.getPoint());
                    Player.fire();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        frame.setLocation(200, 25);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);

        // Initialize the stat counter
        Enemies.getStatCounter().put("spider", 0);
        Enemies.getStatCounter().put("ogre", 0);
        Enemies.getStatCounter().put("axe_thrown", 0);
        Enemies.getStatCounter().put("damage_dealt", 0);

    }

    // GETTERS AND SETTERS
    public static int getTick() {
        return tick;
    }
    public static void addScore() {
        score++;
    }
    public static JFrame getFrame() {
        return frame;
    }
    public static Player getPlayer() {
        return player;
    }
    public static void setZoomingIn(boolean set) {
        zoomingIn = set;
    }
    public static void setZoomingOut(boolean set) {
        zoomingOut = set;
    }
    public static boolean getZoomingIn() {
        return zoomingIn;
    }
    public static boolean getZoomingOut() {
        return zoomingOut;
    }
    public static boolean getZoomAnimate() {
        return zoomAnimate;
    }
    public static void setZoomAnimate(boolean set) {
        zoomAnimate = set;
    }
    public static boolean getPlayerAnimate() {
        return playerAnimate;
    }
    public static void setPlayerAnimate(boolean set) {
        playerAnimate = set;
    }
    public static boolean getMonologue() {
        return monologue;
    }
    public static void setMonologue(boolean set) {
        monologue = set;
    }
    public static boolean getMonologueFinished() {
        return monologueFinished;
    }
    public static void setIsDead(boolean set) {
        isDead = set;
    }
    public static boolean getBlockInput() {
        return blockInput;
    }
    public static void setBlockInput(boolean set) {
        blockInput = set;
    }
}