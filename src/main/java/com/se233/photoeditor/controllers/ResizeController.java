package com.se233.photoeditor.controllers;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.controllers.tasks.BatchExportResizeTask;
import com.se233.photoeditor.enums.ResizeEditMode;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;

public class ResizeController {
    @FXML
    private AnchorPane percentageResizePane, heightResizePane, widthResizePane;

    private ResizeEditMode currentEditMode;
    @FXML
    private ComboBox<String> outputFormat;
    @FXML
    private Button startBtn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ColorPicker bgColorPicker;
    @FXML
    private Slider imageQualitySlider;
    @FXML
    private TextField percentageField, heightField, widthField;
    @FXML
    public void initialize() {
        outputFormat.getItems().addAll("JPEG", "PNG");
        outputFormat.getSelectionModel().select(0);
        this.currentEditMode = ResizeEditMode.PERCENTAGE;
        this.percentageField.textProperty().addListener(acceptOnlyNumberInTextListener(percentageField));
        this.heightField.textProperty().addListener(acceptOnlyNumberInTextListener(heightField));
        this.widthField.textProperty().addListener(acceptOnlyNumberInTextListener(widthField));
        this.startBtn.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Output Directory");
            File selectedDirectory = directoryChooser.showDialog(Launcher.getStage());
            if (selectedDirectory != null) {
                Color color = new Color((int) (bgColorPicker.getValue().getRed() * 255), (int) (bgColorPicker.getValue().getGreen() * 255), (int) (bgColorPicker.getValue().getBlue() * 255));
                int x = ResizeEditMode.PERCENTAGE == this.currentEditMode ? Integer.parseInt(percentageField.getText().isEmpty() ? "0" : percentageField.getText()) :
                        ResizeEditMode.WIDTH == this.currentEditMode ? Integer.parseInt(widthField.getText().isEmpty() ? "0" : widthField.getText()) :
                                Integer.parseInt(heightField.getText().isEmpty() ? "0" : heightField.getText());
                Task<Void> batchExportResizeTask = new BatchExportResizeTask(Launcher.getImageFiles(), this.currentEditMode, x, outputFormat.getSelectionModel().getSelectedItem(), selectedDirectory, (int) imageQualitySlider.getValue(), color);
                Launcher.getExecutorService().submit(batchExportResizeTask);
                progressBar.progressProperty().bind(batchExportResizeTask.progressProperty());
            }
        });
    }

    private ChangeListener<String> acceptOnlyNumberInTextListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }


    @FXML
    private void changeToHeightPane() {
        this.currentEditMode = ResizeEditMode.HEIGHT;
        this.percentageResizePane.setVisible(false);
        this.heightResizePane.setVisible(true);
        this.widthResizePane.setVisible(false);
    }

    @FXML
    private void changeToWidthPane() {
        this.currentEditMode = ResizeEditMode.WIDTH;
        this.percentageResizePane.setVisible(false);
        this.heightResizePane.setVisible(false);
        this.widthResizePane.setVisible(true);
    }

    @FXML
    private void changeToPercentagePane() {
        this.currentEditMode = ResizeEditMode.PERCENTAGE;
        this.percentageResizePane.setVisible(true);
        this.heightResizePane.setVisible(false);
        this.widthResizePane.setVisible(false);
    }

    public ResizeEditMode getCurrentEditMode() {
        return currentEditMode;
    }
}
