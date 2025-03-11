package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextAnalyzerController {

    @FXML private TextArea inputText;
    @FXML private TextFlow wordCountFlow;
    @FXML private TextFlow charCountFlow;
    @FXML private TextFlow emailFlow;
    @FXML private TextFlow urlFlow;

    @FXML
    public void analyzeText() {
        try {
            String text = Optional.ofNullable(inputText.getText()).orElse("").trim();

            if (text.isEmpty()) {
                updateTextFlow(wordCountFlow, "0");
                updateTextFlow(charCountFlow, "0");
                updateTextFlow(emailFlow, "-");
                updateTextFlow(urlFlow, "-");
                return;
            }

            // Contar palabras y caracteres
            List<String> words = Arrays.stream(text.split("[^\\p{L}\\d]+"))
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList());

            long charCount = text.chars()
                    .filter(c -> !Character.isWhitespace(c))
                    .count();

            // Expresión regular para correos electrónicos
            Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            Matcher emailMatcher = emailPattern.matcher(text);
            List<String> emails = new ArrayList<>();
            while (emailMatcher.find()) {
                emails.add(emailMatcher.group());
            }

            // Expresión regular para URLs
            Pattern urlPattern = Pattern.compile(
                    "(https?://\\S+|www\\.[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?!\\S*@)|[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?=/|\\b)(?!\\S*@))"
            );
            Matcher urlMatcher = urlPattern.matcher(text);
            List<String> urls = new ArrayList<>();
            while (urlMatcher.find()) {
                urls.add(urlMatcher.group());
            }

            // Actualizar etiquetas sin modificar la parte en negrita
            updateTextFlow(wordCountFlow, String.valueOf(words.size()));
            updateTextFlow(charCountFlow, String.valueOf(charCount));
            updateTextFlow(emailFlow, emails.isEmpty() ? "-" : String.join(", ", emails));
            updateTextFlow(urlFlow, urls.isEmpty() ? "-" : String.join(", ", urls));

            // Guardar en historial
            String historyEntry = "Texto: " + text + "\n" +
                    "Palabras: " + words.size() + " | Caracteres: " + charCount + "\n" +
                    "Correos: " + (emails.isEmpty() ? "-" : String.join(", ", emails)) + "\n" +
                    "URLs: " + (urls.isEmpty() ? "-" : String.join(", ", urls)) + "\n";

            HistoryController.addHistoryEntry(historyEntry);

        } catch (Exception e) {
            updateTextFlow(wordCountFlow, "Error");
            updateTextFlow(charCountFlow, "");
            updateTextFlow(emailFlow, "-");
            updateTextFlow(urlFlow, "-");
            System.err.println("Error: " + e.getMessage());
        }
    }

    @FXML
    public void openHistory() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo/history-view.fxml"));
        Stage historyStage = new Stage();
        historyStage.setTitle("Historial de Análisis");
        historyStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
        historyStage.show();
    }

    private void updateTextFlow(TextFlow textFlow, String normalText) {
        if (textFlow.getChildren().size() > 1) {
            ((Text) textFlow.getChildren().get(1)).setText(normalText);
        }
    }

    @FXML
    public void clearAnalysis() {
        inputText.clear(); // Borra el texto ingresado

        // Restablece los valores a su estado inicial
        updateTextFlow(wordCountFlow, "0");
        updateTextFlow(charCountFlow, "0");
        updateTextFlow(emailFlow, "-");
        updateTextFlow(urlFlow, "-");
    }

    @FXML
    public void copyToClipboard() {
        // Obtener los datos actuales del análisis
        String analysisResult =
                "Palabras: " + ((Text) wordCountFlow.getChildren().get(1)).getText() + "\n" +
                        "Caracteres: " + ((Text) charCountFlow.getChildren().get(1)).getText() + "\n" +
                        "Correos: " + ((Text) emailFlow.getChildren().get(1)).getText() + "\n" +
                        "URLs: " + ((Text) urlFlow.getChildren().get(1)).getText();

        // Copiar al portapapeles
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(analysisResult);
        clipboard.setContent(content);
    }
}
