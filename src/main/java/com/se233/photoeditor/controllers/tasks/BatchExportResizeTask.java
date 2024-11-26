package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.models.BatchExportResizeInput;
import com.se233.photoeditor.models.GenerateResizeTaskInput;
import com.se233.photoeditor.models.ImageFile;
import com.se233.photoeditor.views.ErrorAlert;
import com.se233.photoeditor.views.ExportSuccessAlert;
import javafx.application.Platform;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

public class BatchExportResizeTask extends BaseTask<Void> {
    private final BatchExportResizeInput input;

    public BatchExportResizeTask(BatchExportResizeInput batchExportResizeInput) {
        this.input = batchExportResizeInput;
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

    @Override
    protected Void work() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CompletionService<Void> completionService = new ExecutorCompletionService<>(Launcher.getExecutorService());
        for (int i = 0; i < this.input.imageFiles().size(); i++) {
            ImageFile imageFile = this.input.imageFiles().get(i);
            GenerateResizeTaskInput generateResizeTaskInput = getGenerateResizeTaskInput(imageFile, i);
            completionService.submit(new GenerateResizeTask(generateResizeTaskInput));
        }
        for (int i = 0; i < this.input.imageFiles().size(); i++) {
            completionService.take();
            this.updateProgress(i + 1, this.input.imageFiles().size());
        }

        long endTime = System.currentTimeMillis();
        Platform.runLater(() -> {
            ExportSuccessAlert exportSuccessAlert = new ExportSuccessAlert(this.input.outputDir(), "Resized images has been exported to destination folder successfully!", endTime - startTime);
            exportSuccessAlert.showAlert();
        });
        return null;
    }

    private GenerateResizeTaskInput getGenerateResizeTaskInput(ImageFile imageFile, int i) {
        return GenerateResizeTaskInput.builder()
                .imageFile(imageFile).i(i).resizeEditMode(this.input.resizeEditMode())
                .x(this.input.x()).outputFormat(this.input.outputFormat())
                .outputPath(this.input.outputDir().getAbsolutePath())
                .imgQuality(this.input.imgQuality())
                .imageBackgroundColor(this.input.imageBackgroundColor()).build();
    }
}
