package threads.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static final File s_recentFile = new File(new File("."), ".threadsrecent");

    static List<File> getRecentFiles() {
        List<File> x_files = new ArrayList<>();

        if(s_recentFile.exists()) {
            try {
                BufferedReader x_reader = new BufferedReader(new FileReader(s_recentFile));
                String x_line;

                while((x_line = x_reader.readLine()) != null) {
                    File x_recentFile = new File(x_line);

                    if(x_recentFile.exists()) {
                        x_files.add(x_recentFile.getAbsoluteFile());
                    }
                }

                x_reader.close();
            } catch (Exception e) {
                // do nothing
            }
        }

        return x_files;
    }

    static void storeRecentFile(File x_file) {
        try {
            List<File> x_files = getRecentFiles();
            BufferedWriter x_writer = new BufferedWriter(new FileWriter(s_recentFile));

            if(!x_files.contains(x_file)) {
                x_files.add(x_file);
            }

            for(File x_f : x_files) {
                x_writer.write(x_f.getAbsolutePath());
                x_writer.newLine();
            }

            x_writer.flush();
            x_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logToFile(File p_file, String p_entry) {
        try {
            BufferedWriter x_writer = new BufferedWriter(new FileWriter(p_file, true));
            x_writer.write(p_entry);
            x_writer.newLine();
            x_writer.flush();
            x_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
