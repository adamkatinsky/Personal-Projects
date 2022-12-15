import java.awt.*;
import java.util.ArrayList;

public class Rook implements Piece {

    // Library \\
    Library library = new Library();

    // Moves Possible \\
    boolean moveByDirection = true; // Continual Moves // Recursion
    Direction[] moves = {
            Direction.N,
            Direction.S,
            Direction.E,
            Direction.W,
    };

    // Vars \\
    private final int plr;
    private int locationOnBoard = 0;
    private boolean hasMoved = false;

    private final String path;

    Rook(int plr, String loc){
        this.plr = plr;
        this.locationOnBoard = library.tileToNumber(loc);

        this.path = String.format("%s-%d.png","rook", plr); // File locations have 1/2s
    }

    // Setters \\
    @Override
    public void movePiece(int newLocation) {
        locationOnBoard = newLocation;
        hasMoved = true;
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

    public boolean getHasMoved(){
        return this.hasMoved;
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
