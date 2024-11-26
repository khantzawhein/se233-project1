package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.models.BatchExportWatermarkTaskInput;
import com.se233.photoeditor.models.GenerateWatermarkTaskInput;
import com.se233.photoeditor.models.ImageFile;
import com.se233.photoeditor.views.ErrorAlert;
import com.se233.photoeditor.views.ExportSuccessAlert;
import javafx.application.Platform;

import java.util.concurrent.*;

public class BatchExportWatermarkTask extends BaseTask<Void> {
    private final BatchExportWatermarkTaskInput input;

    public BatchExportWatermarkTask(BatchExportWatermarkTaskInput batchExportWatermarkTaskInput) {
        this.input = batchExportWatermarkTaskInput;
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

    protected Void work() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CompletionService<Void> completionService = new ExecutorCompletionService<>(Launcher.getExecutorService());
        for (int i = 0; i < this.input.imageFiles().size(); i++) {
            ImageFile imageFile = this.input.imageFiles().get(i);
            GenerateWatermarkTaskInput generateWatermarkTaskInput = getGenerateWatermarkTaskInput(imageFile, i);
            completionService.submit(new GenerateWatermarkTask(generateWatermarkTaskInput));

        }
        for (int i = 0; i < this.input.imageFiles().size(); i++) {
            completionService.take();
            this.updateProgress(i + 1, this.input.imageFiles().size());
        }

        long endTime = System.currentTimeMillis();
        Platform.runLater(() -> {
            ExportSuccessAlert exportSuccessAlert = new ExportSuccessAlert(this.input.outputDir(), "Watermarked images has been exported to destination folder successfully!", endTime - startTime);
            exportSuccessAlert.showAlert();
        });
        return null;
    }

    private GenerateWatermarkTaskInput getGenerateWatermarkTaskInput(ImageFile imageFile, int i) {
        return GenerateWatermarkTaskInput.builder()
                .imageFile(imageFile).i(i).font(this.input.font())
                .watermarkText(this.input.watermarkText())
                .outputFormat(this.input.outputFormat())
                .outputPath(this.input.outputDir().getAbsolutePath())
                .color(this.input.color())
                .rotateDeg(this.input.rotateDeg())
                .fontSize(this.input.fontSize())
                .offsetX(this.input.offsetX())
                .offsetY(this.input.offsetY())
                .paddingX(this.input.paddingX()).build();
    }
}
