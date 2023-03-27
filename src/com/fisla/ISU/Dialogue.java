package com.fisla.ISU;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;

public class Dialogue {

    private static BufferedImage speechBubbleImage;

    private static int x = 0;
    private static int y = 616;
    private static int width = 608;
    private static int height = 128;

    private static int startTalkingTick;
    private static boolean animate = false;

    // All of the possible dialogue options
    private static String[] firstInteraction = {"Greetings, brave knight. I see you have ventured into this cursed dungeon.", "Yes, I am on a quest to defeat the evil that lies within.", "Beware, for this place is full of danger and despair. The enemies are fierce and the passageways are perilous.", "I have come prepared. I will not falter in my mission.", "I admire your bravery, but I fear it may not be enough. I have been trapped here for years, and I have seen many brave souls fall to the dangers of this dungeon.", "What can you tell me about this place?", "The dungeon is filled with all manner of monsters and beasts. Some are small and weak, while others are large and powerful. The passageways are twisting and misleading, and it is easy to get lost.", "Is there any way to escape this place?", "I have searched for a way out for many years, but to no avail. But I have one last hope for you. Take my axe, it has served me well in battle and may aid you in your quest. It is located in the chest above me.", "I will take it. Thank you.", "Be careful, knight. And may the gods be with you."};
    private static String[] firstInteractionSpeakers = {"NPC", "Knight", "NPC", "Knight", "NPC", "Knight", "NPC", "Knight", "NPC", "Knight", "NPC"};

    private static String[] monologue = {"W-what's that rumbling??", "hrrrrrggggggh........", "Who's there?!"};
    private static String[] monologueSpeakers = {"Knight", "Ogre", "Knight"};

    private static int currentText = 0;

    public Dialogue() {

        try {
            speechBubbleImage = ImageIO.read(new File("assets/dialogue/speechBubble.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Desc: Updates the position of the dialogue box to animate it
    // Param: n/a
    // Return: void
    public static void update() {

        if (animate) {
            if (Start.getTick() <= startTalkingTick + 15) {
                y -= 4;
            } else if (Start.getTick() > startTalkingTick + 15 && Start.getTick() <= startTalkingTick + 35) {
                y -= 2;
            } else if (Start.getTick() > startTalkingTick + 35 && Start.getTick() <= startTalkingTick + 50) {
                y -= 1;
            } else {
                animate = false;
            }
        }

    }

    // GETTERS AND SETTERS
    public static void inrCurrentText() {
        currentText++;
    }
    public static void getStartingTickAnimation() {
        startTalkingTick = Start.getTick();
    }
    public static BufferedImage getImage() {
        return speechBubbleImage;
    }
    public static int getX() {
        return x;
    }
    public static int getY() {
        return y;
    }
    public static int getWidth() {
        return width;
    }
    public static int getHeight() {
        return height;
    }
    public static String getCurrentText(String[] text) {
        return text[currentText];
    }
    public static int getCurrentTextIndex() {
        return currentText;
    }
    public static String getCurrentSpeaker(String[] speakers) {
        return speakers[currentText];
    }
    public static void setAnimate(boolean set) {
        animate = set;
    }
    public static String[] getFirstInteraction() {
        return firstInteraction;
    }
    public static String[] getMonologue() {
        return monologue;
    }
    public static void setCurrentText(int set) {
        currentText = set;
    }
    public static void setY(int set) {
        y = set;
    }
    public static String[] getFirstInteractionSpeakers() {
        return firstInteractionSpeakers;
    }
    public static String[] getMonologueSpeakers() {
        return monologueSpeakers;
    }
}
