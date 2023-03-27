package com.fisla.ISU;

public class BoundBlock {

    private double[] topLeftXY = new double[2];
    private double[] bottomRightXY = new double[2];

    private final double leftSideRatioTopLeft;
    private final double upSideRatioTopLeft;
    private final double leftSideRatioBottomRight;
    private final double getUpSideRatioBottomRight;

    private Hitbox hitbox;

    // Constructor
    public BoundBlock(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {

        // Set the top left and bottom right coordinates
        topLeftXY[0] = topLeftX;
        topLeftXY[1] = topLeftY;
        bottomRightXY[0] = bottomRightX;
        bottomRightXY[1] = bottomRightY;

        // Set the hitbox
        leftSideRatioTopLeft = topLeftXY[0] / 608.0;
        upSideRatioTopLeft = topLeftXY[1] / 608.0;
        leftSideRatioBottomRight = bottomRightXY[0] / 608.0;
        getUpSideRatioBottomRight = bottomRightXY[1] / 608.0;

        hitbox = new Hitbox(topLeftX, topLeftY, bottomRightX - topLeftX, bottomRightY - topLeftY);
    }
    // Desc: updates the collision of the block
    // Param: The entity that is colliding with the block
    // Return: Boolean, true if the entity is colliding with the block
    public boolean update(Object entity) {

        updatePositionOfBounds();
        return checkBlockBounds(entity);
    }

    // Desc: Update the position of the block
    // Param: n/a
    // Return: void
    public void updatePositionOfBounds() {

        // Update the top left and bottom right coordinates
        topLeftXY[0] = Map.getTopLeftPixelX() + Map.getWidth() * leftSideRatioTopLeft;
        topLeftXY[1] = Map.getTopLeftPixelY() + Map.getHeight() * upSideRatioTopLeft;

        bottomRightXY[0] = Map.getTopLeftPixelX() + Map.getWidth() * leftSideRatioBottomRight;
        bottomRightXY[1] = Map.getTopLeftPixelY() + Map.getHeight() * getUpSideRatioBottomRight;

        hitbox = new Hitbox((int)topLeftXY[0], (int)topLeftXY[1], (int)(bottomRightXY[0] - topLeftXY[0]), (int)(bottomRightXY[1] - topLeftXY[1]));
    }
    // Desc: Check if the entity is colliding with the block
    // Param: The entity that is colliding with the block
    // Return: Boolean, true if the entity is colliding with the block
    public boolean checkBlockBounds(Object entity) {

        double x;
        double y;

        // Get the x and y coordinates of the entity
        if (entity instanceof Player) {
            Player.findCenter();
            x = Player.getCenterX();
            y = Player.getCenterY();

            // Check if the entity is colliding with the block
            if (Player.getHitbox().collision(this.hitbox)) {

                if (x > bottomRightXY[0]) {
                    Map.setShiftRightCollision(false);
                    return true;
                }
                if (x < topLeftXY[0]) {
                    Map.setShiftLeftCollision(false);
                    return true;
                }
                if (y > bottomRightXY[1]) {
                    Map.setShiftDownCollision(false);
                    return true;
                }
                if (y > topLeftXY[1]) {
                    Map.setShiftUpCollision(false);
                    return true;
                }

            }
        }

        return false;
    }

    // GETTERS AND SETTERS
    public Hitbox getHitboX() {
        return hitbox;
    }
    public double[] getTopLeftXY() {
        return topLeftXY;
    }
    public double[] getBottomRightXY() {
        return bottomRightXY;
    }
}