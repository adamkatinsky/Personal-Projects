import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.awt.Font.*;

import java.io.*;
import java.util.Random;

public class SpaceInvadersV2 extends JComponent implements MouseListener,ActionListener {

    // GLOBAL VARIABLES \\
    private Timer tm = new Timer(10,this);

    private boolean[] alive = new boolean[36];

    private boolean aPressed = false;
    private boolean dPressed = false;
    private boolean wPressed = false;
    private boolean justChanged = false;
    private boolean pause = false;

    private boolean deb = false;

    private int posY = 500;

    private int nodeLX = 200; private int nodeMidX = 500; private int nodeRX = 800; private int nodeGun = 500;
    private int nodeHeight = 150;
    private int dCount = 0;
    private int fireCount = 100;
    private int score = 0;
    private int lives = 3;
    private int amtR = 0;
    private int limit;

    private int  nodeTarget1X, nodeTarget1Y, node1Hitbox;

    private bullet f1,f2,f3,f4,f5;
    private boolean f1b,f2b,f3b,f4b,f5b = false;
    private int f1f,f2f,f3f,f4f,f5f;

    private bulletEnemy eb1;
    private boolean b1 = false;
    private int fb1;
    boolean ranEnd = false;

    private String dir = "R";

    private String highScore = "";


    private JFrame frame = new JFrame("Space Invaders");
    private Container content = frame.getContentPane();

    public static void main(String[] args) {
        SpaceInvadersV2 newGui = new SpaceInvadersV2();
        newGui.setUp();
    }


    public void setUp() {
        frame.setSize(1000, 650);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        this.addMouseListener(this);

        content.setBackground(Color.BLACK);
        content.add(this);

        // Sets all spaces in table as false \\
        for (int i = 0; i < alive.length; i++) {
            alive[i] = false;
        }

        // Waits for key-inputs \\
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                //System.out.println(e.getKeyChar()+" Typed");
            }

