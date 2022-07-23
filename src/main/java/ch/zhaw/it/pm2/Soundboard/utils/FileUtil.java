package ch.zhaw.it.pm2.Soundboard.utils;


import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileUtil {

    private static final Logger logger = Logger.getLogger(FileUtil.class.getName());

    /**
     * Checks weather a File is readable, a file  and exists
     *
     * @param file File to be checked
     * @return Boolean
     */
    public static boolean isValidAudioFile(File file) {

        if (file == null) {
            return false;
        } else if (!file.exists()) {
            return false;
        } else if (!file.canRead()) {
            return false;
        } else if (!file.isFile()) {
            return false;
        } else if (isWav(file)) {
            return true;
        }
        return false;
    }

    public static void saveAudioFile(AudioInputStream audioInputStream, String filePath) {
        try {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(filePath));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing audio to: " + filePath, e);
            e.printStackTrace();
        }
    }

    /**
     * Checks weather a File has the wav extension
     *
     * @param file File to be checked
     * @return Boolean
     */
    private static boolean isWav(File file) {

        if (file != null) {
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            String extension = fileName.substring(dotIndex + 1);
            extension = extension.toLowerCase();

            return extension.equals("wav");
        } else {
            return false;
        }
    }
}
