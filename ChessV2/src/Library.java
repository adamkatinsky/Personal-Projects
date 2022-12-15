import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JOptionPane;

// V2

public class Library {

    public static int plrMove = 1;


    // Takes a number from 1-8 and returns what letter it should be. \\
    // Requirements: Number must be 1-8 \\
    // Example: Sends: 4, Returns: D \\

    public char getLetter(int num){
        return (char)(num+64); // Convert to char and return
    }

    // Uses stats class to increment 50 move rules \\
    // Requirements: N/A || Used after a move is made \\
    public void increment50MoveRule(){
        if(Stats.halfMoves50MoveRule != 0){
            Stats.fullMoves50MoveRule++;
            Stats.halfMoves50MoveRule = 0;
        }else{
            Stats.halfMoves50MoveRule++;
        }
    }

    // Takes a number from 11-88 and returns what tile it is on. \\
    // Requirements: Last digit must be 1-8 || Not greater than 88, not less than 11\\
    // Example: Sends: 42, Returns: D7 \\ (Board Flipping) | (Location in terms of board ceilings) (400/200)

    public String numberToTile(int num){
        char letter = (char) (num/10 + 96); // Integer truncation
        num = num % 10;
        num = (num - 9) * -1;

        return letter + "" + num;
    }

    // Takes a letter and number input and returns what number it is \\
    // Requirements: Letter must be A-H || Number must  be 1-8 \\
    // Example: Sends: D7, Returns: 42 \\ (Board Flipping) | (Location in terms of board ceilings) (400/200)

    public int tileToNumber(String tile){
        char letter = tile.charAt(0);
        int numForLetter = (letter-64) * 10; // ASCII table
        return numForLetter + (Integer.parseInt(tile.substring(1))-9)*-1;
    }

    // Takes a number and all pieces array and returns the piece at the location \\
    // Requirements: Requirements: Last digit must be 1-8 || Not greater than 88, not less than 11 \\
    // Example: Sends: 11 / <Pieces>, Returns: Piece \\

    public Piece getPieceByTileNumber(int num, ArrayList<Piece> pieces){
        for(Piece piece : pieces){
            if(piece.getLoc() == num){
                return piece;
            }
        }

        return null;
    }

    // Runs after a move keeping PGN updated \\
    // Requirements: Piece that moved \\
    // Example: Sends: PieceMoved, pieceRemoved \\
    public void addMove(Piece piece, boolean pieceWasRemoved, boolean castle, String promoted,  int lastLoc){
        String pieceType = piece.getClass().getName();
        String identifier = pieceType.substring(0,1);

        // Getting Move \\
        if(pieceType.equals("Knight")){
            identifier = "N";
        }else if(pieceType.equals("Pawn")){
            if(pieceWasRemoved){
                identifier = numberToTile(lastLoc).substring(0,1);
            }else{
                identifier = "";
            }
        }

        String move = numberToTile(piece.getLoc());
        if(pieceWasRemoved){
            move = "x" + move;
        }

        //System.out.println(piece.getLoc());
        //System.out.println(identifier + move);

        // Castling isn't regular sign \\
        if(castle) {
            if(numberToTile(piece.getLoc()).charAt(0) == 'g') {
                Stats.pgnMoves.add("O-O");
            }else{
                Stats.pgnMoves.add("O-O-O");
            }
        }else if(promoted != null) {
            Stats.pgnMoves.add(identifier + move + "=" + promoted.charAt(0));
        }else{
            Stats.pgnMoves.add(identifier + move);
        }


        // OUTPUTTING PGN \\
        String pgn = "PGN: ";
        int index = 0;
        for(String str : Stats.pgnMoves){
            int num = index/2 + 1;

            if(index%2 == 0){
                pgn += num+". ";
            }

            pgn += str + " ";

            index++;
        }
        //System.out.print("\r" + pgn);
    }


