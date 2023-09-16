package com.se233.photoeditor.views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.*;
import java.io.File;

public class ExportSuccessAlert extends Alert {
    private final ButtonType buttonTypeOne;
    private final File outputDir;

    public ExportSuccessAlert(File outputDir, long timeElasped) {
        super(AlertType.INFORMATION);
        this.setTitle("Success");
        this.setHeaderText("Task Completed Successfully");
        this.setContentText("Watermarked images has been exported to destination folder successfully!\n\nTime Elapsed: " + timeElasped/1000 + "s");
        this.outputDir = outputDir;
        // Add button open folder to alert
        buttonTypeOne = new ButtonType("Show Folder");
        this.getButtonTypes().addAll(buttonTypeOne);

    }
    public void showAlert() {
        this.showAndWait().ifPresent(type -> {
            if (type == buttonTypeOne) {
                try {
                    Desktop.getDesktop().open(outputDir);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
