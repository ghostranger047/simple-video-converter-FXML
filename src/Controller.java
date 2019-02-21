import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.midi.SysexMessage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    List<String> open_ext;

    @FXML
    private Button Open, Start, Stop, Clear;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField textCurrentFile, Crf, FrameRate, textDest, CurrentPercent, TotalPercent;
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

        CurrentPercent.setEditable(false); TotalPercent.setEditable(false);


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
                textCurrentFile, dest, FrameRate, Crf, Preset.getValue(), CurrentPercent, TotalPercent);

        ui.flag = 1;

        op= new ConvertOp(ui);

        op.setDaemon(true);
        op.start();

    }


    @FXML
    public void stop(ActionEvent event)
    {

        Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alert");
        alert.setContentText("Do you really want to Stop?");
        Optional<ButtonType> result =alert.showAndWait();
        if(result.get() == ButtonType.OK)
        {
            ui.flag = 0;
            op.interrupt();

            textCurrentFile.setText("Interrupted by User!");
            Stop.setDisable(true);
            Open.setDisable(false);
            Start.setDisable(false);
            Clear.setDisable(false);
            progBarCurrent.setProgress(0.0);
            progBarTotal.setProgress(0.0);
            CurrentPercent.setText("0.0%");
            TotalPercent.setText("0.0%");
        }
        else
            return;


    }

    @FXML
    public void set_destination(ActionEvent event)
    {
        DirectoryChooser d =new DirectoryChooser();
        d.setTitle("Destination");



        stg = (Stage)borderPane.getScene().getWindow();
        dest = d.showDialog(stg);
        textDest.setText(dest.getPath());

    }

    @FXML
    public void set_crf(MouseEvent event)
    {
        Crf.setText(Long.toString(Math.round(Slider.getValue())));
    }

    @FXML
    public void close(ActionEvent event)
    {


        if(ConvertOp.process.isAlive() == true)
        {
            Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("An ongoing operation is in progress");
            alert.setContentText("Do you really want to Stop?");
            Optional<ButtonType> result =alert.showAndWait();
            if(result.get() == ButtonType.OK)
            {
                ConvertOp.process.destroy();
                op.interrupt();
                System.exit(0);
            }
            else
                return;
        }
        else
        {
            ConvertOp.process.destroy();
            op.interrupt();
            System.exit(0);
        }


    }

    @FXML
    public void about(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Just a hobby project");
        alert.setContentText("Simple video converter\n" +
                "Uses FFMPEG \n All major video format supported");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
    }

}
