import java.util.ArrayList;
import java.util.Locale;

public class AI {

    Stockfish client;
    Library lib = new Library();
    int waitTime = 100;

    public void startUp(){
        client = new Stockfish();
    }

    public void getBestMove(ArrayList<Piece> pieces){

        client.startEngine();
        client.sendCommand("ucinewgame");
        String move = client.getBestMove(lib.getFEN(pieces), waitTime);

        String tile = move.substring(0,2);
        String tileTo = move.substring(2);

        if(move.equals("(none)")){
            System.out.println("Game Over");
            return;
        }

        // Check if promotion specialty \\
        boolean promotion = false;
        String promoteTo = null;

        if(tileTo.length() == 3){
            promotion = true;
            tileTo = tileTo.substring(0,2);
        }

        if(promotion){
            String pieceLetter = move.substring(4);

            switch (pieceLetter.toUpperCase()) {
                case "Q" -> promoteTo = "Queen";
                case "B" -> promoteTo = "Bishop";
                case "R" -> promoteTo = "Rook";
                case "N" -> promoteTo = "Knight";
                default -> {
                }
            }
        }

        Piece pieceToMove = lib.getPieceByTileNumber(lib.tileToNumber(tile.toUpperCase()), pieces);
        int targetSquare = lib.tileToNumber(tileTo.toUpperCase());

        lib.makeMove(pieceToMove, targetSquare, pieces, promoteTo);
    }

}
