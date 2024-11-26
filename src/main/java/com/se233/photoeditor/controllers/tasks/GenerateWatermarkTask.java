package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.models.GenerateWatermarkTaskInput;
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
    private final GenerateWatermarkTaskInput input;

    public GenerateWatermarkTask(GenerateWatermarkTaskInput generateWatermarkTaskInput) {
        this.input = generateWatermarkTaskInput;
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
        BufferedImage bufferedImage = ImageIO.read(new File(this.input.imageFile().getPath()));

        double width = bufferedImage.getWidth();
        double height = bufferedImage.getHeight();

        Graphics2D g = bufferedImage.createGraphics();
        Font font = new Font(this.input.font(), Font.PLAIN, this.input.fontSize());
        FontMetrics fontMetrics = bufferedImage.getGraphics().getFontMetrics(font);

        double x = width / 2 - (double) fontMetrics.stringWidth(this.input.watermarkText()) / 2;
        double y = height / 2 + (double) fontMetrics.getHeight() / 2;
        double rotateWidthOffset = (double) fontMetrics.stringWidth(this.input.watermarkText()) / 2;
        double rotateHeightOffset = (double) fontMetrics.getHeight() / 2;
        int paddingX = this.input.paddingX() * 2;

        g.setFont(font);
        g.rotate(Math.toRadians(this.input.rotateDeg()), x + rotateWidthOffset + this.input.offsetX(), y - rotateHeightOffset + this.input.offsetY());
        g.setColor(this.input.color());
        g.drawString(this.input.watermarkText(), (int) x + paddingX + +this.input.offsetX(), (int) y + this.input.offsetY());
        File file = new File(this.input.outputPath() + "/" + FilenameUtils.getBaseName(this.input.imageFile().getName()) + "-watermarked-" + this.input.i() + "." + this.input.outputFormat().toLowerCase());
        ImageIO.write(bufferedImage, this.input.outputFormat().toLowerCase(), file);
    }
}
