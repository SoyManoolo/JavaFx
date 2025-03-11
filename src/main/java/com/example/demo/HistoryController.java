package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HistoryController {

    @FXML private ListView<String> historyList;

    private static final ObservableList<String> historyData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        historyList.setItems(historyData);
    }

    public static void addHistoryEntry(String entry) {
        String formattedEntry =
                "🔹 - Texto: " + entry + "\n" +
                        "🔹 - Palabras: " + getWordCount(entry) + "\n" +
                        "🔹 - Caracteres: " + getCharCount(entry) + "\n" +
                        "🔹 - Correos: " + getEmails(entry) + "\n" +
                        "🔹 - URLs: " + getURLs(entry) + "\n" +
                        "----------------------------------------------------------";

        historyData.add(0, formattedEntry);
    }

    // Método optimizado para contar palabras usando streams
    private static int getWordCount(String text) {
        return (int) Arrays.stream(text.trim().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .count();
    }

    // Método optimizado para contar caracteres sin espacios usando streams
    private static int getCharCount(String text) {
        return (int) text.codePoints().filter(c -> !Character.isWhitespace(c)).count();
    }

    // Método optimizado para extraer correos electrónicos con streams
    private static String getEmails(String text) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

        List<String> emails = emailPattern.matcher(text)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());

        return emails.isEmpty() ? "-" : String.join(", ", emails);
    }

    // Método optimizado para extraer URLs con streams
    private static String getURLs(String text) {
        Pattern urlPattern = Pattern.compile("(?:(?:https?|ftp)://|www\\.)\\S+\\.[a-zA-Z]{2,}(?:/\\S*)?");
        List<String> urls = urlPattern.matcher(text)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());

        return urls.isEmpty() ? "-" : String.join(", ", urls);
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) historyList.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void saveHistoryToFile() {
        if (historyData.isEmpty()) {
            System.out.println("El historial está vacío. No se guardará ningún archivo.");
            return;
        }

        // Crear un FileChooser para que el usuario elija la ubicación del archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Historial");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        fileChooser.setInitialFileName("historial.txt");

        // Mostrar el diálogo de guardar archivo
        Stage stage = (Stage) historyList.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String entry : historyData) {
                    writer.write(entry);
                    writer.newLine();
                    writer.newLine();
                }
                System.out.println("Historial guardado correctamente en: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error al guardar el historial: " + e.getMessage());
            }
        }
    }

}
