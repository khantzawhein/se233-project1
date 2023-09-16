package com.se233.photoeditor.controllers;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.controllers.tasks.BatchUnzipTask;
import com.se233.photoeditor.controllers.tasks.UnzipTask;
import com.se233.photoeditor.models.ImageFile;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DragAndDropController {
    @FXML
    private AnchorPane dropArea;
    @FXML
    private Text dropLabel;
    @FXML
    private Button addWaterMarkBtn;
    @FXML
    private ProgressBar unzipProgressBar;
    @FXML
    private AnchorPane unzipProgressPane;

    public DragAndDropController() {
    }

    @FXML
    public void initialize() {
        try {
            this.initializeEvents();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    private void initializeEvents() {
        dropArea.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (!dragboard.hasFiles()) {
                event.consume();
                return;
            }
            List<File> files = dragboard.getFiles();
            for (File file : files) {
                // Not jpg and not png and not zip
                if (!file.getName().endsWith(".jpg") && !file.getName().endsWith(".jpeg") && !file.getName().endsWith(".png") && !file.getName().endsWith(".zip")) {
                    event.consume();
                    return;
                }
            }

            event.acceptTransferModes(TransferMode.COPY);
            dropArea.setStyle("-fx-background-color: #D3D3D3; -fx-border-color: #0096FF; -fx-background-radius: 22px");
        });
        dropArea.setOnDragExited(event -> {
            dropArea.setStyle(null);
        });
        dropArea.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            files.stream().filter(file -> !FilenameUtils.getExtension(file.getName()).equals("zip"))
                    .forEach(file -> {
                        try {
                            ImageFile imageFile = new ImageFile(file.getName(), file.getPath(),
                                    FilenameUtils.getExtension(file.getName()), Files.size(Path.of(file.getPath())));
                            Launcher.getImageFiles().add(imageFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            ArrayList<File> zipFiles = files.stream().filter(file -> FilenameUtils.getExtension(file.getName()).equals("zip")).collect(Collectors.toCollection(ArrayList::new));
            if (!zipFiles.isEmpty()) {
                unzipProgressPane.setVisible(true);
                Task<Void> batchUnzipTask = new BatchUnzipTask(zipFiles);
                unzipProgressBar.progressProperty().bind(batchUnzipTask.progressProperty());
                Launcher.getExecutorService().submit(batchUnzipTask);
            }
        });
        addWaterMarkBtn.setOnAction(event -> {
            try {
                Launcher.getStage().setScene(Launcher.getWatermarkScene());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
