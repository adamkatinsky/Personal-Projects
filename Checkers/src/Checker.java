import java.awt.Color;

public class Checker {

    // Declaring Variables \\
    private int plr = 1;
    private boolean king = false;
    private boolean dirUp;
    private int locationOnBoard = 0;
    private Color color;
    private boolean inverted = false;

    public Checker(int plrNum){

        plr = plrNum;
        boolean dirUp = 1 == plrNum;

        if(plrNum == 2){
            color = Color.BLUE;
        }else{
            color = Color.RED;
        }

    }

    public boolean isKing(){
        return king;
    }

    public boolean isGoingUp(){
        return dirUp;
    }

    public int getLoc(){
        return locationOnBoard;
    }

    public void setLoc(int loc){
        locationOnBoard = loc;
    }

    public Color getColor(){
        return color;
    }

    public int getPlr(){
        return plr;
    }

    public void invertKing() {
        king = !king;
        inverted = !inverted;
        /*
        if (plr == 2) {
            if (king) {
                color = new Color(0, 0, 150);
            } else {
                color = Color.BLUE;
            }
        } else {
            if (king) {
                color = new Color(150, 0, 0);
            } else {
                color = Color.RED;
            }
        }*/
    }

    public boolean isInverted(){
        return inverted;
    }

}