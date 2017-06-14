package threads.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static final File s_recentFile = new File(new File("."), ".threadsrecent");
    private static final File s_currentFiles = new File(new File("."), ".threadscurrent");

    public static List<File> getCurrentFiles() {
        return loadFromFile(s_currentFiles);
    }

    static List<File> getRecentFiles() {
        return loadFromFile(s_recentFile);
    }

    private static List<File> loadFromFile(File x_file) {
        List<File> x_files = new ArrayList<>();

        if(x_file.exists()) {
            try {
                BufferedReader x_reader = new BufferedReader(new FileReader(x_file));
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
		List<File> x_files = getRecentFiles();

		if(!x_files.contains(x_file)) {
			x_files.add(x_file);
		}

		writeToFile(x_files, s_recentFile);
	}

    static void storeCurrentFiles(List<File> x_files) {
		writeToFile(x_files, FileUtil.s_currentFiles);
	}

	private static void writeToFile(List<File> x_files, File x_file) {
		try {
			BufferedWriter x_writer = new BufferedWriter(new FileWriter(x_file));

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
