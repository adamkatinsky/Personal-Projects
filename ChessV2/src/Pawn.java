import java.awt.*;
import java.util.ArrayList;

public class Pawn implements Piece {

    // Library \\
    Library library = new Library();

    // Moves Possible \\
    boolean moveByDirection = false; // Continual Moves // Recursion
    Direction[] moves = {
            Direction.S,
            Direction.N
    };

    // Vars \\
    private final int plr;
    private int locationOnBoard = 0;
    private boolean hasMoved = false;
    private boolean canEnPassent = false;

    private final String path;

    Pawn(int plr, String loc){
        this.plr = plr;
        this.locationOnBoard = library.tileToNumber(loc);

        this.path = String.format("%s-%d.png","pawn", plr); // File locations have 1/2s

        // Edit it for pawns \\ Only forward
        Direction[] moves = {this.moves[plr-1]};
        this.moves = moves;
    }

    // Setters \\
    @Override
    public void movePiece(int newLocation){
        locationOnBoard = newLocation;
        hasMoved = true;
    }

    @Override
    public void tempMovePiece(int newLocation) {
        locationOnBoard = newLocation;
    }

    public void setCanEnPassent(boolean newVal){
        canEnPassent = newVal;
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

    public boolean getCanEnPassent() { return this.canEnPassent;}

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
