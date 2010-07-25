package gr.forth.ics.graph.layout;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * A utility that can ease the drawing of arbitrary {@link Shape shapes}, via an interface similar to the
 * <a href="http://en.wikipedia.org/wiki/Logo_(programming_language)">
 * Logo programming language</a>.
 *
 * <p>Usage of this utility can avoid the need for trigonometric calculations for drawing
 * most linear geometric shapes.
 *
 * <p><strong>Note:</strong> All angle measures are expressed in <em>degrees</em>.
 * To translate into rads, use {@link Math#toRadians(double)}. This affects methods that
 * access or change the orientation of the {@code Logo}.
 *
 * <p>The {@code Logo} starts at point (0.0, 0.0), and faces "east" (towards right). It
 * can turn {@link #left(double) left} or {@link #right(double) right}, expressing angles
 * in degrees. It can move {@link #go(double) forwards} or {@link #back(double) backwards}.
 * It can {@link #fly() "fly"}, i.e. thereafter it moves without actually drawing, and it can
 * {@link #land() "land"}, i.e. it resumes drawing. (It is noted that {@code Logo}s are created
 * "landed" by default). It can
 * reorient towards {@link #faceEast() east} (right), {@link #faceNorth() north} (up),
 * {@link #faceWest() west} (left) and {@link #faceSouth() south} (down). It can also
 * move to arbitrary locations via {@link #goTo(Point2D)} and
 * {@link #goTo(double, double)}.
 *
 *
 * <p>Note that most methods can be chained, for example here is how an
 * arrow of the form {@code " ----->"} can be drawn:
 *{@code
 *Logo logo = new Logo();
 *logo.go(50).left(120).go(15).left(180).go(15).right(120).go(15);
 *}
 * 
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public abstract class Logo {
    private double rads;
    private double sin;
    private double cos;
    private double currentX;
    private double currentY;

    /**
     * Creates a Logo instance.
     */
    protected Logo() {
        setRads(0.0);
    }

    private boolean flying = false;

    /**
     * Makes this drawer to "fly" (that is, subsequent movements will not draw).
     *
     * @return this
     * @see #land()
     */
    public Logo fly() {
        flying = true;
        return this;
    }

    /**
     * Makes this drawer to "land" (that is, subsequent movements will draw).
     *
     * @return this
     * @see #fly()
     */
    public Logo land() {
        flying = false;
        return this;
    }

    /**
     * Relocates this drawer to the specified location, without changing its orientation.
     * Whether a line towards there will be drawn depends on whether this drawer
     * flies or is landed.
     *
     * @param point the location where to move this drawer
     * @return this
     * @see #fly()
     */
    public Logo goTo(Point2D point) {
        return goTo(point.getX(), point.getY());
    }

    /**
     * Relocates this drawer to the specified location, without changing its orientation.
     * Whether a line towards there will be drawn depends on whether this drawer
     * flies or is landed.
     *
     * @param x the x-coordinate of the location where to move this drawer
     * @param y the y-coordinate of the location where to move this drawer
     * @return this
     * @see #fly()
     */
    public Logo goTo(double x, double y) {
        moveTo(x, y);
        return this;
    }

    private void moveRelativeTo(double dx, double dy) {
        double x = currentX + dx;
        double y = currentY + dy;
        moveTo(x, y);
    }

    private void moveTo(double x, double y) {
        if (flying) {
            flyLine(currentX, currentY, x, y);
        } else {
            drawLine(currentX, currentY, x, y);
        }
        currentX = x;
        currentY = y;
    }

    /**
     * Draws a line from a start point to a target point.
     *
     * @param x1 the x-coordinate of the start point
     * @param y1 the y-coordinate of the start point
     * @param x2 the x-coordinate of the target point
     * @param y2 the y-coordinate of the target point
     */
    protected abstract void drawLine(double x1, double y1, double x2, double y2);

    /**
     * Moves from a start point to a target point.
     *
     * @param x1 the x-coordinate of the start point
     * @param y1 the y-coordinate of the start point
     * @param x2 the x-coordinate of the target point
     * @param y2 the y-coordinate of the target point
     */
    protected abstract void flyLine(double x1, double y1, double x2, double y2);

    /**
     * Moves this drawer forward for the specified amount of distance.
     * Whether a line towards there will be drawn depends on whether this drawer
     * flies or is landed.
     *
     * @param distance the distance to go forward
     * @return this
     */
    public Logo go(double distance) {
        moveRelativeTo(distance * cos, distance * sin);
        return this;
    }

    /**
     * Moves this drawer backwards for the specified amount of distance.
     * Whether a line towards there will be drawn depends on whether this drawer
     * flies or is landed.
     *
     * @param distance the distance to go backwards
     * @return this
     */
    public Logo back(double distance) {
        return go(-distance);
    }

    /**
     * Reorients this drawer to the east (right).
     * This is defined at {@code 0} {@link #getOrientation() degrees}.
     *
     * @return this
     */
    public Logo faceEast() {
        setRads(Math.toRadians(0.0));
        return this;
    }

    /**
     * Reorients this drawer to the west (left).
     * This is defined at {@code 180} {@link #getOrientation() degrees}.
     *
     * @return this
     */
    public Logo faceWest() {
        setRads(Math.toRadians(180.0));
        return this;
    }

    /**
     * Reorients this drawer to the north (up).
     * This is defined at {@code 90} {@link #getOrientation() degrees}.
     *
     * @return this
     */
    public Logo faceNorth() {
        setRads(Math.toRadians(90.0));
        return this;
    }

    /**
     * Reorients this drawer to the south (down).
     * This is defined at {@code 270} {@link #getOrientation() degrees}.
     *
     * @return this
     */
    public Logo faceSouth() {
        setRads(Math.toRadians(270.0));
        return this;
    }

    /**
     * Turns this drawer left, with an angle of the specifed amount of degrees.
     *
     * @param degrees the degrees of the angle make towards left
     * @return this
     */
    public Logo left(double degrees) {
        return right(-degrees);
    }

    /**
     * Turns this drawer right, with an angle of the specifed amount of degrees.
     *
     * @param degrees the degrees of the angle make towards right
     * @return this
     */
    public Logo right(double degrees) {
        setRads(rads + Math.toRadians(degrees));
        return this;
    }

    private void setRads(double rads) {
        this.rads = rads;
        sin = Math.sin(rads);
        cos = Math.cos(rads);
    }

    /**
     * Returns the current location of this drawer.
     *
     * @return the current location of this drawer
     */
    public Point2D getCurrentPosition() {
        return new Point2D.Double(currentX, currentY);
    }

    /**
     * Returns the current orientation of this drawer, in degrees.
     *
     * @return the current orientation of this drawer, in degrees
     */
    public double getOrientation() {
        return Math.toDegrees(rads);
    }

    /**
     * Orients this drawer towards the specified position. That means, if the drawer moves
     * forward, it will pass over that position, unless it is already located there.
     * If the specified position is exactly the current
     * position of this drawer, its orientation doesn't change.
     *
     * @param position the position to orient this drawer towards
     * @return this
     */
    public Logo orientTowards(Point2D position) {
        return orientTowards(position.getX(), position.getY());
    }

    /**
     * Orients this drawer towards the specified position. That means, if the drawer moves
     * forward, it will pass over that position. If the specified position is exactly the current
     * position of this drawer, its orientation doesn't change.
     *
     * @param x the x-coordinate of the position to orient this drawer towards
     * @param y the y-coordinate of the position to orient this drawer towards
     * @return this
     */
    public Logo orientTowards(double x, double y) {
        if (currentX == x && currentY == y) {
            return this; //don't reorient
        }

        double dx = x - currentX;
        double dy = y - currentY;

        setRads(Math.atan2(dy, dx));
        return this;
    }


    /**
     * A type of Logo which produces {@link Shape Shapes}.
     */
    public static class Shaper extends Logo {
        private final GeneralPath path;

        /**
         * Creates a landed Logo located at (0.0, 0.0) position, using the winding rule
         * defined by {@link GeneralPath#WIND_NON_ZERO} for generated shapes.
         *
         * @see GeneralPath#WIND_NON_ZERO
         */
        public Shaper() {
            this(GeneralPath.WIND_NON_ZERO);
        }

        /**
         * Creates a landed Logo located at (0.0, 0.0) position, using the specified winding rule
         * for generated shapes. Valid parameters are {@link GeneralPath#WIND_NON_ZERO}
         * and {@link GeneralPath#WIND_EVEN_ODD}.
         *
         * @see GeneralPath#WIND_NON_ZERO
         * @see GeneralPath#WIND_EVEN_ODD
         */
        public Shaper(int windingRule) {
            path = new GeneralPath(windingRule);
            path.moveTo(0.0, 0.0);
        }

        @Override
        protected void drawLine(double x1, double y1, double x2, double y2) {
            path.lineTo(x2, y2);
        }

        @Override
        protected void flyLine(double x1, double y1, double x2, double y2) {
            path.moveTo(x2, y2);
        }
        
        /**
         * Returns a new Shape representing whatever was drawn using this drawer.
         *
         * @return a new Shape representing whatever was drawn using this drawer
         */
        public Shape toShape() {
            return new GeneralPath(path);
        }
    }

    /**
     * A type of Logo which draws directly on a {@link Graphics} object.
     */
    public static class Drawer extends Logo {
        private final Graphics g;

        /**
         * Creates a landed Logo located at (0.0, 0.0) position, using the winding rule
         */
        public Drawer(Graphics g) {
            this.g = g;
        }

        @Override
        protected void drawLine(double x1, double y1, double x2, double y2) {
            g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
        }

        @Override
        protected void flyLine(double x1, double y1, double x2, double y2) {
        }
    }
}
