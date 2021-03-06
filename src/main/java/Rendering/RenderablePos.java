package Rendering;

import Toolbox.HackMe;

import java.awt.*;

/**
 * Created by duckman on 24/05/2016.
 */
public class RenderablePos
{
    public enum ScalePosition
    {
        TopLeft(0,0), TopRight(0,1), LowerLeft(1,0), LowerRight(1,1),
        Centre(0.5, 0.5),
        Left(0, 0.5), Right(1, 0.5), Top (0.5, 0), Bottom(0.5, 1);

        //in object co-ordiante space
        double ocXOrigin;
        double ocYOrigin;

        ScalePosition(double ocXOrigin, double ocYOrigin)
        {
            this.ocXOrigin = ocXOrigin;
            this.ocYOrigin = ocYOrigin;
        }

        public final Rectangle scaleRectangle(Rectangle rec, double scale)
        {
            return scaleRectangle(rec, scale, scale);
        }

        public final Rectangle scaleRectangle(Rectangle rec, double sx, double sy)
        {
            //scaling width & height, scales the rec about the top left
            double newWidth = sx * rec.width;
            double newHeight = sy * rec.height;

            //now move the rectangles top left to a new position
            // using the "world space" co-ordinate origin.
            double wsXorigin = rec.x + (ocXOrigin * rec.width);
            double wsYorigin = rec.y + (ocYOrigin * rec.height);

            double newXpos = ((rec.x - wsXorigin) * sx) + wsXorigin;
            double newYpos = ((rec.y - wsYorigin) * sy) + wsYorigin;

            //return the new rectangle, im rounding given the context of this game.
            return new Rectangle((int)Math.round(newXpos), (int)Math.round(newYpos),
                                 (int)Math.round(newWidth), (int)Math.round(newHeight));
        }
    };

    final private int xPos;
    final private int yPos;
    final private double scale;
    final private ScalePosition scalePos;
    final private int depth;
    //TODO:
    //final private boolean mirror;

    private static final String xPosHackText = "pos x hack";
    private static final String yPosHackText = "pos y hack";

    transient volatile int simpleHash = 0;

    /**
     * Nudging things around to find where a sprite should sit
     */
    final private boolean hackMe;

    public RenderablePos(int xPos, int yPos, double scale, ScalePosition scalePos, int depth)
    {
        this(xPos, yPos, scale, scalePos, depth, false);
    }

    public RenderablePos(int xPos, int yPos, double scale, ScalePosition scalePos, int depth, boolean hackMe) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.scale = scale;
        this.scalePos = scalePos;
        this.depth = depth;
        this.hackMe = hackMe;
    }

    protected RenderablePos(int xPos, int yPos, double scale, ScalePosition scalePos, int depth, boolean hackMe, int simpleHash) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.scale = scale;
        this.scalePos = scalePos;
        this.depth = depth;
        this.hackMe = hackMe;
        this.simpleHash = simpleHash;

//        if(hackMe)
//        {
//            //foobar
//            updateHash(xPos, yPos);
//        }
    }

    public RenderablePos(int xPos, int yPos, double scale, int depth) {
        this(xPos, yPos, scale, ScalePosition.Centre, depth);
    }

    public RenderablePos(RenderablePos other) {
        this.xPos = other.xPos;
        this.yPos = other.yPos;
        this.scale = other.scale;
        this.scalePos = other.scalePos;
        this.depth = other.depth;
        hackMe = other.hackMe;
        simpleHash = other.simpleHash;
    }

    public RenderablePos hackMe() {
        return new RenderablePos(xPos, yPos, scale,scalePos,depth, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RenderablePos that = (RenderablePos) o;

        if (xPos != that.xPos) return false;
        if (yPos != that.yPos) return false;
        if (Double.compare(that.scale, scale) != 0) return false;
        if (depth != that.depth) return false;
        return scalePos == that.scalePos;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = xPos;
        result = 31 * result + yPos;
        temp = Double.doubleToLongBits(scale);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (scalePos != null ? scalePos.hashCode() : 0);
        result = 31 * result + depth;
        return result;
    }

    @Override
    public String toString() {
        return "RenderablePos(" +
                "xPos=" + xPos +
                ", yPos=" + yPos +
                ", scale=" + scale +
                ", scalePos=" + scalePos +
                ", depth=" + depth +
                ')';
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    //------------------------------------------------------------------------------------------------------------------
    public RenderablePos above() {
        return new RenderablePos(xPos, yPos, scale, scalePos, depth+1, hackMe, simpleHash);
    }

    public RenderablePos below() {
        return new RenderablePos(xPos, yPos, scale, scalePos, depth-1, hackMe, simpleHash);
    }

    public RenderablePos atDepth(int newDepth) {
        return new RenderablePos(xPos, yPos, scale, scalePos, newDepth, hackMe, simpleHash);
    }

    public RenderablePos translate(int x, int y) {
        return new RenderablePos(xPos+x, yPos+y, scale, scalePos, depth-1, hackMe, simpleHash);
    }

    public RenderablePos scaleLocationOnly(double sx, double sy) {
        return new RenderablePos((int)(xPos*sx), (int)(yPos*sy), scale, scalePos, depth-1, hackMe, simpleHash);
    }

    public RenderablePos scaleScaleOnly(double scaleFactor) {
        return new RenderablePos(xPos, yPos, scale*scaleFactor, scalePos, depth-1, hackMe, simpleHash);
    }

    //------------------------------------------------------------------------------------------------------------------
    // Getters and setters
    //------------------------------------------------------------------------------------------------------------------
    public int getxPos() {
        if(hackMe){
            // Hash the hacked pos, print output if something changed.
            // Then use the output to alter the code so the sprite is in
            // the correct position.
            int posXHack = xPos + HackMe.GlobalInstance.getHack(xPosHackText);
            int posYHack = yPos + HackMe.GlobalInstance.getHack(yPosHackText);
            updateHash(posXHack, posYHack);
            return posXHack;
        }
        return xPos;
    }

    public int getyPos() {
        if(hackMe){
            // Hash the hacked pos, print output if something changed.
            // Then use the output to alter the code so the sprite is in
            // the correct position.
            int posXHack = xPos + HackMe.GlobalInstance.getHack(xPosHackText);
            int posYHack = yPos + HackMe.GlobalInstance.getHack(yPosHackText);
            updateHash(posXHack, posYHack);
            return posYHack;
        }
        return yPos;
    }

    public double getScale() {
        return scale;
    }

    public ScalePosition getScalePos() {
        return scalePos;
    }

    public int getDepth() {
        return depth;
    }

    public Rectangle getImageBounds(int width, int height)
    {
        //this was not scaled before
        //TODO: use scalePos
        return  new Rectangle(getxPos(), getyPos(), (int)(width*scale), (int)(height*scale));

        //old version
        //return  new Rectangle(getxPos(), getyPos(), width, height);
    }

    public Rectangle getImageBoundsAfterOffset(int x, int y, double width, double height) {
        return  new Rectangle(getxPos(), getyPos(), (int)width, (int)height);
        //return  new Rectangle(x+xPos, y+yPos, (int)width, (int)height);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Hacks to move items around
    //------------------------------------------------------------------------------------------------------------------
    protected void updateHash(int posXHack, int posYHack) {
        int hash = posXHack *1000 + posYHack;
        if(hash != simpleHash)
        {
            System.out.println("Renderable pos hacked to " + posXHack + ", " + posYHack);
            simpleHash = hash;
        }
    }
}
