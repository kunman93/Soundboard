package ch.zhaw.it.pm2.Soundboard.utils;

import ch.zhaw.it.pm2.Soundboard.model.SoundClip;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Utility Class that supplies methods relating to Audio handling
 */
public class MediaUtil {
    private static final Logger logger = Logger.getLogger(MediaUtil.class.getName());
    private static Mixer mixerForRecordingVirtualSource;
    private static Mixer mixerForRecordingVirtualTarget;
    private static TargetDataLine targetDataLineForRecording = null;

    private static final String tempFolder = "src/main/resources/ch/zhaw/it/pm2/Soundboard/temp";
    private static final String tempFilePath = tempFolder + "/temp_record";
    private static int tempFileNumber = 1;
    private static final AudioFormat recordingFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED, 44100.0F,
            16, 2, 4, 44100.0F, false);

    private static String audioTempFilePathOfRecording = null;
    private static final Object mutexForTempFilePath = new Object();

    /**
     * Sets up Target and source mixers for recording
     */
    public static boolean initializeRecordingMixers() {
        Mixer.Info[] mixerInfoArray = AudioSystem.getMixerInfo();
        boolean isSetup = setupVirtualCableInput(mixerInfoArray);
        isSetup = isSetup && setupVirtualCableOutput(mixerInfoArray);
        return isSetup;
    }

    private static boolean setupVirtualCableOutput(Mixer.Info[] mixerInfoArray) {
        List<Mixer.Info> cableOutputMixers = Arrays.stream(mixerInfoArray).filter((info) -> info.getName().toLowerCase().contains("CABLE Output (VB-Audio Virtual".toLowerCase()) && info.getDescription().toLowerCase().contains("Direct Audio Device: DirectSound Capture".toLowerCase())).collect(Collectors.toList());
        if (cableOutputMixers.size() > 0) {
            mixerForRecordingVirtualTarget = AudioSystem.getMixer(cableOutputMixers.get(0));
            return true;
        } else {
            logger.log(Level.INFO, "Setup for Virtual output has failed");
            return false;
        }
    }

    private static boolean setupVirtualCableInput(Mixer.Info[] mixerInfoArray) {
        List<Mixer.Info> cableInputMixers = Arrays.stream(mixerInfoArray).filter((info) -> info.getName().toLowerCase().contains("CABLE Input (VB-Audio Virtual Cable)".toLowerCase())).collect(Collectors.toList());
        if (cableInputMixers.size() > 0) {
            mixerForRecordingVirtualSource = AudioSystem.getMixer(cableInputMixers.get(0));
            return true;
        } else {
            logger.log(Level.INFO, "Setup for Virtual Input has failed");
            return false;
        }
    }

    /**
     * Clean the temp folder
     */
    public static void cleanTempFolder() {
        File tempDir = new File(tempFolder);
        if (tempDir.exists()) {
            for (File tempFile : Objects.requireNonNull(tempDir.listFiles())) {
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException e) {
                    logger.log(Level.SEVERE, null, e);
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Loads a SoundClip from a File
     *
     * @param audioFile     File to be loaded from
     * @param canBeRecorded Determines weather SoundClip can/should be recorded
     * @return SoundClip that was loaded
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public static SoundClip loadClip(File audioFile, boolean canBeRecorded) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        if (audioFile == null) {
            logger.log(Level.SEVERE, "MediaUtil LoadClip IllegalArgumentException ");
            throw new IllegalArgumentException();
        }

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile.getAbsoluteFile());
        AudioInputStream audioInputStreamForRecordingForRecording = AudioSystem.getAudioInputStream(audioFile.getAbsoluteFile());
        return getSoundClip(canBeRecorded, audioInputStream, audioInputStreamForRecordingForRecording);
    }


    private static SoundClip getSoundClip(boolean canBeRecorded, AudioInputStream audioInputStream, AudioInputStream audioInputStreamForRecording) throws LineUnavailableException, IOException {
        Clip loadedClip;
        Clip recordingClip = null;

        loadedClip = AudioSystem.getClip();
        loadedClip.open(audioInputStream);

        if (mixerForRecordingVirtualSource != null && canBeRecorded) {
            recordingClip = AudioSystem.getClip(mixerForRecordingVirtualSource.getMixerInfo());
            recordingClip.open(audioInputStreamForRecording);
        } else {
            if (audioInputStreamForRecording != null) {
                audioInputStreamForRecording.close();
            }
        }

        return new SoundClip(loadedClip, recordingClip, canBeRecorded);
    }

    /**
     * Starts recording of all audio output we generate except Sound clips with CanRecord==false
     *
     * @return true if recoding was successfull, instantly returns false if recoding was already running
     */
    public static boolean startRecording() {
        if (targetDataLineForRecording == null) {

            try {
                AudioInputStream audioInputStream = setupTargetDataLineAndInputStream();

                createTempFolderIfNotExists();

                String pathForThisRecording = tempFilePath + tempFileNumber + ".wav";
                tempFileNumber++;

                deleteExistingTempFile(pathForThisRecording);

                recordAudio(audioInputStream, pathForThisRecording);

            } catch (LineUnavailableException | IOException e) {
                logger.log(Level.SEVERE, null, e);
                targetDataLineForRecording = null;
                e.printStackTrace();
            }

            return true;

        } else {
            return false;
        }


    }

    private static AudioInputStream setupTargetDataLineAndInputStream() throws LineUnavailableException {
        targetDataLineForRecording = AudioSystem.getTargetDataLine(recordingFormat, mixerForRecordingVirtualTarget.getMixerInfo());
        targetDataLineForRecording.open(recordingFormat);
        targetDataLineForRecording.start();

        return new AudioInputStream(targetDataLineForRecording);
    }

    private static void recordAudio(AudioInputStream audioInputStream, String pathForThisRecording) throws IOException {
        audioTempFilePathOfRecording = null;

        //while recording we will be stuck here until we close the TargetDataLine
        FileUtil.saveAudioFile(audioInputStream, pathForThisRecording);
        audioInputStream.close();


        synchronized (mutexForTempFilePath) {
            //Could Move this to bottom but we don't want that cause AudioSystem.write could throw an exception and the file would not exist
            audioTempFilePathOfRecording = pathForThisRecording;

            //Notify so the stopRecordingFunction can copy the TempFile with the audioTempFilePathOfRecording
            mutexForTempFilePath.notify();
        }
    }

    private static void deleteExistingTempFile(String pathForThisRecording) {
        File tempFile = new File(pathForThisRecording);
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    private static void createTempFolderIfNotExists() {
        File tempDir = new File(tempFolder);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
    }


    /**
     * Stops the currently running Recording
     *
     * @return String temp path for recorded audio wav file, null if error or no recording was running
     */
    public static String stopRecording() {
        if (targetDataLineForRecording == null) {
            return null;
        } else {
            targetDataLineForRecording.close();
            targetDataLineForRecording.flush();
            targetDataLineForRecording = null;
        }

        synchronized (mutexForTempFilePath) {
            if (audioTempFilePathOfRecording == null) {
                try {
                    //Wait until audioTempFilePathOfRecording is ready in startRecording :)
                    mutexForTempFilePath.wait();
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "MediaUtil LoadClip InterruptedException ");
                    e.printStackTrace();
                }
            }
        }

        return audioTempFilePathOfRecording;


    }

}
