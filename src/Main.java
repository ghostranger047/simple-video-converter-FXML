import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("Form.fxml"));
        //root.getStylesheets().add("/css/theme.css");
        primaryStage.getIcons().add(new Image("main.png"));
        primaryStage.setTitle("Simple Video Converter");
        primaryStage.setScene(new Scene(root, 700, 450));

        primaryStage.setMinHeight(455);
        primaryStage.setMinWidth(700);

        primaryStage.setOnCloseRequest(event ->
        {
            stop_app();
        });

        primaryStage.show();
    }


    public void stop_app()
    {
        System.out.println("Stop");
        ConvertOp.process.destroy();
        System.exit(0);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
