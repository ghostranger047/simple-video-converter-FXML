import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    List<String> open_ext;

    @FXML
    private Button Open, Start, Stop, Clear;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField textCurrentFile;
    @FXML
    private ListView<File> listViewFiles;
    @FXML
    private ListView<String> listViewFormats;
    @FXML
    private ProgressBar progBarCurrent, progBarTotal;


    private ObservableList<File> files;
    private ObservableList<String> formats;

    private List<Double> list_durations;

    private String str_format;

    private ConvertOp op;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println("Initialized");
        Open.setText("Open");
        Stop.setDisable(true);

        open_ext = new ArrayList<>();
        open_ext.add("*.mp4"); open_ext.add("*.avi"); open_ext.add("*.mkv"); open_ext.add("*.wmv");
        formats = FXCollections.observableArrayList("avi", "mp4", "mkv");
        listViewFormats.setItems(formats);
        listViewFormats.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        files = FXCollections.observableArrayList();

        str_format = "avi";
        listViewFormats.getSelectionModel().select(0);
        textCurrentFile.setEditable(false);
        //Stop.setDisable(true);
    }

    @FXML
    public void open_file(ActionEvent event)
    {
        System.out.println("open");
        FileChooser fc = new FileChooser();
        fc.setTitle("Videos");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Videos", open_ext));
        Stage stg = (Stage)borderPane.getScene().getWindow();
        List<File> fs = fc.showOpenMultipleDialog(stg);


        files.addAll(fs);


        listViewFiles.setItems(files);
        listViewFiles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        FFProbe fprobe = new FFProbe();
        try
        {
            list_durations = fprobe.get_duration(files);
            System.out.println(list_durations);

        }
        catch (Exception e)
        {
            System.out.println(e);

        }


    }

    @FXML
    public void clear_queue(ActionEvent event)

    {
        files.clear();
    }



    @FXML
    public void get_selected_format(MouseEvent event)
    {
        if(!(listViewFormats.getSelectionModel().getSelectedItem() == null))
        {
            str_format = listViewFormats.getSelectionModel().getSelectedItem();
            System.out.println(str_format);
        }

    }

    @FXML
    public void convert_clicked(ActionEvent event)
    {

        op= new ConvertOp(files, str_format, textCurrentFile, progBarCurrent, progBarTotal, list_durations, Open, Start, Stop, Clear);
        op.setDaemon(true);
        op.start();

    }

}
