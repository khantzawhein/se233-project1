package com.se233.photoeditor.controllers;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.controllers.tasks.BatchExportWatermarkTask;
import com.se233.photoeditor.models.ImageFile;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatermarkController {
    @FXML
    private TextField watermarkText;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ComboBox<String> fontPicker, outputFormat;
    @FXML
    private Slider rotationSlider, paddingSlider, sizeSlider;
    @FXML
    private ImageView previewImageView;
    @FXML
    private Button previousImgBtn, nextImgBtn, startWatermarkBtn;
    @FXML
    private ProgressBar progressBar;
    private Color fontColor;
    private int offsetX, offsetY;
    private int currentImageIndex;

    @FXML
    public void initialize() {
        ObservableList<ImageFile> imageFiles = Launcher.getImageFiles();
        Launcher.getExecutorService().execute(() -> {
            fontPicker.getItems().addAll(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            Platform.runLater(() -> {
                fontPicker.getSelectionModel().select(0);
            });
        });

        outputFormat.getItems().addAll("JPEG", "PNG");
        outputFormat.getSelectionModel().select(0);
        this.currentImageIndex = 0;

        watermarkText.setOnKeyReleased(event -> {
            updatePreviewOnBackground();
        });

        colorPicker.setOnAction(event -> {
            updatePreviewOnBackground();
        });

        fontPicker.setOnAction(event -> {
            updatePreviewOnBackground();
        });

        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePreviewOnBackground();
        });

        rotationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePreviewOnBackground();
        });

        paddingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePreviewOnBackground();
        });

        previousImgBtn.setOnAction(event -> {
            if (this.currentImageIndex > 0) {
                this.currentImageIndex--;
                updatePreviewOnBackground();
            }
        });

        nextImgBtn.setOnAction(event -> {
            if (this.currentImageIndex < imageFiles.size() - 1) {
                this.currentImageIndex++;
                updatePreviewOnBackground();
            }
        });

        startWatermarkBtn.setOnAction((event) -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Output Directory");
            File selectedDirectory = directoryChooser.showDialog(Launcher.getStage());
            if (selectedDirectory != null) {
                Task<Void> batchExportWatermarkTask = new BatchExportWatermarkTask(Launcher.getImageFiles(), selectedDirectory, fontPicker.getValue(),
                        watermarkText.getText(), outputFormat.getValue(), fontColor,
                        (int) rotationSlider.getValue(), (int) sizeSlider.getValue(),
                        this.offsetX, this.offsetY,(int) paddingSlider.getValue());

                Launcher.getExecutorService().submit(batchExportWatermarkTask);

                progressBar.progressProperty().bind(batchExportWatermarkTask.progressProperty());
            }

        });

        updatePreviewOnBackground();

    }

    private void updatePreviewOnBackground() {
        Launcher.getExecutorService().execute(this::updatePreview);
    }

    private void updatePreview() {
        ImageFile currentImageFile = Launcher.getImageFiles().get(this.currentImageIndex);
        BufferedImage bufferedImage;

        try {
            bufferedImage = ImageIO.read(new File(currentImageFile.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        double width = bufferedImage.getWidth();
        double height = bufferedImage.getHeight();

        Graphics2D g = bufferedImage.createGraphics();
        Font font = new Font(fontPicker.getValue(), Font.PLAIN, (int) sizeSlider.getValue());
        FontMetrics fontMetrics = bufferedImage.getGraphics().getFontMetrics(font);

        double x = width / 2 - (double) fontMetrics.stringWidth(watermarkText.getText()) / 2;
        double y = height / 2 + (double) fontMetrics.getHeight() / 2;
        double rotateWidthOffset = (double) fontMetrics.stringWidth(watermarkText.getText()) / 2;
        double rotateHeightOffset = (double) fontMetrics.getHeight() / 2;
        int paddingX = (int) paddingSlider.getValue() * 2;
        this.fontColor = new Color((int) (colorPicker.getValue().getRed() * 255), (int) (colorPicker.getValue().getGreen() * 255), (int) (colorPicker.getValue().getBlue() * 255));

        g.setFont(font);
        g.rotate(Math.toRadians(rotationSlider.getValue()), x + rotateWidthOffset + this.offsetX, y - rotateHeightOffset + this.offsetY);
        g.setColor(fontColor);
        g.drawString(watermarkText.getText(), (int) x + paddingX + +this.offsetX, (int) y + this.offsetY);

        Platform.runLater(() -> {
            previewImageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        });
    }

    @FXML
    private void moveCenterButtonPressed() {
        this.offsetX = 0;
        this.offsetY = 0;
        updatePreviewOnBackground();
    }

    @FXML
    private void moveRightButtonPressed() {
        this.offsetX += 20;
        updatePreviewOnBackground();
    }

    @FXML
    private void moveUpButtonPressed() {
        this.offsetY -= 20;
        updatePreviewOnBackground();
    }

    @FXML
    private void moveDownButtonPressed() {
        this.offsetY += 20;
        updatePreviewOnBackground();
    }

    @FXML
    private void moveLeftButtonPressed() {
        this.offsetX -= 20;
        updatePreviewOnBackground();
    }

}