            @Override
            public void keyPressed(KeyEvent e) {
                char key = e.getKeyChar();

                if (key == 'a' || key == 'A') {
                    if (!aPressed) {
                        aPressed = true;
                    }

                } else if (key == 'd' || key == 'D') {
                    if (!dPressed) {
                        dPressed = true;
                    }
                } else if (key == 'w' || key == 'W') {
                    if (!wPressed) {
                        wPressed = true;
                    }
                } else if (e.getKeyCode() == 37){
                    if(!aPressed){
                        aPressed = true;
                    }
                } else if (e.getKeyCode() == 38){
                    if(!wPressed){
                        wPressed = true;
                    }
                } else if (e.getKeyCode() == 39){
                    if(!dPressed){
                        dPressed = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                char key = e.getKeyChar();

                if (key == 'a' || key == 'A') {
                    if (aPressed) {
                        aPressed = false;
                    }

                } else if (key == 'd' || key == 'D') {
                    if (dPressed) {
                        dPressed = false;
                    }
                } else if (key == 'w' || key == 'W') {
                    if (wPressed) {
                        wPressed = false;
                    }
                }else if (e.getKeyCode() == 37){
                    if(aPressed){
                        aPressed = false;
                    }
                } else if (e.getKeyCode() == 38){
                    if(wPressed){
                        wPressed = false;
                    }
                } else if (e.getKeyCode() == 39){
                    if(dPressed){
                        dPressed = false;
                    }
                } else if (key == ' ') {
                    if (!pause) {
                        pause = true;
                    } else {
                        pause = false;
                    }
                }
            }
        });

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if(x>950 && x<990 && y > 5 && y < 40){
            CheckScore();
            System.exit(0);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

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


    @Override
    public void paintComponent(Graphics g) {
        // Getting High Score \\
        if(highScore == ""){
            highScore = getHighScore();
        }

        // TITLE \\
        g.setColor(Color.white);

        Font f = new Font(SERIF,0,35);
        g.setFont(f);
        g.drawString("SPACE INVADERS",5,35);
        // SCORE \\
        g.drawString("HIGHSCORE: "+highScore,500,600);

        g.drawString("STAGE: " + (amtR + 1),725,35);

        g.drawString("SCORE: " +score,450,35);

        g.setColor(Color.RED);
        Font m = new Font(SANS_SERIF,Font.BOLD,35);
        g.setFont(m);
        g.drawString("X",950,35);

        g.setColor(Color.green);

        Font l = new Font(SERIF,0,35);
        g.setFont(l);

        // ALIEN SCORECARD \\
        g.setColor(Color.white);
        drawScorecard(g,5,575);
        g.drawString(" = 10",50,597);

        g.setColor(Color.BLUE);
        drawScorecard(g,155,575);
        g.drawString(" = 20",200,597);

        g.setColor(Color.RED);
        drawScorecard(g,310,575);
        g.drawString(" = 30",355,597);

        g.setColor(Color.green);

        shooterLives(g);

        if(!pause){
            // CHECKING TO SEE WHAT ALIENS ARE ALIVE \\
            for(int i = 24; i < alive.length; i++){
                if(!alive[i]){
                    limit = 580;
                }
            }

            for(int i = 12; i < 23; i++){
                if(!alive[i]){
                    limit = 530;
                }
            }

            for(int i = 0; i < 11; i++){
                if(!alive[i]){
                    limit = 480;
                }
            }

            // RUNS GFX FOR ALIENS AND SHOOTER \\
            if(lives == 0 || nodeHeight > limit){
                // LOSING CASE \\
                Font r = new Font(SERIF,0,100);
                g.setFont(r);
                if(!ranEnd){
                    ranEnd = true;
                    CheckScore();
                }

                g.drawString("GAME OVER",200,200);
            }else{
                // SHOOTER \\
                g.fillRect(nodeGun - 25, posY, 50, 15);
                g.fillRect(nodeGun - 20, posY - 4, 40, 4);
                g.fillRect(nodeGun - 5, posY - 8, 10, 4);
                g.fillRect(nodeGun - 2, posY - 12, 4, 4);

                // NODES \\
                g.setColor(Color.white);

                //g.drawRect(nodeLX, nodeHeight, 5, 5);
                //g.drawRect(nodeMidX, nodeHeight, 5, 5);
                //g.drawRect(nodeRX, nodeHeight, 5, 5);

                // BULLETS \\
                g.setColor(Color.green);
                if (f1b) {
                    g.fillRect(f1f, f1.getLocationY(), 2, 10);

                    boolean broke = f1.move();
                    if (broke) {
                        f1b = false;
                    }
                }
                g.setColor(Color.white);
                if (b1) {
                    g.fillRect(fb1, eb1.getLocationY(), 2, 10);
                }

                // TARGET \\
                nodeTarget1X = nodeMidX - 50;
                nodeTarget1Y = nodeHeight;
                node1Hitbox = nodeTarget1Y - 15;

                drawEnemy(g,nodeMidX - 290,nodeHeight,0);
                drawEnemy(g,nodeMidX - 240,nodeHeight,1);
                drawEnemy(g,nodeMidX - 190,nodeHeight,2);
                drawEnemy(g,nodeMidX - 140,nodeHeight,3);
                drawEnemy(g,nodeMidX - 90,nodeHeight,4);
                drawEnemy(g,nodeMidX - 40,nodeHeight,5);
                drawEnemy(g,nodeMidX + 10,nodeHeight,6);
                drawEnemy(g,nodeMidX + 60,nodeHeight,7);
                drawEnemy(g,nodeMidX + 110,nodeHeight,8);
                drawEnemy(g,nodeMidX + 160,nodeHeight,9);
                drawEnemy(g,nodeMidX + 210,nodeHeight,10);
                drawEnemy(g,nodeMidX + 260,nodeHeight,11);


                g.setColor(Color.BLUE);
                drawEnemy(g,nodeMidX - 290,nodeHeight - 50,12);
                drawEnemy(g,nodeMidX - 240,nodeHeight - 50,13);
                drawEnemy(g,nodeMidX - 190,nodeHeight - 50,14);
                drawEnemy(g,nodeMidX - 140,nodeHeight - 50,15);
                drawEnemy(g,nodeMidX - 90,nodeHeight - 50,16);
                drawEnemy(g,nodeMidX - 40,nodeHeight - 50,17);
                drawEnemy(g,nodeMidX + 10,nodeHeight - 50,18);
                drawEnemy(g,nodeMidX + 60,nodeHeight - 50,19);
                drawEnemy(g,nodeMidX + 110,nodeHeight - 50,20);
                drawEnemy(g,nodeMidX + 160,nodeHeight - 50,21);
                drawEnemy(g,nodeMidX + 210,nodeHeight - 50,22);
                drawEnemy(g,nodeMidX + 260,nodeHeight - 50,23);


                g.setColor(Color.RED);
                drawEnemy(g,nodeMidX - 290,nodeHeight - 100,24);
                drawEnemy(g,nodeMidX - 240,nodeHeight - 100,25);
                drawEnemy(g,nodeMidX - 190,nodeHeight - 100,26);
                drawEnemy(g,nodeMidX - 140,nodeHeight - 100,27);
                drawEnemy(g,nodeMidX - 90,nodeHeight - 100,28);
                drawEnemy(g,nodeMidX - 40,nodeHeight - 100,29);
                drawEnemy(g,nodeMidX + 10,nodeHeight - 100,30);
                drawEnemy(g,nodeMidX + 60,nodeHeight - 100,31);
                drawEnemy(g,nodeMidX + 110,nodeHeight - 100,32);
                drawEnemy(g,nodeMidX + 160,nodeHeight - 100,33);
                drawEnemy(g,nodeMidX + 210,nodeHeight - 100,34);
                drawEnemy(g,nodeMidX + 260,nodeHeight - 100,35);


                // SHOOTER HIT? \\
                if (b1) {
                    if ((eb1.getLocationY() >= posY - 10 && eb1.getLocationY() <= posY + 10) && fb1 > nodeGun - 25 && fb1 < nodeGun + 25) {
                        b1 = false;
                        lives -= 1;
                    } else {
                        boolean broke = eb1.move();
                        if (!broke) {
                            b1 = false;
                        }
                    }
                } else {
                    Random rand = new Random();
                    int num = rand.nextInt(36);

                    if (!alive[num]) {
                        if(!alive[num % 11]){
                            num %=11;
                        }

                        if(num >= 24) {
                            num %= 12;
                            eb1 = new bulletEnemy(nodeHeight - 90);
                            b1 = true;
                            fb1 = nodeMidX -290 +(50*Math.abs(num));
                        }else if(num >= 12){
                            num %= 12;
                            eb1 = new bulletEnemy(nodeHeight - 40);
                            b1 = true;
                            fb1 = nodeMidX -290 +(50*Math.abs(num));
                        }else{
                            eb1 = new bulletEnemy(nodeHeight + 10);
                            b1 = true;
                            fb1 = nodeMidX -290 +(50*Math.abs(num));
                        }
                    }

                }
            }

            // DID YOU WIN? IF SO RESET \\
            if(score %720 == 0 && deb){
                for(int i = 0; i < alive.length; i++){
                    alive[i] = false;
                }

                amtR ++;
                nodeHeight = 150 + 20 * amtR;
                nodeMidX = 500;
                nodeLX = 200;
                nodeRX = 800;

                deb = false;

                if(lives < 9) {
                    lives++;
                }
            }
        }else{
            g.setColor(Color.RED);
            Font p = new Font(SERIF,0,100);
            g.setFont(p);
            g.drawString("PAUSED", 300,300);
        }

        tm.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(!pause) {
            // MOVING THE SHOOTER \\
            if (aPressed && dPressed) {

            } else if (aPressed) {
                if (nodeGun >= 50) {
                    nodeGun = nodeGun - 4;
                }
            } else if (dPressed) {
                if (nodeGun <= 931) {
                    nodeGun = nodeGun + 4;
                }
            }

            // MOVING NODES \\
            if (dir.equals("R")) {
                nodeLX = nodeLX + 1;
                nodeRX = nodeRX + 1;
                nodeMidX = nodeMidX + 1;
            } else if (dir.equals("L")) {
                nodeLX = nodeLX - 1;
                nodeRX = nodeRX - 1;
                nodeMidX = nodeMidX - 1;
            } else if (dir.equals("D")) {
                nodeHeight = nodeHeight + 2;
                if (nodeRX == 950) {
                    dir = "L";
                } else if (nodeLX == 50) {
                    dir = "R";
                }
            }

            // MOVING THE ALIENS \\
            if (nodeRX == 950 || nodeLX == 50) {
                if (!justChanged || dCount < 40) {
                    dir = "D";
                    dCount += 4;
                    justChanged = true;
                } else {
                    dCount = 0;
                    justChanged = false;
                }
            }

            // FIRING BULLETS \\
            if (wPressed) {
                if (!f1b) {
                    f1 = new bullet(posY - 12);
                    f1b = true;
                    f1f = nodeGun;
                }
            }
        }
        // REPAINT \\
        repaint();
    }

    public void drawEnemy(Graphics g, int nodeTarget1X, int nodeTarget1Y,int num){
        if (!alive[num]) {
            // DRAW ALIEN \\
            g.fillRect(nodeTarget1X,nodeTarget1Y+9,3,9);
            g.fillRect(nodeTarget1X + 3,nodeTarget1Y+6,3,6);
            g.fillRect(nodeTarget1X + 6,nodeTarget1Y + 3,3,15);
            g.fillRect(nodeTarget1X + 6,nodeTarget1Y - 3,3,3);
            g.fillRect(nodeTarget1X + 9,nodeTarget1Y,3,6);
            g.fillRect(nodeTarget1X + 9,nodeTarget1Y +9,3,6);
            g.fillRect(nodeTarget1X + 9,nodeTarget1Y +18, 6,3);
            g.fillRect(nodeTarget1X + 12,nodeTarget1Y + 3,9,12);
            g.fillRect(nodeTarget1X + 21,nodeTarget1Y,3,6);
            g.fillRect(nodeTarget1X + 21,nodeTarget1Y +9,3,6);
            g.fillRect(nodeTarget1X + 18,nodeTarget1Y + 18,6,3);
            g.fillRect(nodeTarget1X + 24,nodeTarget1Y -3,3,3);
            g.fillRect(nodeTarget1X + 24,nodeTarget1Y + 3,3,15);
            g.fillRect(nodeTarget1X + 27,nodeTarget1Y +6,3,6);
            g.fillRect(nodeTarget1X + 30,nodeTarget1Y +9,3,9);

            // DID BULLET HIT ALIEN? \\
            if (f1b) {
                if ((f1.getLocationY() <= 2 + (nodeTarget1Y + 14) && f1.getLocationY() >= -15 + (nodeTarget1Y + 14)) && nodeTarget1X < f1f && f1f < (nodeTarget1X + 30)) {
                    f1b = false;
                    alive[num] = true;

                    if (num >= 24) {
                        score+= 30;
                    }else if(num>= 12) {
                        score += 20;
                    }else{
                        score+= 10;
                    }
                    deb = true;
                }
            }
        }
    }

    public void drawScorecard(Graphics g, int nodeTarget1X, int nodeTarget1Y){
        // DRAW ALIEN SCORECARD \\
        g.fillRect(nodeTarget1X,nodeTarget1Y+9,3,9);
        g.fillRect(nodeTarget1X + 3,nodeTarget1Y+6,3,6);
        g.fillRect(nodeTarget1X + 6,nodeTarget1Y + 3,3,15);
        g.fillRect(nodeTarget1X + 6,nodeTarget1Y - 3,3,3);
        g.fillRect(nodeTarget1X + 9,nodeTarget1Y,3,6);
        g.fillRect(nodeTarget1X + 9,nodeTarget1Y +9,3,6);
        g.fillRect(nodeTarget1X + 9,nodeTarget1Y +18, 6,3);
        g.fillRect(nodeTarget1X + 12,nodeTarget1Y + 3,9,12);
        g.fillRect(nodeTarget1X + 21,nodeTarget1Y,3,6);
        g.fillRect(nodeTarget1X + 21,nodeTarget1Y +9,3,6);
        g.fillRect(nodeTarget1X + 18,nodeTarget1Y + 18,6,3);
        g.fillRect(nodeTarget1X + 24,nodeTarget1Y -3,3,3);
        g.fillRect(nodeTarget1X + 24,nodeTarget1Y + 3,3,15);
        g.fillRect(nodeTarget1X + 27,nodeTarget1Y +6,3,6);
        g.fillRect(nodeTarget1X + 30,nodeTarget1Y +9,3,9);
    }

    public void shooterLives(Graphics g){
        // SHOOTER LIVES \\
        g.fillRect(350 - 25, 20, 50, 15);
        g.fillRect(350 - 20, 20 - 4, 40, 4);
        g.fillRect(350 - 5, 20 - 8, 10, 4);
        g.fillRect(350 - 2, 20 - 12, 4, 4);

        g.setColor(Color.WHITE);
        g.drawString(" x " + lives,375,35);
        g.setColor(Color.GREEN);
    }

    public String getHighScore(){
        FileReader readFile = null;
        BufferedReader reader = null;
        try {
            readFile = new FileReader("highscore.dat");
            reader = new BufferedReader(readFile);
            return reader.readLine();
        }catch(Exception e){
            return "Nobody: 0";
        }finally {
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void CheckScore(){
        if(score > Integer.parseInt(highScore.split(": ")[1])){
            String name = "Unknown";
            try{
                name = JOptionPane.showInputDialog(null, "You have a high score! What is your Name?");
            }catch(Exception e){
                e.printStackTrace();
            }

            if(name != null){
                if(name.equals("")) {
                    name = "Unknown";
                }
            }else{
                name = "Unknown";
            }

            highScore = name+": "+score;
            File scoreFile = new File("highscore.dat");
            if (!scoreFile.exists()){
                try {
                    scoreFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileWriter writeFile = null;
            BufferedWriter writer = null;

            try{
                writeFile = new FileWriter(scoreFile);
                writer = new BufferedWriter(writeFile);
                writer.write(""+highScore);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try{
                    if(writer != null){
                        writer.close();
                    }
                }catch(Exception e){}
            }
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