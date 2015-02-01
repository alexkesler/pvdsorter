package org.kesler.pvdsorter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kesler.pvdsorter.gui.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class PVDSorterApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(PVDSorterApp.class);

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        log.info("Starting PVDSorter application");

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PVDSorterAppFactory.class);
        MainController mainController = context.getBean(MainController.class);
        mainController.setStage(stage);
        Scene scene = new Scene(mainController.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Сортировка дел ПК ПВД");
        stage.getIcons().add(new Image(PVDSorterApp.class.getResourceAsStream("/images/Bibble.png")));
        mainController.show();
    }
}
