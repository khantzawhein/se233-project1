package com.se233.photoeditor.views;

import com.se233.photoeditor.Launcher;
import com.se233.photoeditor.models.ImageFile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class ImageFileItemPane {
    private final AnchorPane imageFileItemPane;

    public ImageFileItemPane(ImageFile imageFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("image-file-pane.fxml"));
        this.imageFileItemPane = fxmlLoader.load();
        for (Node node : imageFileItemPane.getChildren()) {
            if (node instanceof Text && node.getId().equals("filename")) {
                ((Text) node).setText(imageFile.getName());
            } else if (node instanceof Text && node.getId().equals("filesize")) {
                double mb = (double) imageFile.getSize() / 1000 / 1000;
                String size = "Size: " + String.format("%.2f", mb) + " MiB";
                ((Text) node).setText(size);
            }
        }
    }

    public void setDeleteHandler(EventHandler<ActionEvent> deleteHandler) {
        for (Node node : imageFileItemPane.getChildren()) {
            if (node instanceof Hyperlink && node.getId().equals("deleteBtn")) {
                ((Hyperlink) node).setOnAction(deleteHandler);
            }
        }
    }

    public AnchorPane getImageFileItemPane() {
        return imageFileItemPane;
    }
}
