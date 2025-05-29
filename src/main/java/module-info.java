module com.caleb.workshopjavajdbc {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires mysql.connector.j;


    opens com.caleb.workshopjavajdbc to javafx.fxml;
    exports com.caleb.workshopjavajdbc;
    opens com.caleb.workshopjavajdbc.model.entities to javafx.base;
    exports com.caleb.workshopjavajdbc.controller;
    opens com.caleb.workshopjavajdbc.controller to javafx.fxml;
}