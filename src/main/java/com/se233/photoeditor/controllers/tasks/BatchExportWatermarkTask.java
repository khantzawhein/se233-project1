package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.models.ImageFile;
import com.se233.photoeditor.views.ExportSuccessAlert;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.*;

public class BatchExportWatermarkTask extends Task<Void> {
    private ArrayList<ImageFile> imageFiles;
    private String font;
    private String outputFormat, watermarkText;
    private int fontSize, offsetX, offsetY, rotateDeg, paddingX;
    private Color color;

    private File outputDir;

    public BatchExportWatermarkTask(ArrayList<ImageFile> imageFiles, File outputDir, String font,
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
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CompletionService<Void> completionService = new ExecutorCompletionService<>(executorService);
        for (ImageFile imageFile : imageFiles) {
            completionService.submit(new GenerateWatermarkTask(imageFile, font, watermarkText, outputFormat, outputDir.getAbsolutePath(), color, rotateDeg, fontSize, offsetX, offsetY, paddingX));

        }
        for (int i = 0; i < imageFiles.size(); i++) {
            try {
                completionService.take();
                this.updateProgress(i + 1, imageFiles.size());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Platform.runLater(() -> {
            ExportSuccessAlert exportSuccessAlert = new ExportSuccessAlert(outputDir);
            exportSuccessAlert.showAlert();
        });
        return null;
    }
}
