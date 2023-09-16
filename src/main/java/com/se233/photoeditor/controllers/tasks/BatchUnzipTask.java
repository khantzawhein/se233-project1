package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.models.ImageFile;
import com.se233.photoeditor.views.ErrorAlert;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class BatchUnzipTask extends Task<Void> {

    private ArrayList<File> zipFiles;

    public BatchUnzipTask(ArrayList<File> zipFiles) {
        this.zipFiles = zipFiles;
    }

    @Override
    protected Void call() throws Exception {
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

    private void work() throws InterruptedException, ExecutionException {
        ArrayList<ArrayList<File>> allFiles = new ArrayList<>();
        CompletionService<ArrayList<File>> completionService = new ExecutorCompletionService<>(Launcher.getExecutorService());
        for (File file : zipFiles) {
            completionService.submit(new UnzipTask(file));
        }
        for (int i = 0; i < zipFiles.size(); i++) {

            Future<ArrayList<File>> files = completionService.take();
            allFiles.add(files.get());
            this.updateProgress(i + 1, zipFiles.size());

        }

        ArrayList<ImageFile> imageFiles = allFiles.stream()
                .flatMap(Collection::stream)
                .filter((file) -> file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png"))
                .map((file) -> {
                    ImageFile newFile;
                    try {
                        newFile = new ImageFile(file.getName(), file.getAbsolutePath(), FilenameUtils.getExtension(file.getName()), Files.size(Path.of(file.getPath())));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return newFile;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        Launcher.getImageFiles().addAll(imageFiles);
    }
}
