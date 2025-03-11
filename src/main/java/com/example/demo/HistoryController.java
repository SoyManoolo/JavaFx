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

public class HistoryController {

    @FXML private ListView<String> historyList;

    private static final ObservableList<String> historyData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        historyList.setItems(historyData);
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

    public static void addHistoryEntry(String entry) {
        historyData.add(0, entry); // Agrega solo el texto ya formateado sin recalcularlo
    }

}
