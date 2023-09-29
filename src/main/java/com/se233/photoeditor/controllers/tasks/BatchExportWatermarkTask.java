package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.models.ImageFile;
import com.se233.photoeditor.views.ErrorAlert;
import com.se233.photoeditor.views.ExportSuccessAlert;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.*;

public class BatchExportWatermarkTask extends Task<Void> {
    private ObservableList<ImageFile> imageFiles;
    private String font;
    private String outputFormat, watermarkText;
    private int fontSize, offsetX, offsetY, rotateDeg, paddingX;
    private Color color;

    private File outputDir;

    public BatchExportWatermarkTask(ObservableList<ImageFile> imageFiles, File outputDir, String font,
                                    String watermarkText, String outputFormat, Color color,
                                    int rotateDeg,
                                    int fontSize, int offsetX, int offsetY, int paddingX) {
        this.imageFiles = imageFiles;
        this.font = font;
        this.watermarkText = watermarkText;
        this.outputFormat = outputFormat;
        this.fontSize = fontSize;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.paddingX = paddingX;
        this.rotateDeg = rotateDeg;
        this.color = color;
        this.outputDir = outputDir;
    }

    @Override
    protected Void call() {
        try {
            this.work();
        } catch (Exception e) {
            Platform.runLater(() -> {
                ErrorAlert errorAlert = new ErrorAlert(e);
                errorAlert.showAlert();
            });
        }
        return null;
    }

    private void work() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CompletionService<Void> completionService = new ExecutorCompletionService<>(Launcher.getExecutorService());
        for (int i = 0; i < imageFiles.size(); i++) {
            ImageFile imageFile = imageFiles.get(i);
            completionService.submit(new GenerateWatermarkTask(imageFile, i, font, watermarkText, outputFormat, outputDir.getAbsolutePath(), color, rotateDeg, fontSize, offsetX, offsetY, paddingX));

        }
        for (int i = 0; i < imageFiles.size(); i++) {
            completionService.take();
            this.updateProgress(i + 1, imageFiles.size());
        }

        long endTime = System.currentTimeMillis();
        Platform.runLater(() -> {
            ExportSuccessAlert exportSuccessAlert = new ExportSuccessAlert(outputDir, "Watermarked images has been exported to destination folder successfully!", endTime - startTime);
            exportSuccessAlert.showAlert();
        });
    }
}
