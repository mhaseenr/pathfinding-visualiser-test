package com.jpro.pathfinding;

import com.jpro.webapi.JProApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class PathfindingApplication extends JProApplication
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        Font.loadFont(
                PathfindingApplication.class.getResource("/com/jpro/pathfinding/css/NORM.TTF").toExternalForm(),
                10
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jpro/pathfinding/fxml/prototype-view.fxml"));
        Scene scene = null;
        try
        {
            Parent root = loader.load();
            PathfindingController controller = loader.getController();
            controller.init(this);

            //create JavaFX scene
            scene = new Scene(root);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        stage.setTitle("Pathfinding Algorithm!");
        stage.setScene(scene);

        //open JavaFX window
        stage.show();
    }
}


/*
package com.jpro.pathfinding;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PathfindingApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException {
//        Font.loadFont(
//                PathfindingApplication.class.getResource("NORM.TTF").toExternalForm(),
//                10
//        );
        FXMLLoader fxmlLoader = new FXMLLoader(PathfindingApplication.class.getResource("prototype-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
//        scene.getStylesheets().add(getClass().getResource("Viper.css").toExternalForm());
        stage.setTitle("Pathfinding Visualiser");
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
    }
}*/
