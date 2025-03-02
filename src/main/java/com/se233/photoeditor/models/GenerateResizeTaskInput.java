package com.se233.photoeditor.models;

import com.se233.photoeditor.enums.ResizeEditMode;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@Builder
public record GenerateResizeTaskInput(ImageFile imageFile, int i,
                                      ResizeEditMode resizeEditMode, int x, String outputFormat,
                                      String outputPath, int imgQuality, Color imageBackgroundColor) {
}
