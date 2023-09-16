package com.se233.photoeditor.views;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class ErrorAlert extends Alert {

    private ButtonType exitButton;
    public ErrorAlert(Throwable throwable) {
        super(AlertType.ERROR);
        this.setTitle("Error");
        this.setHeaderText("Something went wrong and the application has to close.");
        this.setContentText(throwable.getClass().getName() + ": " + throwable.getMessage());
        throwable.printStackTrace();
        this.getButtonTypes().clear();
        exitButton = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().addAll(exitButton);
    }

    public void showAlert() {
        this.showAndWait().ifPresent(type -> {
            if (type == exitButton){
                Platform.exit();
                System.exit(0);
            }
        });
    }
}
