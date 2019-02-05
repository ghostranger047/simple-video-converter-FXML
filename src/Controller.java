import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
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
    private TextField textCurrentFile, Crf, FrameRate, textDest;
    @FXML
    private ListView<File> listViewFiles;
    @FXML
    private ListView<String> listViewFormats;
    @FXML
    private ProgressBar progBarCurrent, progBarTotal;
    @FXML
    private Slider Slider;
    @FXML
    private ComboBox<String> Preset;

    private File dest;


    private ObservableList<File> files;
    private ObservableList<String> formats;

    private List<Double> list_durations;

    private String str_format;

    private ConvertOp op;

    Stage stg;

    UIElements ui;

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
        Crf.setMaxWidth(40); Crf.setMaxHeight(30);
        Crf.setEditable(false);

        FrameRate.setMaxWidth(40);FrameRate.setMaxHeight(30);
        FrameRate.setText("30");

        Preset.setValue("fast");

        Slider.setValue(22);
        Crf.setText(Integer.toString((int)Slider.getValue()));

        Slider.setMin(0); Slider.setMax(51);
        Slider.setShowTickLabels(true); Slider.setShowTickMarks(true);
        Slider.setMajorTickUnit(22);
        Slider.setMinorTickCount(51);
        Slider.setBlockIncrement(1);

        Preset.getItems().addAll("ultrafast", "superfast", "veryfast", "faster", "fast", "medium", "slow", "slower", "veryslow");



    }

    @FXML
    public void open_file(ActionEvent event)
    {
        System.out.println("open");
        FileChooser fc = new FileChooser();
        fc.setTitle("Videos");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Videos", open_ext));
        stg = (Stage)borderPane.getScene().getWindow();
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

        //ui =new UIElements(files, str_format, progBarCurrent, progBarTotal, list_durations, Open, Start, Stop, Clear, textCurrentFile, dest.getAbsolutePath()+"/");
        //System.out.println(dest.getAbsolutePath());
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


        if(textDest.getText().equals("") || files.isEmpty() == true)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("You may not have made appropriate selections!");
            alert.setContentText("Please make sure you've added at least one file.\n" +
                    "Please check you've selected a destination in \n\"Additional Settings\" Tab.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
            return;
        }


        ui =new UIElements(files, str_format, progBarCurrent, progBarTotal, list_durations, Open, Start, Stop, Clear,
                textCurrentFile, dest, FrameRate, Crf, Preset.getValue());



        op= new ConvertOp(ui);

        op.setDaemon(true);
        op.start();

    }

    @FXML
    public void set_destination(ActionEvent event)
    {
        DirectoryChooser d =new DirectoryChooser();
        d.setTitle("Destination");



        stg = (Stage)borderPane.getScene().getWindow();
        dest = d.showDialog(stg);
        textDest.setText(dest.getPath() + "/");

    }

    @FXML
    public void set_crf(MouseEvent event)
    {
        Crf.setText(Long.toString(Math.round(Slider.getValue())));
    }

}
