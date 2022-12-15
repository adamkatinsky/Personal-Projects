import java.awt.*;
import java.util.ArrayList;

public class Bishop implements Piece {

    // Library \\
    Library library = new Library();

    // Moves Possible \\
    public boolean moveByDirection = true; // Continual Moves // Recursion
    Direction[] moves = {
            Direction.NE,
            Direction.SE,
            Direction.NW,
            Direction.SW,
    };

    // Vars \\
    private final int plr;
    private int locationOnBoard;

    private final String path;

    Bishop(int plr, String loc){
        this.plr = plr;
        this.locationOnBoard = library.tileToNumber(loc);

        this.path = String.format("%s-%d.png","bishop", plr); // File locations have 1/2s
    }

    // Setters \\
    @Override
    public void movePiece(int newLocation) {
        locationOnBoard = newLocation;
    }

    @Override
    public void tempMovePiece(int newLocation) {
        locationOnBoard = newLocation;
    }


    // Getters \\
    @Override
    public int getPlr(){
        return this.plr;
    }

    @Override
    public int getLoc(){
        return this.locationOnBoard;
    }

    @Override
    public String getPath(){
        return this.path;
    }

    // Get All Move Locs \\
    @Override
    public ArrayList<Integer> getMoveLocations(ArrayList<Piece> allPieces, boolean isFirstIt) {
        return library.getMoveLocations(this, allPieces, isFirstIt);
    }

    @Override
    public Direction[] getMoveDirections(){
        return moves;
    }

    @Override
    public boolean getMoveByDirection(){return moveByDirection;}
}
