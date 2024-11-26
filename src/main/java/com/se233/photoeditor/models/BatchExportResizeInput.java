package com.se233.photoeditor.models;

import com.se233.photoeditor.enums.ResizeEditMode;
import javafx.collections.ObservableList;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.io.File;

@Builder
public record BatchExportResizeInput(ObservableList<ImageFile> imageFiles, ResizeEditMode resizeEditMode, int x,
                                     String outputFormat, File outputDir, int imgQuality, Color imageBackgroundColor) {
}
