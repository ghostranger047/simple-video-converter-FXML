import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.io.File;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;



public class ConvertOp extends Thread
{

    UIElements Ui;

    public static Process process = new Process()
    {
        @Override
        public OutputStream getOutputStream()
        {
            return null;
        }

        @Override
        public InputStream getInputStream()
        {
            return null;
        }

        @Override
        public InputStream getErrorStream()
        {
            return null;
        }

        @Override
        public int waitFor() throws InterruptedException
        {
            return 0;
        }

        @Override
        public int exitValue()
        {
            return 0;
        }

        @Override
        public void destroy()
        {

        }
    };



    public ConvertOp(UIElements ui)
    {

        this .Ui = ui;

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
       Ui.Open.setDisable(fl1);
       Ui.Start.setDisable(fl1);
       Ui.Clear.setDisable(fl1);
       Ui.Stop.setDisable(fl2);

    }


    public void run()
    {

       //System.out.println(Ui.durations);
       toggle_buttons(1);
       int i = 0;

       double tot_dur = 0.0;
        for(double dub : Ui.durations)
        {
            tot_dur += dub;
        }
        System.out.println(tot_dur);
        Ui.progBarTotal.setProgress(0.0);

       for(File file : Ui.files)
       {
           //System.out.println(tot_dur);

           Ui.progBarCurrent.setProgress(0.0);
           String currentLoc = file.getAbsoluteFile().toString();
           String dir = "";
           String os =System.getProperty("os.name").toLowerCase();
           System.out.println(os.indexOf("windows"));

           List<String> arg = new ArrayList<>();

           if(os.indexOf("windows") != -1)
           {
               dir = "\\";
               arg.add(System.getProperty("user.dir") + "\\ffmpeg\\bin\\ffmpeg.exe");

           }

           else
           {
               dir = "/";
               arg.add("ffmpeg");
           }
           //ffmpeg -vaapi_device /dev/dri/renderD128 -i input.mp4 -vf 'format=nv12,hwupload' -c:v h264_vaapi output.mp4

           Ui.textCurrentFile.setText(currentLoc);

           String currentFile = currentLoc.substring(currentLoc.lastIndexOf(dir)+1);
           currentFile = currentFile.substring(0, currentFile.lastIndexOf('.'));



           new File(Ui.dest.getPath() + dir + "SVC" + dir).mkdir();

           String str_op = Ui.dest.getPath() + dir + "SVC" + dir  + currentFile + "." + Ui.str_format;

           String args = "";

           arg.add("-i");
           arg.add(currentLoc);
           if(Ui.gpu.isSelected() == false)
                args = "-c:v libx264 -preset " + Ui.Preset +
                   " -crf " + Ui.Crf.getText() + " -r " + Ui.FrameRate.getText();
           else
                args = "-vaapi_device /dev/dri/renderD128 -vf format=nv12,hwupload -c:v h264_vaapi -preset " + Ui.Preset +
                        " -crf " + Ui.Crf.getText() + " -r " + Ui.FrameRate.getText();

           arg.addAll(Arrays.asList(args.split(" ")));
           arg.add(str_op);




           System.out.println(arg);
           ProcessBuilder p = new ProcessBuilder(arg);
           //System.out.println(currentFile);



           try
            {
                process = p.start();
                Scanner sc = new Scanner(process.getErrorStream());

                while(sc.hasNextLine())
                {
                    String op = sc.nextLine();
                    System.out.println(op);
                    if(Ui.flag == 0)
                    {
                        process.destroy();
                        return;
                    }


                    if(op.indexOf("time=") != -1)
                    {
                        String progress = op.substring(op.indexOf("time=")+5, op.lastIndexOf("time")+13);
                        //System.out.println(progress);
                        String time[] = progress.split(":");
                        double dou_progress = (3600 * Double.parseDouble(time[0])) +
                                (60 * Double.parseDouble(time[1])) + Double.parseDouble(time[2]);



                        Double val_current = dou_progress/Ui.durations.get(i);

                        Ui.progBarCurrent.setProgress(val_current);
                        Ui.CurrentPercent.setText(Double.toString(val_current*100.0).substring(0,5) +"%");

                        if(i == 0)
                        {
                            //System.out.println(dou_progress/tot_dur);
                            Ui.progBarTotal.setProgress(dou_progress/tot_dur);
                            Ui.TotalPercent.setText(Double.toString((dou_progress)/tot_dur*100.0).substring(0,5) + "%");
                        }

                        else
                        {
                            //System.out.println((durations.get(i-1) + dou_progress)/tot_dur);
                            double tmp=0.0;
                            for(int j=0;j <i;j++)
                                tmp += Ui.durations.get(j);

                            Ui.progBarTotal.setProgress((tmp + dou_progress)/tot_dur);
                            Ui.TotalPercent.setText(Double.toString((tmp + dou_progress)/tot_dur*100.0).substring(0,5) + "%");
                        }

                        //progBarTotal.setProgress(total_done/tot_dur);

                    }

                }

            }
            catch(Exception e)
            {
                //System.out.println(e);
                toggle_buttons(0);
            }
           try
           {
               process.waitFor();
           } catch (InterruptedException e)
           {
               e.printStackTrace();
           }
           i = i+1;


       }
       System.out.println("Ended");
       Ui.textCurrentFile.setText("Task Completed");
       Ui.CurrentPercent.setText("100%");
       Ui.TotalPercent.setText("100%");
       toggle_buttons(0);

       //files.clear();
    }


}
