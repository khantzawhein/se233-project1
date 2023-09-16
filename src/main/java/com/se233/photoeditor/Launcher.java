package com.se233.photoeditor;

import com.se233.photoeditor.enums.EditMode;
import com.se233.photoeditor.models.ImageFile;
import com.se233.photoeditor.views.ErrorAlert;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Launcher extends Application {
    private static Stage stage;
    private static EditMode currentEditMode;
    private static ArrayList<ImageFile> imageFiles;

    private static ExecutorService executorService;

    @Override
    public void start(Stage stage) throws Exception {
        imageFiles = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Launcher.stage = stage;
        stage.setScene(getDragAndDropScene());
        stage.setTitle("Photo Editor");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executorService.shutdown();
        // Remove Tmp Files
        this.cleanUpTemp();
    }

    public void cleanUpTemp() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir") + "photo-editor/unzip/";
        File tmpFolder = new File(tmpDir);
        if (tmpFolder.exists()) {

            Stream<Path> walk = Files.walk(tmpFolder.toPath());
            walk.map(java.nio.file.Path::toFile)
                    .sorted((o1, o2) -> -o1.compareTo(o2))
                    .forEach(File::delete);

            walk.close();
        }
    }

    public static Scene getHomeScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(Launcher.class.getResource("assets/css/style.css").toExternalForm());
        return scene;
    }

    public static Scene getDragAndDropScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("drag-and-drop.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(Launcher.class.getResource("assets/css/style.css").toExternalForm());
        return scene;
    }

    public static Scene getWatermarkScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("watermark.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(Launcher.class.getResource("assets/css/style.css").toExternalForm());
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage() {
        return stage;
    }

    public static EditMode getCurrentEditMode() {
        return currentEditMode;
    }

    public static void setCurrentEditMode(EditMode currentEditMode) {
        Launcher.currentEditMode = currentEditMode;
    }

    public static ArrayList<ImageFile> getImageFiles() {
        return imageFiles;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
