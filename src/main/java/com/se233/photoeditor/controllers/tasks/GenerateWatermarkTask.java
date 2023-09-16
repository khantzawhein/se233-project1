package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.models.ImageFile;
import com.se233.photoeditor.views.ErrorAlert;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class GenerateWatermarkTask implements Callable<Void> {
    private ImageFile imageFile;
    private String font;
    private String outputFormat, watermarkText, outputPath;
    private int fontSize, offsetX, offsetY, rotateDeg, paddingX;
    private Color color;
    private BufferedImage bufferedImage;
    private int i;

    public GenerateWatermarkTask(ImageFile imageFile, int i, String font, String watermarkText, String outputFormat, String outputPath,
                                 Color color, int rotateDeg, int fontSize, int offsetX, int offsetY, int paddingX) {
        this.imageFile = imageFile;
        this.font = font;
        this.watermarkText = watermarkText;
        this.outputFormat = outputFormat;
        this.fontSize = fontSize;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.paddingX = paddingX;
        this.rotateDeg = rotateDeg;
        this.color = color;
        this.outputPath = outputPath;
        this.i = i;
    }

    @Override
    public Void call() {
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

    private void work() throws IOException {
        bufferedImage = ImageIO.read(new File(this.imageFile.getPath()));

        double width = bufferedImage.getWidth();
        double height = bufferedImage.getHeight();

        Graphics2D g = bufferedImage.createGraphics();
        Font font = new Font(this.font, Font.PLAIN, this.fontSize);
        FontMetrics fontMetrics = bufferedImage.getGraphics().getFontMetrics(font);

        double x = width / 2 - (double) fontMetrics.stringWidth(this.watermarkText) / 2;
        double y = height / 2 + (double) fontMetrics.getHeight() / 2;
        double rotateWidthOffset = (double) fontMetrics.stringWidth(this.watermarkText) / 2;
        double rotateHeightOffset = (double) fontMetrics.getHeight() / 2;
        int paddingX = this.paddingX * 2;

        g.setFont(font);
        g.rotate(Math.toRadians(this.rotateDeg), x + rotateWidthOffset + this.offsetX, y - rotateHeightOffset + this.offsetY);
        g.setColor(this.color);
        g.drawString(this.watermarkText, (int) x + paddingX + +this.offsetX, (int) y + this.offsetY);
        File file = new File(this.outputPath + "/" + FilenameUtils.getBaseName(this.imageFile.getName()) + "-watermarked-" + i + "." + this.outputFormat.toLowerCase());
        ImageIO.write(bufferedImage, this.outputFormat.toLowerCase(), file);
    }
}
