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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // Método para contar palabras
    private static int getWordCount(String text) {
        return text.trim().isEmpty() ? 0 : text.split("\\s+").length;
    }

    // Método para contar caracteres (sin espacios)
    private static int getCharCount(String text) {
        return (int) text.chars().filter(c -> !Character.isWhitespace(c)).count();
    }

    // Método para extraer correos electrónicos
    private static String getEmails(String text) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = emailPattern.matcher(text);
        List<String> emails = new ArrayList<>();
        while (matcher.find()) {
            emails.add(matcher.group());
        }
        return emails.isEmpty() ? "-" : String.join(", ", emails);
    }

    // Método para extraer URLs
    private static String getURLs(String text) {
        Pattern urlPattern = Pattern.compile("(https?://\\S+|www\\.[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
        Matcher matcher = urlPattern.matcher(text);
        List<String> urls = new ArrayList<>();
        while (matcher.find()) {
            urls.add(matcher.group());
        }
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
