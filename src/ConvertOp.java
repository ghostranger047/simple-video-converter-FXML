import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConvertOp extends Thread
{
    private ObservableList<File> files;
    private String str_format;
    private TextField textCurrentFile;
    private ProgressBar progBarCurrent, progBarTotal;
    private List<Double> durations;
    private Button Open, Start, Stop, Clear;



    public ConvertOp(ObservableList<File> files, String str_format, TextField textCurrentFile, ProgressBar progBarCurrent, ProgressBar progBarTotal,
                     List<Double> durations, Button Open, Button Start, Button Stop, Button Clear)
    {
        this.files = files;
        this.str_format =str_format;
        this.textCurrentFile = textCurrentFile;
        this.progBarCurrent = progBarCurrent;
        this.progBarTotal = progBarTotal;
        this.durations = durations;

        this.Open = Open;
        this.Start = Start;
        this.Stop = Stop;
        this.Clear = Clear;




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

       double tot_dur = 0.0;
        for(double dub : durations)
        {
            tot_dur += dub;
        }
        System.out.println(tot_dur);
        progBarTotal.setProgress(0.0);

       for(File file : files)
       {
           //System.out.println(tot_dur);

           progBarCurrent.setProgress(0.0);
           String currentLoc = file.getAbsoluteFile().toString();

           textCurrentFile.setText(currentLoc);

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
                    String op = sc.nextLine();
                    //System.out.println(op.length());


                    if(op.indexOf("time=") != -1)
                    {
                        String progress = op.substring(op.indexOf("time=")+5, op.lastIndexOf("time")+13);
                        //System.out.println(progress);
                        String time[] = progress.split(":");
                        double dou_progress = (3600 * Double.parseDouble(time[0])) +
                                (60 * Double.parseDouble(time[1])) + Double.parseDouble(time[2]);
                        //System.out.println(dou_progress);


                        progBarCurrent.setProgress(dou_progress/durations.get(i));

                        if(i == 0)
                        {
                            //System.out.println(dou_progress/tot_dur);
                            progBarTotal.setProgress(dou_progress/tot_dur);
                        }

                        else
                        {
                            //System.out.println((durations.get(i-1) + dou_progress)/tot_dur);
                            progBarTotal.setProgress((durations.get(i-1) + dou_progress)/tot_dur);
                        }

                        //progBarTotal.setProgress(total_done/tot_dur);

                    }

                }
                process.waitFor();
            }
            catch(Exception e)
            {
                //System.out.println(e);
                toggle_buttons(0);
            }
            i = i+1;


       }
       System.out.println("Ended");
       textCurrentFile.setText("Task Completed");
       toggle_buttons(0);

       //files.clear();
    }
}
