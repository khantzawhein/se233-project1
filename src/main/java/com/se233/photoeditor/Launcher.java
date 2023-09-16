package com.se233.photoeditor;

import com.se233.photoeditor.enums.EditMode;
import com.se233.photoeditor.models.ImageFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.ArrayList;

public class Launcher extends Application {
    private static Stage stage;
    private static EditMode currentEditMode;
    private static ArrayList<ImageFile> imageFiles;
    @Override
    public void start(Stage stage) throws Exception {
        imageFiles = new ArrayList<>();
        Launcher.stage = stage;
        stage.setScene(getDragAndDropScene());
        stage.setTitle("Photo Editor");
        stage.show();
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
}
