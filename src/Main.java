import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("Form.fxml"));
        primaryStage.setTitle("Simple Video Converter");
        primaryStage.setScene(new Scene(root, 700, 450));
        primaryStage.setMinHeight(455);
        primaryStage.setMinWidth(700);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
