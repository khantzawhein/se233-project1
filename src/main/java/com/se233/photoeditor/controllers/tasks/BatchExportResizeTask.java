package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.enums.ResizeEditMode;
import com.se233.photoeditor.models.ImageFile;
import com.se233.photoeditor.views.ErrorAlert;
import com.se233.photoeditor.views.ExportSuccessAlert;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

public class BatchExportResizeTask extends Task<Void> {
    private ArrayList<ImageFile> imageFiles;
    private String outputFormat;
    private File outputDir;
    private Color imageBackgroundColor;
    private int x, imgQuality;
    private ResizeEditMode resizeEditMode;

    public BatchExportResizeTask(ArrayList<ImageFile> imageFiles, ResizeEditMode resizeEditMode, int x, String outputFormat, File outputDir, int imgQuality, Color imageBackgroundColor) {
        this.resizeEditMode = resizeEditMode;
        this.imageFiles = imageFiles;
        this.x = x;
        this.outputFormat = outputFormat;
        this.outputDir = outputDir;
        this.imgQuality = imgQuality;
        this.imageBackgroundColor = imageBackgroundColor;
    }

    @Override
    protected Void call() {
        try {
            this.work();
        } catch (InterruptedException e) {
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
            completionService.submit(new GenerateResizeTask(imageFile, i, resizeEditMode, x, outputFormat, outputDir.getAbsolutePath(), imgQuality, imageBackgroundColor));
        }
        for (int i = 0; i < imageFiles.size(); i++) {
            completionService.take();
            this.updateProgress(i + 1, imageFiles.size());
        }

        long endTime = System.currentTimeMillis();
        Platform.runLater(() -> {
            ExportSuccessAlert exportSuccessAlert = new ExportSuccessAlert(outputDir, endTime - startTime);
            exportSuccessAlert.showAlert();
        });
    }
}
