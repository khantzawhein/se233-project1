package com.se233.photoeditor.models;

import javafx.collections.ObservableList;
import lombok.Builder;

import java.awt.*;
import java.io.File;

@Builder
public record BatchExportWatermarkTaskInput(ObservableList<ImageFile> imageFiles, File outputDir, String font,
                                            String watermarkText, String outputFormat, Color color,
                                            int rotateDeg,
                                            int fontSize, int offsetX, int offsetY, int paddingX) {
}
