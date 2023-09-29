package com.se233.photoeditor;

import com.se233.photoeditor.enums.ResizeEditMode;
import com.se233.photoeditor.models.ImageFile;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
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
    private static ResizeEditMode currentEditMode;
    private static ObservableList<ImageFile> imageFiles;
    private static ExecutorService executorService;

    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(Launcher.class.getResource("assets/fonts/Satoshi-Regular.otf").toExternalForm(), 10);
        Font.loadFont(Launcher.class.getResource("assets/fonts/Satoshi-Bold.otf").toExternalForm(), 10);
        Font.loadFont(Launcher.class.getResource("assets/fonts/Cookie-Regular.ttf").toExternalForm(), 10);
        imageFiles = FXCollections.observableList(new ArrayList<>());
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

    public static Scene getResizeScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("resize.fxml"));
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

    public static ResizeEditMode getCurrentEditMode() {
        return currentEditMode;
    }

    public static void setCurrentEditMode(ResizeEditMode currentEditMode) {
        Launcher.currentEditMode = currentEditMode;
    }

    public static ObservableList<ImageFile> getImageFiles() {
        return imageFiles;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
