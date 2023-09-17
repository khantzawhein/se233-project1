package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.enums.ResizeEditMode;
import com.se233.photoeditor.models.ImageFile;
import com.se233.photoeditor.views.ErrorAlert;
import javafx.application.Platform;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class GenerateResizeTask implements Callable<Void> {
    private final ImageFile imageFile;
    private final int i, x, imgQuality;
    private final ResizeEditMode resizeEditMode;
    private final String outputFormat, outputPath;
    private final Color imageBackgroundColor;

    public GenerateResizeTask(ImageFile imageFile, int i, ResizeEditMode resizeEditMode, int x, String outputFormat, String outputPath, int imgQuality, Color imageBackgroundColor) {
        this.imageFile = imageFile;
        this.i = i;
        this.resizeEditMode = resizeEditMode;
        this.x = x;
        this.outputFormat = outputFormat;
        this.outputPath = outputPath;
        this.imgQuality = imgQuality;
        this.imageBackgroundColor = imageBackgroundColor;
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
        BufferedImage bufferedImage = ImageIO.read(new File(this.imageFile.getPath()));
        if (resizeEditMode == ResizeEditMode.WIDTH) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, x);
        } else if (resizeEditMode == ResizeEditMode.HEIGHT) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT, x);
        } else if (resizeEditMode == ResizeEditMode.PERCENTAGE) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT, (int) (bufferedImage.getWidth() * x / 100.0), (int) (bufferedImage.getHeight() * x / 100.0));
        }
        File file = new File(this.outputPath + "/" + FilenameUtils.getBaseName(this.imageFile.getName()) + "-resized-" + i + "." + this.outputFormat.toLowerCase());
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(this.outputFormat.toLowerCase()).next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(this.imgQuality / 100.0f);

        BufferedImage newBufferedImage;
        if (FilenameUtils.getExtension(this.imageFile.getName()).equalsIgnoreCase("png")) {
            newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = newBufferedImage.createGraphics();
            graphics.setColor(this.imageBackgroundColor);
            graphics.fillRect(0, 0, newBufferedImage.getWidth(), newBufferedImage.getHeight());
            graphics.drawImage(bufferedImage, 0, 0, null);
        } else {
            newBufferedImage = bufferedImage;
        }
        IIOImage iioImage = new IIOImage(newBufferedImage, null, null);
        if (file.exists()) {
            file.delete();
        }
        imageWriter.setOutput(ImageIO.createImageOutputStream(file));
        imageWriter.write(null, iioImage, imageWriteParam);
    }
}