    // Runs after a move keeping FEN updated \\
    // Requirements: Pieces Array \\
    // Example: Sends: Pieces Returns: "8/8/8/8/8/8/8/8" filled w/ piece initials \\
    public String getFEN(ArrayList<Piece> pieces){

        String fen = "";
        Piece[][] board = new Piece[8][8];

        // Creating Board \\
        for(Piece piece : pieces) {
            int loc = piece.getLoc();

            int x = loc/10;
            int y = loc%10;

            board[x-1][y-1] = piece;
        }

        // Creating FEN \\ (ROW BY ROW)
        for(int i = 0; i < 8; i ++){
            int count = 0;
            for(int v = 0; v < 8; v++){
                Piece piece = board[v][i];
                if(piece != null){

                    // Appending number
                    if(count >0){
                        fen += count;
                        count = 0;
                    }

                    // Getting Piece Index \\
                    int plr = piece.getPlr();
                    String pieceType = piece.getClass().getName();

                    String index = "";
                    if(pieceType.equals("Knight")){
                        index = "N";
                    }else{
                        index = pieceType.substring(0,1);
                    }

                    if(plr == 1){
                        index = index.toUpperCase(); // W
                    }else{
                        index = index.toLowerCase(); // B
                    }

                    fen += index;
                }else{
                    count++;
                }
            }

            // Append count \\
            if(count > 0){
                fen += count;
                count = 0;
            }

            // Add Slash \\
            if(i < 7) {
                fen += "/";
            }
        }

        // Plr Move \\
        if(plrMove == 1){
            fen += " w";
        }else{
            fen += " b";
        }

        // CASTLING RIGHTS \\
        String castle = "";

        // White \\
        Piece maybeWKing = board[4][7];
        King wKing = null;
        if(maybeWKing != null && maybeWKing.getClass().getName().equals("King")){
            wKing = (King) maybeWKing;
        }

        if(wKing != null && !wKing.getHasMoved()) {
            if (board[7][7] != null && board[7][7].getClass().getName().equals("Rook")) {
                Rook rook = (Rook) board[7][7];
                if (!rook.getHasMoved()){
                    castle += "K";
                }
            }
            if (board[7][0] != null && board[7][0].getClass().getName().equals("Rook")) {
                Rook rook = (Rook) board[7][0];
                if (!rook.getHasMoved()){
                    castle += "Q";
                }
            }
        }

        // Black \\
        Piece maybeBKing = board[4][0];
        King bKing = null;
        if(maybeBKing != null && maybeBKing.getClass().getName().equals("King")){
            bKing = (King) maybeBKing;
        }

        if(bKing != null && !bKing.getHasMoved()) {
            if (board[0][7] != null && board[0][7].getClass().getName().equals("Rook")) {
                Rook rook = (Rook) board[0][7];
                if (!rook.getHasMoved()){
                    castle += "k";
                }
            }
            if (board[0][0] != null && board[0][0].getClass().getName().equals("Rook")) {
                Rook rook = (Rook) board[0][0];
                if (!rook.getHasMoved()){
                    castle += "q";
                }
            }
        }

        fen += " " + castle;

        fen += String.format(" %d %d",Stats.halfMoves50MoveRule, Stats.fullMoves50MoveRule);

        System.out.println(fen);
        return fen;
    }


    // Runs at the start of game and creates an Array of all the Chess Pieces \\
    // Requirements: Run at the start of a game \\
    // Example: Returns: <Array of chess pieces> \\

