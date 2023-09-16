package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.views.ErrorAlert;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import java.util.zip.ZipException;

public class UnzipTask implements Callable<ArrayList<File>> {
    private File zipFile;

    public UnzipTask(File zipFile) {
        this.zipFile = zipFile;
    }

    @Override
    public ArrayList<File> call() {
        try {
            return work();
        } catch (ZipException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid Zip File, Please try again");
                alert.setHeaderText("Invalid Zip File");
                alert.setTitle("Warning");
                alert.showAndWait();
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                ErrorAlert errorAlert = new ErrorAlert(e);
                errorAlert.showAlert();
            });
        }
        return new ArrayList<>();
    }

    private ArrayList<File> work() throws IOException {
        ArrayList<File> fileArrayList = new ArrayList<>();
        String tmpDir = System.getProperty("java.io.tmpdir") + "photo-editor/unzip/" + zipFile.getName().replace(".zip", "") + "/";
        // If Exists delete folder
        Path tmpPath = Paths.get(tmpDir);
        if (Files.exists(tmpPath)) {
            Stream<Path> walk = Files.walk(tmpPath);
            walk.map(Path::toFile).forEach(File::delete);
            walk.close();
            Files.delete(tmpPath);
        }
        try (InputStream inputStream = Files.newInputStream(zipFile.toPath())) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ArchiveInputStream archiveInputStream = new ZipArchiveInputStream(bufferedInputStream);

            ArchiveEntry archiveEntry;
            while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
                Path path = Paths.get(tmpDir, archiveEntry.getName());
                File file = path.toFile();
                if (archiveEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (!parent.isDirectory()) {
                        parent.mkdirs();
                    }
                }
                fileArrayList.add(file);
                Files.copy(archiveInputStream, path);
            }

        }

        return fileArrayList;
    }
}
