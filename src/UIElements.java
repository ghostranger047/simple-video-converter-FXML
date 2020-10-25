import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.List;

public class UIElements
{
    public static TextField textCurrentFile, FrameRate, Crf, CurrentPercent, TotalPercent;
    public static ObservableList<File> files;
    public static String str_format, Preset;

    public static ProgressBar progBarCurrent, progBarTotal;
    public static List<Double> durations;
    public static Button Open, Start, Stop, Clear;
    public static File dest;

    int flag;



    public UIElements(ObservableList<File> files, String str_format, ProgressBar progBarCurrent, ProgressBar progBarTotal,
                      List<Double> durations, Button Open, Button Start, Button Stop, Button Clear, TextField textCurrentFile, File dest, TextField FrameRate,
                       TextField Crf, String Preset, TextField CurrentPercent, TextField TotalPercent)
    {
        this.textCurrentFile = textCurrentFile;

        this.files = files;
        this.str_format =str_format;
        //this.textCurrentFile = textCurrentFile;
        this.progBarCurrent = progBarCurrent;
        this.progBarTotal = progBarTotal;
        this.durations = durations;

        this.Open = Open;
        this.Start = Start;
        this.Stop = Stop;
        this.Clear = Clear;
        this.FrameRate = FrameRate;
        this.dest = dest;
        this.Crf = Crf;
        this.Preset = Preset;
        this.CurrentPercent = CurrentPercent;
        this.TotalPercent = TotalPercent;
        flag = 1;
    }
}
