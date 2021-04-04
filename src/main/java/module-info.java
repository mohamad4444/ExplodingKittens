/**
 *
 */
module com.gruppe14.explodingkitten {
    exports com.gruppe14.explodingkitten.common.Exceptions;
    exports com.gruppe14.explodingkitten.common.data;
    exports com.gruppe14.explodingkitten.client;
    exports com.gruppe14.explodingkitten.client.gui.controllers;
    exports com.gruppe14.explodingkitten.common.data.Game;
    exports com.gruppe14.explodingkitten.client.gui;
    exports com.gruppe14.explodingkitten.client.gui.controllers.spielRaum;
    exports com.gruppe14.explodingkitten.common.serverIF;
    exports com.gruppe14.explodingkitten.server;
    exports com.gruppe14.explodingkitten.server.main;
    exports com.gruppe14.explodingkitten.common;

    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires javafx.graphics;
    requires javafx.base;
    requires java.base;

    opens com.gruppe14.explodingkitten.client to javafx.fxml;
    opens com.gruppe14.explodingkitten.client.gui.controllers to javafx.fxml;
    opens com.gruppe14.explodingkitten.client.gui.controllers.spielRaum to javafx.fxml;
}