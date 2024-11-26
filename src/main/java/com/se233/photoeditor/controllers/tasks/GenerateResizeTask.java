package com.se233.photoeditor.controllers.tasks;

import com.se233.photoeditor.enums.ResizeEditMode;
import com.se233.photoeditor.models.GenerateResizeTaskInput;
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
    private final GenerateResizeTaskInput input;

    public GenerateResizeTask(GenerateResizeTaskInput generateResizeTaskInput) {
        this.input = generateResizeTaskInput;
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
        if (this.input.resizeEditMode() == ResizeEditMode.WIDTH) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, this.input.x());
        } else if (this.input.resizeEditMode() == ResizeEditMode.HEIGHT) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT, this.input.x());
        } else if (this.input.resizeEditMode() == ResizeEditMode.PERCENTAGE) {
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT, (int) (bufferedImage.getWidth() * this.input.x() / 100.0), (int) (bufferedImage.getHeight() * this.input.x() / 100.0));
        }
        File file = new File(this.input.outputPath() + "/" + FilenameUtils.getBaseName(this.input.imageFile().getName()) + "-resized-" + this.input.i() + "." + this.input.outputFormat().toLowerCase());
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(this.input.outputFormat().toLowerCase()).next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(this.input.imgQuality() / 100.0f);

        BufferedImage newBufferedImage;
        boolean isPng = FilenameUtils.getExtension(this.input.imageFile().getName()).equalsIgnoreCase("png");
        if (isPng) {
            newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = newBufferedImage.createGraphics();
            graphics.setColor(this.input.imageBackgroundColor());
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