    public ArrayList<Piece> starterBoard(){

        ArrayList<Piece> pieces = new ArrayList<>();

        // King \\
        pieces.add(new King(1,"E1"));
        pieces.add(new King(2,"E8"));

        // Queen \\
        pieces.add(new Queen(1, "D1"));
        pieces.add(new Queen(2,"D8"));

        // Bishop \\
        pieces.add(new Bishop(1,"C1"));
        pieces.add(new Bishop(1,"F1"));
        pieces.add(new Bishop(2,"C8"));
        pieces.add(new Bishop(2,"F8"));

        // Knight \\
        pieces.add(new Knight(1,"B1"));
        pieces.add(new Knight(1, "G1"));
        pieces.add(new Knight(2,"B8"));
        pieces.add(new Knight(2,"G8"));

        // Rook \\
        pieces.add(new Rook(1,"A1"));
        pieces.add(new Rook(1, "H1"));
        pieces.add(new Rook(2,"A8"));
        pieces.add(new Rook(2,"H8"));

        // Pawn \\
        pieces.add(new Pawn(1,"A2"));
        pieces.add(new Pawn(1, "B2"));
        pieces.add(new Pawn(1,"C2"));
        pieces.add(new Pawn(1,"D2"));
        pieces.add(new Pawn(1,"E2"));
        pieces.add(new Pawn(1, "F2"));
        pieces.add(new Pawn(1,"G2"));
        pieces.add(new Pawn(1,"H2"));

        pieces.add(new Pawn(2,"A7"));
        pieces.add(new Pawn(2, "B7"));
        pieces.add(new Pawn(2,"C7"));
        pieces.add(new Pawn(2,"D7"));
        pieces.add(new Pawn(2,"E7"));
        pieces.add(new Pawn(2, "F7"));
        pieces.add(new Pawn(2,"G7"));
        pieces.add(new Pawn(2,"H7"));

//        pieces.add(new Bishop(1, "E3"));
//        pieces.add(new Knight(1, "F3"));
//        pieces.add(new King(1, "D4"));
//        pieces.add(new King(2, "F5"));

        return pieces;
    }


    public ArrayList<Integer> removeCheckTiles(int location, Piece piece, ArrayList<Integer> moves, ArrayList<Piece> pieces){

        // New Arr \\
        ArrayList<Integer> newMoveArr = new ArrayList<>();
        ArrayList<Piece> newPieceArr = new ArrayList<>();

        newPieceArr.addAll(pieces);

        // Remove Moves when Check Still There \\ (After move) \\ (Can't be in move alg cause inf loop)
        for(int move : moves){


            // MOVE PIECE \\
            // Remove Piece \\
            if(getPieceByTileNumber(move, newPieceArr) != null){
                Piece pieceFound = getPieceByTileNumber(move, newPieceArr);
                newPieceArr.remove(pieceFound);
            }

            // Make Move \\
            piece.tempMovePiece(move);

            ArrayList<Piece> piecesThatCheck = isInCheck(newPieceArr, piece.getPlr());
            if(piecesThatCheck.size() == 0){
                newMoveArr.add(move);
            }

            // Fix \\
            piece.tempMovePiece(location);
            newPieceArr = new ArrayList<>();
            newPieceArr.addAll(pieces);
        }

        return newMoveArr;
    }


    // Checks to see if the plr sent through is in check \\
    // Requirements: Pieces array and plr, kings must be on board \\
    // Example: Returns: Pieces Array of Who Checks it \\

    public ArrayList<Piece> isInCheck(ArrayList<Piece> pieces, int plr){ // Plr is plr in check

        ArrayList<Piece> piecesWhoCheck = new ArrayList<>();

        // Find Vulnerable King \\
        King king = null;
        for(Piece piece : pieces){
            if(piece.getClass().getName().equals("King") && piece.getPlr() == plr){
                king = (King) piece;
                break;
            }
        }
        if(king == null){
            System.err.println("King not found for plr " + plr);
            return piecesWhoCheck;
        }

        // Vulnerable Location \\
        int vulnLoc = king.getLoc();

        // Going through all OPPONENT pieces and seeing if one sees location \\
        for(Piece piece : pieces){
            if(piece.getPlr() != plr) {
                ArrayList<Integer> moveLocs = piece.getMoveLocations(pieces, false);

                if (moveLocs.contains(vulnLoc)) {
                    piecesWhoCheck.add(piece);
                }
            }
        }

        return piecesWhoCheck;
    }

