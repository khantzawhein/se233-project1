module com.se233.photoeditor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.commons.io;
    requires java.desktop;
    requires javafx.swing;

    opens com.se233.photoeditor to javafx.fxml;
    opens com.se233.photoeditor.controllers to javafx.fxml;
    opens com.se233.photoeditor.models to javafx.fxml;
    opens com.se233.photoeditor.views to javafx.fxml;

    exports com.se233.photoeditor;
    exports com.se233.photoeditor.controllers;
    exports com.se233.photoeditor.models;
    exports com.se233.photoeditor.views;
    exports com.se233.photoeditor.enums;
}