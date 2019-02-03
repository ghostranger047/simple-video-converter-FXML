import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FFProbe
{
    public List<Double> get_duration(ObservableList<File> dests) throws IOException, InterruptedException
    {
        List<Double> durations = new ArrayList<>();

        for(File file : dests)
        {
            String current_file = file.getAbsolutePath();
            //System.out.println(current_file);
            List<String> arg;

            String str_arg[] = {"ffprobe", "-i", current_file, "-show_entries", "format=duration", "-v", "quiet"};
            arg = Arrays.asList(str_arg);
            ProcessBuilder p = new ProcessBuilder(str_arg);

            Process process = p.start();
            Scanner sc = new Scanner(process.getInputStream());

            while(sc.hasNextLine())
            {
                String tmp = sc.nextLine();
                if(tmp.indexOf("duration=") != -1)
                {
                    durations.add(Double.parseDouble(tmp.split("=")[1]));
                }

            }
            process.waitFor();
        }
        return durations;
    }
}
