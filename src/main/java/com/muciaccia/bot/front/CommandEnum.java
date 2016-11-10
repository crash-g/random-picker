package com.muciaccia.bot.front;

import java.util.regex.Pattern;

public enum CommandEnum {
    INSERISCI_CMD("Inserisci", true, " nome1[, nome2, ...] --> Inserisce nome1, nome2... nella lista di presenti."),
    RIMUOVI_CMD("Rimuovi", true, " nome1[, nome2, ...] --> Rimuove nome1, nome2... dalla lista di presenti."),
    ASSENTI_CMD("Assenti", true, " nome1[, nome2, ...] --> Inserisce nome1, nome2... nella lista degli assenti."),
    PRESENTI_CMD("Presenti", true, " nome1[, nome2, ...] --> Rimuove nome1, nome2... dalla lista degli assenti."),
    ESTRAI_CMD("Estrai", false, " --> Estrae una persona a sorte."),
    LISTE_CMD("Liste", false, " --> Stampa le liste di presenti e assenti."),
    SALVA_CMD("Salva", true, " nome_file --> Esporta lo stato attuale su nome_file."),
    CARICA_CMD("Carica", true, " nome_file --> Utilizza nome_file per inizializzare lista presenti e assenti."),
    QUIT_CMD("Quit", false, " --> Termina il programma."),
    HELP_CMD("Help", false, " --> Stampa questo messaggio.");


    private final String value;
    private final boolean acceptArg;
    private final String help;
    private Pattern pattern;

    CommandEnum(final String value, final boolean acceptArg, final String help) {
        this.value = value;
        this.acceptArg = acceptArg;
        this.help = help;
    }

    public String getValue() {
        return value;
    }

    public String getHelp() {
        return help;
    }

    public Pattern getPattern() {
        if (null == pattern) {
            String regex = "(?i)(?:" + value + "|" + value.charAt(0) + ")";
            if (acceptArg) {
                regex += " (.+)";
            }
            pattern = Pattern.compile(regex);
        }
        return pattern;
    }
}
