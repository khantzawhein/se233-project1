package com.se233.photoeditor.controllers;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.enums.EditMode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class HomeController {
    @FXML
    private Button watermarkBtn;

    @FXML
    private Button resizeBtn;
    @FXML
    public void initialize() {

        watermarkBtn.setOnAction(e -> {
            try {
                Launcher.setCurrentEditMode(EditMode.WATERMARK);
                Launcher.getStage().setScene(Launcher.getDragAndDropScene());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        resizeBtn.setOnAction(e -> {
            try {
                Launcher.setCurrentEditMode(EditMode.RESIZE);
                Launcher.getStage().setScene(Launcher.getDragAndDropScene());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