    // Checks to see if the pawn can promote and promotes pawn \\
    // Requirements: Pawn and array of pieces \\
    // Example: Returns: <Pieces> \\

    public String promotePawn(Pawn pawn, ArrayList<Piece> pieces, String promoteTo){
        if(pawn.getLoc() % 10 == 8 || pawn.getLoc() % 10 == 1){
            if(promoteTo == null){

                String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                int optionAnswer = JOptionPane.showOptionDialog(null, "Which piece would you like to promote to?",
                        "Click a button",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                promoteTo = options[optionAnswer];

            }

            // Add Other Piece \\
            switch (promoteTo) {
                case "Queen" -> pieces.add(new Queen(pawn.getPlr(), numberToTile(pawn.getLoc()).toUpperCase()));
                case "Rook" -> pieces.add(new Rook(pawn.getPlr(), numberToTile(pawn.getLoc()).toUpperCase()));
                case "Bishop" -> pieces.add(new Bishop(pawn.getPlr(), numberToTile(pawn.getLoc()).toUpperCase()));
                case "Knight" -> pieces.add(new Knight(pawn.getPlr(), numberToTile(pawn.getLoc()).toUpperCase()));
                default -> {
                    pieces.add(new Queen(pawn.getPlr(), numberToTile(pawn.getLoc())));
                    System.err.println("Promotion Defaulted");
                }
            }

            // Remove Pawn \\
            pieces.remove(pawn);
            return promoteTo;

        }

        return null;
    }


    // Checks for Pawn Diagonal Captures based on Move Direction \\
    // Requirements: Pawn, Direction to move, All Piece Array \\
    // Example: Returns: {15,16,17,18} (Continuation of previous array) \\

    public ArrayList<Integer> checkPawnCaptures(Piece piece, ArrayList<Piece> pieces, int location, ArrayList<Integer> movesToAdd, Piece.Direction dir){
        int newLocX = dir.getMoveDirection()[0] * 10 + location/10*10; // Integer math will truncate and then redo the times
        int newLocY = dir.getMoveDirection()[1] + location%10;

        int newLoc = newLocX + newLocY;

        Piece pieceOnSquare = getPieceByTileNumber(newLoc, pieces);
        if(pieceOnSquare != null && pieceOnSquare.getPlr() != piece.getPlr()){
            movesToAdd.add(newLoc);
        }

        return movesToAdd;
    }

    // Checks for moves based on type of piece special moves \\
    // Requirements: 2-digit number for location, both being 1-8. Pieces array \\
    // Example: Sends: 14, <Pieces> // Returns: {15,16,17,18} \\

    public ArrayList<Integer> checkSpecialMoves(Piece pieceToMove, ArrayList<Piece>  pieces, ArrayList<Integer> movesCanMake){
        ArrayList<Integer> movesToAdd = new ArrayList<>();

        int location = pieceToMove.getLoc();
        String type = pieceToMove.getClass().getName();

        // Switch for types \\
        switch (type){

            // Double forward + En Passant  + Captures  \\
            case "Pawn": {

                // DOUBLE FORWARD \\
                // Getting if pawn has moved \\
                Pawn pawn = (Pawn) pieceToMove;
                boolean hasMoved = pawn.getHasMoved();

                // Has Pawn Moved \\
                if (!hasMoved) {
                    if (location % 10 > 5) {
                        if(getPieceByTileNumber(location-1, pieces) == null){
                            if(getPieceByTileNumber(location-2, pieces) == null) {
                                movesToAdd.add(location - 2); // Move Twice
                            }
                        }
                    } else {
                        if(getPieceByTileNumber(location+1, pieces) == null){
                            if(getPieceByTileNumber(location+2, pieces) == null) {
                                movesToAdd.add(location + 2); // Move Twice
                            }
                        }
                    }
                }

                // PAWN CAPTURES \\
                if(Piece.Direction.N == pawn.moves[0]){
                    // NW | NE \\
                    movesToAdd = checkPawnCaptures(pieceToMove, pieces,location, movesToAdd, Piece.Direction.NE);
                    movesToAdd = checkPawnCaptures(pieceToMove, pieces,location, movesToAdd, Piece.Direction.NW);

                }else{
                    // SW | SE \\
                    movesToAdd = checkPawnCaptures(pieceToMove, pieces,location, movesToAdd, Piece.Direction.SE);
                    movesToAdd = checkPawnCaptures(pieceToMove, pieces,location, movesToAdd, Piece.Direction.SW);
                }

                // EN PASSANT \\

                break;
            }
            // Castling \\
            case "King": {
                // Getting if king has moved \\
                King king = (King) pieceToMove;
                boolean hasMoved = king.getHasMoved();

                // King hasn't moved \\
                if (!hasMoved) {
                    int rank = location%10;

                    // Rook 1
                    Piece piece1 = getPieceByTileNumber(10+rank, pieces);
                    if(piece1 != null && piece1.getClass().getName().equals("Rook")){
                        Rook rook = (Rook) piece1;

                        // Rook Hasn't Moved \\
                        if(!rook.getHasMoved()){
                            // No piece between \\
                            boolean pieceFound = false;
                            for(int i = location-10; i > 19; i-=10){
                                if(getPieceByTileNumber(i, pieces) != null){
                                    pieceFound = true;
                                    break;
                                }
                            }

                            // Check to see if going through check
                            boolean possibleMove = false;
                            for(int move : movesCanMake){
                                if (location - 10 == move) {
                                    possibleMove = true;
                                    break;
                                }
                            }

                            if(!pieceFound && possibleMove)
                                movesToAdd.add(location-20);
                        }
                    }

                    // Rook 2
                    Piece piece2 = getPieceByTileNumber(80+rank, pieces);
                    if(piece2 != null && piece2.getClass().getName().equals("Rook")){
                        Rook rook = (Rook) piece2;

                        // Rook Hasn't Moved \\
                        if(!rook.getHasMoved()){
                            // No piece between \\
                            boolean pieceFound = false;
                            for(int i = location+10; i < 79; i+=10){
                                if(getPieceByTileNumber(i, pieces) != null){
                                    pieceFound = true;
                                    break;
                                }
                            }

                            // Check to see if going through check
                            boolean possibleMove = false;
                            for(int move : movesCanMake){
                                if(location + 10 == move){
                                    possibleMove = true;
                                }
                            }

                            if(!pieceFound && possibleMove)
                                movesToAdd.add(location+20);
                        }
                    }
                }

                break;
            }

            // Castling
            case "Rook":
                break;
            default:
                break;
        }

        return movesToAdd;
    }


    // Checks for moves based on piece location and type of moves \\
    // Requirements: 2-digit number for location, both being 1-8. Pieces array, and moves table \\
    // Example: Sends: 14, <Pieces>, South, True // Returns: {15,16,17,18} \\

    public ArrayList<Integer> getMoveLocations(Piece pieceToMove, ArrayList<Piece> pieces, boolean isFirstIt){
        ArrayList<Integer> movesCanMake = new ArrayList<>();

        // Startup Vars \\
        int location = pieceToMove.getLoc();
        Piece.Direction[] moves = pieceToMove.getMoveDirections();
        boolean isMoveByDirection = pieceToMove.getMoveByDirection();

        int oldLoc = location;

        // For loop through possible cardinal directions \\
        for(Piece.Direction move : moves){

            // Change back to normal at the start of for loop \\
            location = oldLoc;

            // Vars \\
            int[] dir = move.getMoveDirection();
            int newLocX;
            int newLocY;

            // Do while \\
            do{

                newLocX = dir[0] * 10 + location/10*10; // Integer math will truncate and then redo the times
                newLocY = dir[1] + location%10;

                location = newLocX+newLocY;

                // Continue? Check \\
                if(getPieceByTileNumber(newLocX+newLocY, pieces) != null) { // Piece on square
                    Piece piece = getPieceByTileNumber(newLocX + newLocY, pieces);
                    if (piece.getPlr() == getPieceByTileNumber(oldLoc, pieces).getPlr()) {
                        // Same Color \\
                    } else {
                        // Diff Color \\
                        if(!getPieceByTileNumber(oldLoc, pieces).getClass().getName().equals("Pawn")){ // Pawns can't move forward if piece there
                            movesCanMake.add(newLocX + newLocY);
                        }
                    }
                    break;
                }else if(newLocX < 90 && newLocX > 9 && newLocY >=1 && newLocY<=8) { // Still in bounds check
                    movesCanMake.add(newLocX + newLocY);
                }else{ // Out of bounds \\
                    break;
                }
            }while(isMoveByDirection);

            // Add Special Moves Into Array \\
            // Remove checks first
            if(isFirstIt){
                movesCanMake = removeCheckTiles(oldLoc, pieceToMove, movesCanMake, pieces);
            }

            ArrayList<Integer> special = checkSpecialMoves(pieceToMove,pieces, movesCanMake);
            movesCanMake.addAll(special);

        }

        // Remove checks after everything too \\
        if(isFirstIt){
            movesCanMake = removeCheckTiles(oldLoc, pieceToMove, movesCanMake, pieces);
        }

        return movesCanMake;
    }


    public boolean makeMove(Piece piece, int targetSquare, ArrayList<Piece> pieces, String promoteTo) {
        ArrayList<Integer> moves = piece.getMoveLocations(pieces, true);

        // Remove Moves when Check Still There \\ (After move) \\ (Can't be in move alg cause inf loop)
        moves = removeCheckTiles(piece.getLoc(), piece, moves, pieces);

        int oldLoc = piece.getLoc();

        // Possible Move \\
        boolean moveMade =false;
        boolean pieceWasRemoved = false;
        if(moves.contains(targetSquare)){
            Piece pieceOnSquare = getPieceByTileNumber(targetSquare, pieces);
            if(pieceOnSquare != null){
                pieces.remove(pieceOnSquare);
                pieceWasRemoved = true;
                Stats.fullMoves50MoveRule = 0;
                Stats.halfMoves50MoveRule = 0;
            }

            // Make move \\
            piece.movePiece(targetSquare);
            moveMade = true;

            increment50MoveRule();
        }


        // SPECIAL CASES \\
        // Get Rook Move for Castle \\
        boolean didCastle = false;
        if(piece.getClass().getName().equals("King") && moveMade){
            if(Math.abs(piece.getLoc() - oldLoc) == 20){
                // Castle \\ Move Rook \\
                if(piece.getLoc() > oldLoc){
                    for(Piece pieceInArr : pieces){
                        if(pieceInArr.getClass().getName().equals("Rook")){
                            if(pieceInArr.getLoc() / 10 == 8 && pieceInArr.getPlr() == piece.getPlr()){
                                pieceInArr.movePiece(pieceInArr.getLoc()-20);
                            }
                        }
                    }
                }else{
                    for(Piece pieceInArr : pieces){
                        if(pieceInArr.getClass().getName().equals("Rook")){
                            if(pieceInArr.getLoc() / 10 == 1 && pieceInArr.getPlr() == piece.getPlr()){
                                pieceInArr.movePiece(pieceInArr.getLoc()+30);
                            }
                        }
                    }
                }
                didCastle = true;
            }
        }

        // Pawn Promotion \\
        String promotedTo = null;
        if(piece.getClass().getName().equals("Pawn") && moveMade){
            promotedTo = promotePawn((Pawn) piece, pieces, promoteTo);
            Stats.fullMoves50MoveRule = 0;
            Stats.halfMoves50MoveRule = 0;
        }

        // Add Move to Stats \\
        if(moveMade){
            addMove(piece, pieceWasRemoved, didCastle, promotedTo, oldLoc);
            plrMove = plrMove%2+1;
            return true;
        }
        return false;

    }

}
