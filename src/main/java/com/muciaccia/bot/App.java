package com.muciaccia.bot;

import com.muciaccia.bot.exception.QuitException;
import com.muciaccia.bot.front.CommandEnum;
import com.muciaccia.bot.front.InputParser;

import java.io.*;

public class App {
    private static final String PROMPT = "-> ";

    private static final InputStream INPUT_STREAM;
    private static final PrintStream PRINT_STREAM;
    private static final InputParser INPUT_PARSER;

    static {
        INPUT_STREAM = System.in;
        PRINT_STREAM = System.out;
        INPUT_PARSER = new InputParser(PRINT_STREAM);
    }

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(INPUT_STREAM));
        try {
            PRINT_STREAM.println("Caricamento completato. Digitare '" + CommandEnum.HELP_CMD.getValue() + "' per un riassunto dei comandi.");
            while (true) {
                PRINT_STREAM.print(PROMPT);
                String input = br.readLine();
                INPUT_PARSER.parseInput(input.trim());
            }
        } catch (QuitException e) {
            PRINT_STREAM.println("Spegnimento...");
        } catch (IOException e) {
            PRINT_STREAM.println("L'applicazione è terminata a causa di un errore.");
        }
    }
}
