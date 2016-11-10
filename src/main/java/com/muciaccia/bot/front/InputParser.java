package com.muciaccia.bot.front;

import com.muciaccia.bot.exception.QuitException;
import com.muciaccia.bot.pojo.Person;
import com.muciaccia.bot.pojo.PersonInfo;
import com.muciaccia.bot.service.DataManager;
import com.muciaccia.bot.service.DataManagerImpl;
import com.muciaccia.bot.service.RandomPicker;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Stream;

/**
 * This class contains the methods that process the user input.
 */
public class InputParser {
    // separator for a list of arguments
    private static final char SEPARATOR = ',';
    // system-specific new line
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final PrintStream printStream;
    private final DataManager dataManager;
    private final SimpleDateFormat dateFormat;

    private static String INSTRUCTIONS;

    public InputParser(final PrintStream printStream) {
        this.printStream = printStream;
        dataManager = new DataManagerImpl();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        INSTRUCTIONS = makeInstructions();
    }

    /**
     * This method parses the input calling the appropriate methods according to the command.
     *
     * @param input the user input to parse.
     * @throws QuitException if the user uses the QUIT command.
     */
    public void parseInput(final String input) throws QuitException {
        CommandEnum chosenCommand = null;
        Matcher matcher = null;
        for (CommandEnum command : CommandEnum.values()) {
            matcher = command.getPattern().matcher(input);
            if (matcher.matches()) {
                chosenCommand = command;
                break;
            }
        }
        if (null != chosenCommand) {
            switch (chosenCommand) {
                case INSERISCI_CMD:
                    writeChanges(matcher.group(1), dataManager::addPerson);
                    break;
                case RIMUOVI_CMD:
                    writeChanges(matcher.group(1), dataManager::removePerson);
                    break;
                case ASSENTI_CMD:
                    writeChanges(matcher.group(1), dataManager::addAbsentee);
                    break;
                case PRESENTI_CMD:
                    writeChanges(matcher.group(1), dataManager::removeAbsentee);
                    break;
                case ESTRAI_CMD:
                    pickOne();
                    break;
                case LISTE_CMD:
                    printLists();
                    break;
                case SALVA_CMD:
                    export(matcher.group(1).trim());
                    break;
                case CARICA_CMD:
                    load(matcher.group(1).trim());
                    break;
                case HELP_CMD:
                    printUsage();
                    break;
                case QUIT_CMD:
                    throw new QuitException();
                default:
                    printStream.println("ERRORE: comando non supportato.");
            }
        } else {
            printStream.println("Comando non supportato.");
        }
    }


    private void writeChanges(final String names, Consumer<String> writeMethod) {
        List<String> namesList = parseNameList(names);
        namesList.forEach(writeMethod);
    }

    private void pickOne() {
        List<Person> list = dataManager.getParticipantsWithNoAbsentee();
        if (list.isEmpty()) {
            printStream.println("La lista dei presenti è vuota! Usa 'i nome1, nome2, ...' per aggiungere qualcuno.");
        } else {
            Person chosen = RandomPicker.pickOne(list);
            printStream.println(chosen.getName());
        }
    }

    private void printLists() {
        printStream.println("PARTECIPANTI:");
        for (Person person : dataManager.getParticipantsList()) {
            printStream.println(person.getName());
        }
        printStream.println(NEW_LINE + "ASSENTI:");
        for (Map.Entry<Person, PersonInfo> entry : dataManager.getAbsenteesMap().entrySet()) {
            printStream.println(entry.getKey().getName() + " " + dateFormat.format(entry.getValue().getExcludedDate()));
        }
    }

    private void export(final String fileName) {
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (Person person : dataManager.getParticipantsList()) {
                sb.append(person.getName());
                sb.append(SEPARATOR);
            }
            lines.add(sb.toString());
            sb = new StringBuilder();
            for (Map.Entry<Person, PersonInfo> entry : dataManager.getAbsenteesMap().entrySet()) {
                sb.append(entry.getKey().getName());
                sb.append(" ");
                sb.append(dateFormat.format(entry.getValue().getExcludedDate()));
                sb.append(SEPARATOR);
            }
            lines.add(sb.toString());
            Path file = Paths.get(fileName);
            Files.write(file, lines, StandardCharsets.UTF_8);
            printStream.println("Liste esportate con successo.");
        } catch (IOException e) {
            printStream.println("Non è stato possibile esportare le liste.");
        }
    }

    private void load(final String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            List<String> lines = new ArrayList<>();
            stream.forEach(lines::add);
            writeChanges(lines.get(0), dataManager::addPerson);
            loadAbsentees(lines.get(1));
            printStream.println("Liste caricate con successo.");
        } catch (IOException | IndexOutOfBoundsException | ParseException e) {
            printStream.println("Non è stato possibile caricare le liste.");
        }
    }

    private void loadAbsentees(final String absentees) throws ParseException {
        List<String> names = parseNameList(absentees);
        for (String name : names) {
            if (!name.isEmpty()) {
                int pos = name.lastIndexOf(" ");
                dataManager.addAbsentee(name.substring(0, pos), dateFormat.parse(name.substring(pos + 1)));
            }
        }
    }

    private void printUsage() {
        printStream.print(INSTRUCTIONS);
    }

    private String makeInstructions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Questo programma permette di estrarre a sorte una persona da un gruppo.");
        sb.append(NEW_LINE);
        sb.append(NEW_LINE);
        sb.append("COMANDI (di ogni parola chiave è sufficiente la prima lettera):");
        sb.append(NEW_LINE);
        for (CommandEnum command : CommandEnum.values()) {
            sb.append(command.getValue());
            sb.append(command.getHelp());
            sb.append(NEW_LINE);
        }
        return sb.toString();
    }

    /**
     * This method parses a list of names.
     *
     * @param names a list of names, containing spaces and unicode characters, separated by SEPARATOR.
     *              Names are trimmed and empty ones are ignored.
     * @return a list of strings, one for each name.
     */
    private List<String> parseNameList(final String names) {
        List<String> namesList = new ArrayList<>();
        int fromIndex = 0;
        while (fromIndex < names.length()) {
            int toIndex = names.indexOf(SEPARATOR, fromIndex);
            if (-1 == toIndex) {
                toIndex = names.length();
            }
            String name = names.substring(fromIndex, toIndex).trim();
            if (!name.isEmpty()) {
                namesList.add(name);
            }
            fromIndex = toIndex + 1;
        }
        return namesList;
    }
}
