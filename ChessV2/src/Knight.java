import java.awt.*;
import java.util.ArrayList;

public class Knight implements Piece {

    // Library \\
    Library library = new Library();

    // Moves Possible \\
    boolean moveByDirection = false; // Continual Moves // Recursion
    Direction[] moves = {
            Direction.NNE,
            Direction.NNW,
            Direction.SSE,
            Direction.SSW,
            Direction.SEE,
            Direction.NEE,
            Direction.SWW,
            Direction.NWW,
    };

    // Vars \\
    private final int plr;
    private int locationOnBoard = 0;

    private final String path;

    Knight(int plr, String loc){
        this.plr = plr;
        this.locationOnBoard = library.tileToNumber(loc);

        this.path = String.format("%s-%d.png","knight", plr); // File locations have 1/2s
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
