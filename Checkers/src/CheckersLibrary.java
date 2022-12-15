import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class CheckersLibrary extends JComponent implements MouseListener{
    JFrame frame = new JFrame("Checkers");
    Container content = frame.getContentPane();


    //========================================================================\\
    //====================== DECLARING GLOBAL VARIABLES ======================\\
    //========================================================================\\
    private final int[] tiles =
            {11,0,12,0,13,0,14,0,0,21,0,22,0,23,0,24,31,0,32,0,33,0,34,0,0,41,0,42,0,43,0,44,
                    51,0,52,0,53,0,54,0,0,61,0,62,0,63,0,64,71,0,72,0,73,0,74,0,0,81,0,82,0,83,0,84};

    private ArrayList<Checker> plr1Checkers = new ArrayList<>();
    private ArrayList<Checker> plr2Checkers = new ArrayList<>();

    int targetSquare = -1;
    int prevLoc =-1;
    private Checker curPiece = null;
    private ArrayList<Integer> moveLocations = new ArrayList<>();
    private ArrayList<Integer> removedMoveLocations = new ArrayList<>();
    private ArrayList<Integer> possibleJumpLocations = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> confirmedJumpLocations = new ArrayList<>(); // Jump Location, Remove Location
    private ArrayList<Integer> previousMove = new ArrayList<>(); // Trail

    boolean forceJump = true;
    boolean jumpFound = false;
    int oppLoc = -1;

    boolean forceMove = false;
    private ArrayList<ArrayList<Integer>> forceMoveLocations = new ArrayList<>(); // From, To (Double Jump)

    int turn = 2;


    //========================================================================\\
    //==================== MOUSE MANAGEMENT + TURN MANAGEMENT ================\\
    //========================================================================\\
    @Override
    public void mousePressed(MouseEvent me) {

        prevLoc = targetSquare;
        getClickLocation(me);

        // Forcing D-Jumps \\
        if(forceMove) {
            // Resetting Arrays \\
            moveLocations.clear();
            removedMoveLocations.clear();
            possibleJumpLocations.clear();

            // Going through all forced move locations (D-Jump) \\
            ArrayList<ArrayList<Integer>> copy = (ArrayList<ArrayList<Integer>>) forceMoveLocations.clone();
            for (ArrayList<Integer> pair : copy) {
                if (pair.get(0) == curPiece.getLoc()) {
                    if (targetSquare == pair.get(1)) {
                        placeChecker();
                    }else {
                        moveLocations.add(pair.get(1));
                    }
                }
            }

            targetSquare = curPiece.getLoc();
            repaint();

            return; // Break out of entire method
        }

        // If Not a Forced Move Case \\
        curPiece = getCheckerByLocation(targetSquare); // Will update curPiece var

        if(curPiece == null){
            // Place Checker
            placeChecker();
        }else {
            moveLocations.clear();
            removedMoveLocations.clear();
            possibleJumpLocations.clear();

            if(curPiece.getPlr() == turn) {
                getMoveLocations();
            }else{
                targetSquare = -1;
            }
        }

        repaint();

    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }


    //========================================================================\\
    //====================== SETTING UP THE GUI / GAMES ======================\\
    //========================================================================\\
    public void setUp(){
        frame.setSize(817, 840);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);

        this.addMouseListener(this);

        content.setBackground(Color.WHITE);
        content.add(this);


        // Set Up Checker Objects \\
        for(int i = 0; i < 12; i++){

            int x = i / 4 + 1;
            int y = i % 4 + 1;
            int num = Integer.parseInt(x + "" + y);
            //System.out.println(num);

            Checker e = new Checker(1);
            e.setLoc(num);

            plr1Checkers.add(e);

        }

        for(int i = 0; i < 12; i++){

            int x = i / 4 + 6;
            int y = i % 4 + 1;
            int num = Integer.parseInt(x + "" + y);
            //System.out.println(num);

            Checker e = new Checker(2);
            e.setLoc(num);

            plr2Checkers.add(e);

        }

    }



    //========================================================================\\
    //============================ GUI MANAGEMENT ============================\\
    //========================================================================\\
    @Override
    public void paintComponent(Graphics g){

        // Sending a function to draw the checkerboard \\
        drawBoard(g);

    }


    //========================================================================\\
    //=========================== DRAWING CHECKERBOARD =======================\\
    //========================================================================\\
    private void drawBoard(Graphics g){


        for(int i = 0; i <= 63; i++){

            if((i/8) % 2 == 0){
                if(i%2 == 0){

                    g.setColor(Color.WHITE);
                    g.fillRect((i%8)* 100, (i/8) * 100, 100, 100);

                    // Drawing Piece Outline \\
                    drawCheckerPieces(g,i);


                }else{
                    g.setColor(Color.BLACK);
                    g.fillRect((i%8)* 100, (i/8) * 100, 100, 100);
                }
            }else{
                if(i%2 == 0){
                    g.setColor(Color.BLACK);
                    g.fillRect((i%8)* 100, (i/8) * 100, 100, 100);
                }else{
                    g.setColor(Color.WHITE);
                    g.fillRect((i%8)* 100, (i/8) * 100, 100, 100);

                    drawCheckerPieces(g,i);
                }
            }

            g.setColor(Color.BLACK);
            g.drawString("" + tiles[i],(i%8)* 100, (i/8) * 100 + 10);


        }

    }


    //========================================================================\\
    //========================= GETTING CLICKED LOCATION =====================\\
    //========================================================================\\

    public void getClickLocation(MouseEvent me){
        /*
        EXPLANATION
        If you divide by 100.0 and floor it, gets you the left-most line number in a range of values
        With this, you have to see if that column is even or odd
        If it is even, that means the number cannot be 1 as black spaces are floored to 0 (evens)
        If it is odd, that means the number cannot be 0 as black spaces are floored to 1 (odds)

        The rest of it is just doing a calculation to find the square number it is referring to
        */

        int y = me.getY() / 100 + 1;
        int x = me.getX();
        boolean changed = false;

        if(y % 2 == 0){ // Even Row
            if(Math.floor(x / 100.0) % 2 == 1){
                x = x / 100 / 2 + 1;
                changed = true;
            }
        }else{ // Odd Row
            if(Math.floor(x / 100.0) % 2 == 0){
                x = x / 100 / 2 + 1;
                changed = true;
            }
        }

        // Setting Int \\
        if(changed){
            String temp = y + "" + x;
            targetSquare = Integer.parseInt(temp);
            changed = false;
        }
    }


    //========================================================================\\
    //======================== GETTING POSSIBLE MOVES ========================\\
    //========================================================================\\

    public void getMoveLocations(){

        moveLocations.clear();

        int row = targetSquare / 10;
        boolean evenRow = row %2 == 0;

        // Getting move possible move locations based on king or not \\
        if(curPiece.isKing()){

            // Even Row or Odd Row\\
            if(evenRow){
                moveLocations.add(targetSquare - 10);
                moveLocations.add(targetSquare - 10 + 1);
                moveLocations.add(targetSquare + 10);
                moveLocations.add(targetSquare + 10 + 1);
            }else{
                moveLocations.add(targetSquare - 10);
                moveLocations.add(targetSquare - 10 - 1);
                moveLocations.add(targetSquare + 10);
                moveLocations.add(targetSquare + 10 - 1);
            }

        }else{

            if(curPiece.getPlr() == 2) { // Need to know whose turn it is to see possible moves (nonkings can't move wrong way)
                // Even Row or Odd Row\\
                if (evenRow) {
                    moveLocations.add(targetSquare - 10);
                    moveLocations.add(targetSquare - 10 + 1);
                } else {
                    moveLocations.add(targetSquare - 10);
                    moveLocations.add(targetSquare - 10 - 1);
                }
            }else{
                // Even Row or Odd Row\\
                if (evenRow) {
                    moveLocations.add(targetSquare + 10);
                    moveLocations.add(targetSquare + 10 + 1);
                } else {
                    moveLocations.add(targetSquare + 10);
                    moveLocations.add(targetSquare + 10 - 1);
                }
            }

        }

        // Checking for user pieces at that location \\
        if(curPiece.getPlr() == 2){ // You
            for(Checker locCheck : plr2Checkers){
                for(int i = 0; i < moveLocations.size(); i++){
                    if(moveLocations.get(i) == locCheck.getLoc()){
                        moveLocations.remove(i);
                    }
                }
            }
        }else{ // Opp
            for(Checker locCheck : plr1Checkers){
                for(int i = 0; i < moveLocations.size(); i++){
                    if(moveLocations.get(i) == locCheck.getLoc()){
                        moveLocations.remove(i);
                    }
                }
            }
        }

        // Checking for opponent pieces at that location \\
        if(curPiece.getPlr() == 2) { // You
            for (Checker locCheck : plr1Checkers) {
                for (int i = 0; i < moveLocations.size(); i++) {
                    if (moveLocations.get(i) == locCheck.getLoc()) {
                        removedMoveLocations.add(moveLocations.remove(i)); // Inserting number into array
                    }
                }
            }
        }else{
            for (Checker locCheck : plr2Checkers){
                for (int i = 0; i < moveLocations.size(); i++){
                    if (moveLocations.get(i) == locCheck.getLoc()){
                        // Inserting number into array
                        removedMoveLocations.add(moveLocations.remove(i));
                    }
                }
            }
        }

        //  Checking if the pieces can be jumped \\
        checkForJumps();

        // Calling Force Jump Method if Enabled \\
        if(forceJump) {
            checkForForceJump();
        }
    }


    //========================================================================\\
    //======================== DRAWING CHECKER PIECES ========================\\
    //========================================================================\\

    public void drawCheckerPieces(Graphics g, int i){

        // Highlight Trail \\
        for(int loc : previousMove){
            if(loc == tiles[i]){
                g.setColor(Color.GREEN);
                g.fillRect((i%8)* 100, (i/8) * 100, 100, 100);
            }
        }

        // Show Move Locations \\
        for(int loc : moveLocations){
            if(loc == tiles[i]){
                g.setColor(Color.CYAN);
                g.fillRect((i%8)* 100, (i/8) * 100, 100, 100);
            }
        }

        // Drawing Pieces and Outline \\
        for(Checker piece : plr1Checkers){

            if(targetSquare == piece.getLoc() && tiles[i] == targetSquare){
                g.setColor(Color.YELLOW);
                g.fillRect((i%8)* 100, (i/8) * 100, 100, 100);
            }

            if(piece.getLoc() == tiles[i]){
                if(piece.isKing()){
                    g.setColor(Color.ORANGE);
                    g.fillOval((i%8)* 100 + 10, (i/8) * 100 + 10,80,80);

                    g.setColor(piece.getColor());
                    g.fillOval((i%8)* 100 + 15, (i/8) * 100 + 15,70,70);
                }else {
                    g.setColor(piece.getColor());
                    g.fillOval((i % 8) * 100 + 10, (i / 8) * 100 + 10, 80, 80);
                }
            }
        }

        for(Checker piece : plr2Checkers){
            if(targetSquare == piece.getLoc() && tiles[i] == targetSquare) {
                g.setColor(Color.YELLOW);
                g.fillRect((i % 8) * 100, (i / 8) * 100, 100, 100);
            }

            if(piece.getLoc() == tiles[i]){
                if(piece.isKing()){
                    g.setColor(Color.ORANGE);
                    g.fillOval((i%8)* 100 + 10, (i/8) * 100 + 10,80,80);

                    g.setColor(piece.getColor());
                    g.fillOval((i%8)* 100 + 15, (i/8) * 100 + 15,70,70);
                }else {
                    g.setColor(piece.getColor());
                    g.fillOval((i % 8) * 100 + 10, (i / 8) * 100 + 10, 80, 80);
                }
            }
        }
    }


    //========================================================================\\
    //======================= GET CHECKER BY LOCATIONS =======================\\
    //========================================================================\\

    public Checker getCheckerByLocation(int loc){

        Checker piece = null;

        for(Checker c : plr1Checkers){
            if(c.getLoc() == loc){
                piece = c;
            }
        }

        for(Checker c : plr2Checkers){
            if(c.getLoc() == loc){
                piece = c;
            }
        }
        return piece;
    }


    //========================================================================\\
    //============================ PLACE CHECKERS ============================\\
    //========================================================================\\

    public void placeChecker() {

        // Standard case \\
        boolean canMove = false;

        // Seeing if the location to move is valid \\
        for (int moveLoc : moveLocations) {
            if (Math.abs(targetSquare - moveLoc) < .1) { // Tolerance
                canMove = true;
            }
        }

        // Change What you are Looking At \\
        curPiece = getCheckerByLocation(prevLoc);

        // Seeing if target square is a move location \\
        boolean isAMove = false;
        for(int loc : moveLocations){
            if(targetSquare == loc){
                isAMove = true;
            }
        }
        // Returning if not a move \\
        if(!isAMove && !forceMove){
            moveLocations.clear();
            return;
        }

        // Moving Checker \\
        if (canMove || forceMove){
            // Changing Location \\
            curPiece.setLoc(targetSquare);

            // Clearing All Tables \\
            moveLocations.clear();
            removedMoveLocations.clear();
            possibleJumpLocations.clear();

            // Change King Status \\
            if(targetSquare / 10 == 1 || targetSquare / 10 == 8){
                if(!curPiece.isKing()){
                    curPiece.invertKing();
                }
            }

            // Seeing if jump was made -- Double jump + Piece Removal \\
            if(Math.abs(targetSquare /10 - prevLoc/10) == 2){

                // Removing Jumped Checker \\
                for(ArrayList<Integer> pair : confirmedJumpLocations){
                    if(pair.get(0) == curPiece.getLoc()){
                        oppLoc = pair.get(1);
                    }
                }
                for(int i = 0; i < plr1Checkers.size(); i++){
                    if(plr1Checkers.get(i).getLoc() == oppLoc){
                        plr1Checkers.remove(i);
                    }
                }
                for(int i = 0; i < plr2Checkers.size(); i++){
                    if(plr2Checkers.get(i).getLoc() == oppLoc){
                        plr2Checkers.remove(i);
                    }
                }

                // Double Jump Algorithms \\
                getMoveLocations();
                if(jumpFound){
                    prevLoc = curPiece.getLoc();
                    moveLocations.clear();
                    forceMove = true;
                    forceMoveLocations.clear();

                    // Adding to move locations \\
                    checkForJumps();

                    // Forcing That Location \\
                    for(int loc : moveLocations){
                        ArrayList<Integer> temp = new ArrayList<>();
                        temp.add(curPiece.getLoc());
                        temp.add(loc);

                        forceMoveLocations.add(temp);
                    }

                    repaint();
                }else{
                    moveLocations.clear();
                    forceMoveLocations.clear();
                    changeTurn();

                    // Manipulating Trail \\
                    previousMove.clear();
                    previousMove.add(prevLoc);
                    previousMove.add(curPiece.getLoc());

                    // Resetting Stats \\
                    targetSquare = -1; // Resetting selected checker
                    forceMove = false;
                }
            }else{
                forceMoveLocations.clear();
                changeTurn();

                // Manipulating Trail \\
                previousMove.clear();
                previousMove.add(prevLoc);
                previousMove.add(curPiece.getLoc());

                targetSquare = -1; // Resetting selected checker
                forceMove = false;
            }


        }
    }


    //========================================================================\\
    //======================== VALID LOCATION CHECKER ========================\\
    //========================================================================\\

    public boolean isValidLocation(int loc){

        if(loc / 10 < 1 || loc / 10 > 8 || loc %10 == 0 || loc %10 > 4){
            return false;
        }

        return true;

    }


    //========================================================================\\
    //============================ CHECK FOR JUMP ============================\\
    //========================================================================\\

    public boolean checkForJumps(){

        // Variable to see if jump found \\
        jumpFound = false;

        // Finding Piece Location \\
        int playerPieceLocation = curPiece.getLoc();

        // Adding Possible Jumps to Array \\
        possibleJumpLocations.add(playerPieceLocation + 19);
        possibleJumpLocations.add(playerPieceLocation + 21);
        possibleJumpLocations.add(playerPieceLocation - 21);
        possibleJumpLocations.add(playerPieceLocation - 19);

        //  Checking if the pieces can be jumped \\
        for(int loc : removedMoveLocations) {

            // Valid Jump Location Variable \\
            int validJumpLoc = -1;

            // Getting the row \\
            int row = loc / 10;

            // Going through all the possible jump locations \\
            for (int jumpLoc : possibleJumpLocations) {
                if (getCheckerByLocation(jumpLoc) == null) {
                    if (row % 2 == 0) { // EVEN

                        if (loc - 10 == jumpLoc || loc - 9 == jumpLoc || loc + 11 == jumpLoc || loc + 10 == jumpLoc) {
                            if (isValidLocation(jumpLoc)){
                                jumpFound = true;
                                addJump(jumpLoc, loc);
                            }
                        }

                    } else {
                        if (loc - 10 == jumpLoc || loc - 11 == jumpLoc || loc + 9 == jumpLoc || loc + 10 == jumpLoc) {
                            if (isValidLocation(jumpLoc)){
                                jumpFound = true;
                                addJump(jumpLoc, loc);
                            }
                        }
                    }
                }

            }
        }

        // Return Statement - Returns if a jump was found \\
        return jumpFound;
    }


    //========================================================================\\
    //========================= ADDING JUMP TO TABLE =========================\\
    //========================================================================\\

    public void addJump(int validJumpLoc, int loc){

        if(validJumpLoc != -1){

            if(!curPiece.isKing()){
                if(curPiece.getPlr() == 2){
                    // Piece must be going to a less number
                    if(validJumpLoc < curPiece.getLoc()){
                        moveLocations.add(validJumpLoc);

                        ArrayList<Integer> pair = new ArrayList<>();
                        pair.add(validJumpLoc);
                        pair.add(loc);
                        confirmedJumpLocations.add(pair);
                    }
                }else{
                    // Piece must be going to a bigger number
                    if(validJumpLoc > curPiece.getLoc()){
                        moveLocations.add(validJumpLoc);

                        ArrayList<Integer> pair = new ArrayList<>();
                        pair.add(validJumpLoc);
                        pair.add(loc);
                        confirmedJumpLocations.add(pair);
                    }
                }
            }else{
                moveLocations.add(validJumpLoc);

                ArrayList<Integer> pair = new ArrayList<>();
                pair.add(validJumpLoc);
                pair.add(loc);
                confirmedJumpLocations.add(pair);
            }

        }
    }


    //========================================================================\\
    //============================= CHANGE TURNS =============================\\
    //========================================================================\\

    public void changeTurn(){
        if(turn == 2){
            turn = 1;
        }else{
            turn = 2;
        }
    }


    //========================================================================\\
    //======================== FORCING JUMP ALGORITHM ========================\\
    //========================================================================\\

    public void checkForForceJump(){



    }


    //========================================================================\\
    //======================= WAIT TIMER (FOR AI PLAY) =======================\\
    //========================================================================\\

    public void wait(int seconds){
        int milliseconds = seconds * 1000;
        try{
            Thread.sleep(milliseconds);
        }catch(Exception e){

        }
    }

}

// Lines -- 384 \\
// (Location-x, Location-y, E-Location-x, E-Location-y) \\


// Rectangles -- 386 \\
// (Location-x, Location-y, Length, Width) \\


// Ovals/Circles -- 387 \\
// (Location-x, Location-y, Length, Width) \\

// String/Font -- 381 \\
// SetFont >> (Font,Bold/Italics,Size) \\
// DrawString >> (String,Location-x,Location-y) ><><><> BOTTOM LEFT

// Arcs -- 388 \\
// (Location-x, Location-y, Width, Height, Starting Point, Degrees to draw)