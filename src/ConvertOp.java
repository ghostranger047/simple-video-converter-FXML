import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConvertOp extends Thread
{
    private ObservableList<File> files;
    private String str_format;
    private Label labelCurrentFile;
    private ProgressBar progBarCurrent, progBarTotal;
    private List<Double> durations;
    private Button Open, Start, Stop, Clear;


    public ConvertOp(ObservableList<File> files, String str_format, Label labelCurrentFile, ProgressBar progBarCurrent, ProgressBar progBarTotal,
                     List<Double> durations, Button Open, Button Start, Button Stop, Button Clear)
    {
        this.files = files;
        this.str_format =str_format;
        this.labelCurrentFile = labelCurrentFile;
        this.progBarCurrent = progBarCurrent;
        this.progBarTotal = progBarTotal;
        this.durations = durations;

        this.Open = Open;
        this.Start = Start;
        this.Stop = Stop;
        this.Clear = Clear;
        //progBarCurrent.setProgress(45/100.0);


    }
    public void toggle_buttons(int n)
    {

       Boolean fl1, fl2;
       if(n == 1)
        {
            fl1 = true; fl2 = false;
        }
       else
        {
            fl1 = false; fl2 = true;
        }
       Open.setDisable(fl1);
       Start.setDisable(fl1);
       Clear.setDisable(fl1);
       Stop.setDisable(fl2);

    }


    public void run()
    {

       toggle_buttons(1);
       int i = 0;
       for(File file : files)
       {

           String currentLoc = file.getAbsoluteFile().toString();
           Double current_duration = durations.get(i);

           labelCurrentFile.setText(currentLoc);

           List<String> arg = new ArrayList<>();
           String currentFile = currentLoc.substring(currentLoc.lastIndexOf('/')+1);
           currentFile = currentFile.substring(0, currentFile.lastIndexOf('.'));

           arg.add("ffmpeg");
           arg.add("-i"); arg.add(currentLoc); arg.add("/home/avenger047/Desktop/" + currentFile + "." + str_format);
           ProcessBuilder p = new ProcessBuilder(arg);
           System.out.println(currentFile);




            try
            {
                Process process = p.start();
                Scanner sc = new Scanner(process.getErrorStream());

                while(sc.hasNextLine())
                {
                    System.out.println(sc.nextLine());
                }
                process.waitFor();
            }
            catch(Exception e)
            {
                System.out.println(e);
                toggle_buttons(0);
            }

            i++;

       }
       System.out.println("Ended");
       toggle_buttons(0);
       //files.clear();
    }
}
