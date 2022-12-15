import java.io.*;
import java.util.Scanner;

/**
 * A simple and efficient client to run Stockfish from Java
 *
 * @author Rahul A R
 *
 */
public class Stockfish {

    private Process engineProcess;
    BufferedWriter processWriter;
    private BufferedReader processReader;

    private static String PATH = System.getProperty("user.dir") + "\\src\\Engine\\Stockfish.exe";

    /**
     * Starts Stockfish engine as a process and initializes it
     *
     * @param //None
     * @return True on success. False otherwise
     */
    public boolean startEngine() {
        try {
            ProcessBuilder build = new ProcessBuilder("cmd", "/c", PATH);

            engineProcess = build.start();
            processWriter = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Takes in any valid UCI command and executes it
     *
     * @param command
     */
    public void sendCommand(String command) {
        try {
            command += "\n";
            processWriter = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));
            //System.out.print("WRITING COMMAND: " + command);
            processWriter.write(command);
            processWriter.flush();
            //processWriter3.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is generally called right after 'sendCommand' for getting the raw
     * output from Stockfish
     *
     * @param waitTime
     *            Time in milliseconds for which the function waits before
     *            reading the output. Useful when a long running command is
     *            executed
     * @return Raw output from Stockfish
     */
    public String getOutput(int waitTime) {
        StringBuffer buffer = new StringBuffer();
        try {
            Thread.sleep((long) (waitTime *1.2));
            sendCommand("isready");
            String text = "";
            String lastText = "";

            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));

            Scanner scanner = new Scanner(engineProcess.getInputStream());
            while (scanner.hasNextLine()) {
                text = scanner.nextLine();
                //System.out.println(text);

                if (text.contains("readyok")) {
                    if(lastText.contains("bestmove")) {
                        return lastText;
                    }
                }else if(text.contains("bestmove")) {
                    return text;
                }else{
                    lastText = text;
                }
            }

            return "(none)";
        } catch (Exception e) {
            System.out.println("ERR");
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * This function returns the best move for a given position after
     * calculating for 'waitTime' ms
     *
     * @param fen
     *            Position string
     * @param waitTime
     *            in milliseconds
     * @return Best Move in PGN format
     */
    public String getBestMove(String fen, int waitTime) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);
        return getOutput(waitTime + 20).split("bestmove ")[1].split(" ")[0];
    }

    /**
     * Stops Stockfish and cleans up before closing it
     */
    public void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
        } catch (IOException e) {
        }
    }

    /**
     * Get a list of all legal moves from the given position
     *
     * @param fen
     *            Position string
     * @return String of moves
     */
    public String getLegalMoves(String fen) {
        sendCommand("position fen " + fen);
        return getOutput(0).split("Legal moves: ")[1];
    }

    /**
     * Draws the current state of the chess board
     *
     * @param fen
     *            Position string
     */
    public void drawBoard(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("d");

        String[] rows = getOutput(0).split("\n");

        for (int i = 1; i < 18; i++) {
            System.out.println(rows[i]);
        }
    }

    /**
     * Get the evaluation score of a given board position
     * @param fen Position string
     * @param waitTime in milliseconds
     * @return evalScore
     */
    public float getEvalScore(String fen, int waitTime) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        float evalScore = 0.0f;
        String[] dump = getOutput(waitTime + 20).split("\n");
        for (int i = dump.length - 1; i >= 0; i--) {
            if (dump[i].startsWith("info depth ")) {
                try {
                    evalScore = Float.parseFloat(dump[i].split("score cp ")[1]
                            .split(" nodes")[0]);
                } catch(Exception e) {
                    evalScore = Float.parseFloat(dump[i].split("score cp ")[1]
                            .split(" upperbound nodes")[0]);
                }
            }
        }
        return evalScore/100;
    }
}



