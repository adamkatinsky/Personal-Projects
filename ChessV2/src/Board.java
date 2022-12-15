// Imports \\
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.*;

public class Board extends JComponent implements MouseListener{

    // FRAME // DISPLAY SETUP //
    JFrame frame = new JFrame("CHESS");
    Container content = frame.getContentPane();

    // VARS \\
    Color background1 = new Color(240, 217, 181);
    Color background2 = new Color(199, 150, 109);

    String chessPieceIconPath = System.getProperty("user.dir") + "\\src\\ChessIcons\\"; // System.getProperty gets the User

    // GAME MANAGEMENT \\
    ArrayList<Piece> pieces = new ArrayList<>();

    int targetSquare = -1;
    Piece pieceSelected = null;
    ArrayList<Integer> moveTiles = new ArrayList<>();

    // Library \\
    Library library = new Library();
    AI client = new AI();
    boolean ai = false;
    boolean aiTurn = false;
    boolean cpuGame = false;

    // USER EVENT //
    @Override
    public void mousePressed(MouseEvent e) {
        if (aiTurn) return; // Dont let plr move

        // Get Coords \\
        int x = e.getX();
        char letter = library.getLetter(x/100+1); // +1 bc of int division (truncation)
        int y = e.getY();

        int square = (x/100+1)*10 + y/100+1;

        // Setting Target Square \\
        // Second click \\
        if(pieceSelected != null){
            // Currently, on piece \\
            if(square == pieceSelected.getLoc()){


                // Reset \\
                targetSquare = -1;
                pieceSelected = null;

                // Repaint \\
                repaint();
                return;
            }else{
                // Possible move location \\
                Piece pieceOnSquare = library.getPieceByTileNumber(square, pieces);
                if (pieceOnSquare == null || pieceOnSquare.getPlr() != pieceSelected.getPlr()) {
                    // Did not click own piece \\
                    boolean moveMade = library.makeMove(pieceSelected, square, pieces, null);

                    // Reset \\
                    pieceSelected = null;
                    targetSquare = -1;

                    // Repaint \\
                    repaint();

                    if(moveMade && ai)
                        aiTurn = true;

                    return;
                }

            }
        }

        // First Click \\
        targetSquare = square; // Integer Math (truncation)
        pieceSelected = library.getPieceByTileNumber(square, pieces); // Returns null if no piece found

        // Reset if wrong player \\
        if(pieceSelected != null && pieceSelected.getPlr() != library.plrMove){
            targetSquare = -1;
            pieceSelected = null;
        }

        // Get Tile \\
        //String tile = String.format("%s%d",letter, (y/100+1) * -1+9); // -1+9 bc of board flip (notation)
        //System.out.println(tile + " " + targetSquare);

        repaint();
    }

    // START GAME //
    public void startGame(boolean isAI, boolean isCPUGame) {

        // Initialize Background //
        frame.setSize(817, 840);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.setAlwaysOnTop(false);

        this.addMouseListener(this);

        content.setBackground(Color.WHITE);
        content.add(this);

        pieces = library.starterBoard();

        ai = isAI;
        cpuGame = isCPUGame;
        aiTurn = cpuGame;

        if(isAI) {
            client.startUp();
            // Initialize AI // With Debounce \\
            boolean deb = false;
            new Thread(() -> { // Opens new program
                while (true){
                    try {
                        Thread.sleep(1000);

                        if(!deb && aiTurn){
                            client.getBestMove(pieces);
                            aiTurn = isCPUGame;
                            repaint();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        repaint();
    }

    // GUI MANAGEMENT //

    public void drawBoard(Graphics g){

        ArrayList<Piece> piecesThatCheck = library.isInCheck(pieces, library.plrMove);

        for(int i = 0; i <= 63; i++) {

            // Get Background Color \\
            if ((i / 8) % 2 == 0) {
                if (i % 2 == 0) {
                    g.setColor(background1);
                } else {
                    g.setColor(background2);
                }
            } else {
                if (i % 2 == 0) {
                    g.setColor(background2);
                } else {
                    g.setColor(background1);
                }
            }

            // Draw Box \\
            g.fillRect((i % 8) * 100, (i / 8) * 100, 100, 100);

            // Draw Outline \\
            g.setColor(Color.black);
            g.drawRect((i % 8) * 100, (i / 8) * 100, 100, 100);

        }

        // Highlight Check Squares (RED)  // BEFORE MOVE LOCS!!!
        for(Piece piece : pieces){

            // Vars \\
            int x = piece.getLoc()/10;
            int y = piece.getLoc()%10;

            // Highlight red if piece is giving check \\
            if (piecesThatCheck.contains(piece)) {
                // Make Box Red \\
                g.setColor(Color.RED);
                g.fillRect((x - 1) * 100, (y - 1) * 100, 100, 100);
                g.setColor(Color.black);
                g.drawRect((x - 1) * 100, (y - 1) * 100, 100, 100);
            } else if (piece.getClass().getName().equals("King") && piecesThatCheck.size() > 0) {
                // Do I highlight the king red?? \\
                if (piecesThatCheck.get(0).getPlr() != piece.getPlr()) {
                    // Yes \\
                    g.setColor(Color.RED);
                    g.fillRect((x - 1) * 100, (y - 1) * 100, 100, 100);
                    g.setColor(Color.black);
                    g.drawRect((x - 1) * 100, (y - 1) * 100, 100, 100);
                }
            }
        }

        // Highlight Target Squares \\
        if(targetSquare != -1){
            // Get Piece \\
            Piece piece = library.getPieceByTileNumber(targetSquare, pieces);
            if(piece != null){
                // Specific Piece \\
                int x = targetSquare/10;
                int y = targetSquare%10;

                g.setColor(Color.pink);
                g.fillRect((x-1)*100, (y-1)*100, 100,100);
                g.setColor(Color.black);
                g.drawRect((x-1)*100, (y-1)*100, 100,100);

                // Move Locs \\
                moveTiles = piece.getMoveLocations(pieces, true);

                for(int loc : moveTiles){
                    // Specific Piece \\
                    x = loc/10;
                    y = loc%10;

                    g.setColor(Color.cyan);
                    g.fillRect((x-1)*100, (y-1)*100, 100,100);
                    g.setColor(Color.black);
                    g.drawRect((x-1)*100, (y-1)*100, 100,100);
                }
            }else{
                targetSquare = -1; // Turn back normal
            }
        }

        // Display Pieces on Board \\
        for(Piece piece : pieces){

            // Piece Img \\
            File file = new File(chessPieceIconPath + piece.getPath());

            // Vars \\
            int x = piece.getLoc()/10;
            int y = piece.getLoc()%10;

            // Display Img \\
            Image img;
            try {
                img = ImageIO.read(file);
                img = img.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                g.drawImage(img, (x-1) * 100+5, (y-1) * 100+5, null, null); // Math for board geometry
            } catch (IOException e) {
                System.err.println("Image Failed to Load: "+ e);
            }
        }

    }


    @Override
    public void paintComponent(Graphics g){
        drawBoard(g);
        //drawPieces(g);

    }


    // UNUSED MOUSE EVENTS //
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
