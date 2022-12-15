import java.util.ArrayList;

public interface Piece {

   enum Direction{
        N(0,1),
        S(0,-1),
        E(1,0),
        W(-1,0),
        NE(1,1),
        SE(1,-1),
        NW(-1,1),
        SW(-1,-1),

        // KNIGHTS MOVES //
        NNE(1,2),
        NEE(2,1),
        SEE(2,-1),
        SSE(1,-2),
        SSW(-1,-2),
        SWW(-2,-1),
        NWW(-2,1),
        NNW(-1,2);



        private int[] moveDirection = new int[2];
        private int location = 0;

        Direction(int horiz, int vert){
            this.moveDirection[0] = horiz;
            this.moveDirection[1] = vert;
        }

        public int[] getMoveDirection() {
           return moveDirection;
        }

   }

    ArrayList<Integer> getMoveLocations(ArrayList<Piece> allPieces, boolean isFirstIt);
    int getLoc();
    int getPlr();
    void tempMovePiece(int newLocation);
    void movePiece(int newLocation);
    String getPath();
    Direction[] getMoveDirections();
    boolean getMoveByDirection();


}
