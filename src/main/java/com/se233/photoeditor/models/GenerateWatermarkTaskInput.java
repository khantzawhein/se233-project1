package com.se233.photoeditor.models;

import lombok.Builder;

import java.awt.*;

@Builder
public record GenerateWatermarkTaskInput(ImageFile imageFile, int i, String font, String watermarkText, String outputFormat,
                                         String outputPath, Color color, int rotateDeg, int fontSize, int offsetX,
                                         int offsetY, int paddingX) {
}
