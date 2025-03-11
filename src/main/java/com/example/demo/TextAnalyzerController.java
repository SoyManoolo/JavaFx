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

            // Usar métodos de la clase TextAnalyzerUtils
            int wordCount = TextAnalyzerUtils.getWordCount(text);
            int charCount = TextAnalyzerUtils.getCharCount(text);
            String emails = TextAnalyzerUtils.getEmails(text);
            String urls = TextAnalyzerUtils.getURLs(text);

            // Actualizar UI
            updateTextFlow(wordCountFlow, String.valueOf(wordCount));
            updateTextFlow(charCountFlow, String.valueOf(charCount));
            updateTextFlow(emailFlow, emails);
            updateTextFlow(urlFlow, urls);

            // Guardar en historial
            String historyEntry = "Texto: " + text + "\n" +
                    "Palabras: " + wordCount + " | Caracteres: " + charCount + "\n" +
                    "Correos: " + emails + "\n" +
                    "URLs: " + urls + "\n";

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
    public void openHistory() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo/history-view.fxml"));

            if (fxmlLoader.getLocation() == null) {
                System.err.println("Error: No se encontró el archivo history-view.fxml");
                return;
            }

            System.out.println("Cargando ventana de historial...");
            Stage historyStage = new Stage();
            historyStage.setTitle("Historial de Análisis");
            historyStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            historyStage.show();
            System.out.println("Ventana de historial abierta correctamente.");
        } catch (IOException e) {
            System.err.println("Error al abrir la ventana de historial: " + e.getMessage());
            e.printStackTrace();
        }
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
