package ch.zhaw.it.pm2.Soundboard.utils;

import ch.zhaw.it.pm2.Soundboard.model.SoundClip;
import com.google.common.io.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests The MediaUtil-Class.
 */
class MediaUtilTest {

    File file;
    SoundClip soundClip;

    @BeforeEach
    void setUp() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        MediaUtil.cleanTempFolder();
        MediaUtil.initializeRecordingMixers();
        file = new File("src/main/resources/ch/zhaw/it/pm2/Soundboard/defaultSounds/default-background.wav");
        soundClip = MediaUtil.loadClip(file, true);
    }

    /**
     * Tests if all files will be removed from temp-folder successfully.
     * @throws IOException
     */
    @Test
    void cleanTempFolderTest() throws IOException {
        String tempFolder = "src/main/resources/ch/zhaw/it/pm2/Soundboard/temp";
        File tempDir = new File(tempFolder);
        tempDir.mkdir();
        File fileWithRelativePath = new File(tempDir, "newFile.txt");

        assertFalse(fileWithRelativePath.exists());

        Files.touch(fileWithRelativePath);

        assertTrue(fileWithRelativePath.exists());

        MediaUtil.cleanTempFolder();

        assertFalse(fileWithRelativePath.exists());
    }

    /**
     * Tests if a valid file can be loaded.
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    @Test
    void loadValidClipTest() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        file = new File("src/main/resources/ch/zhaw/it/pm2/Soundboard/defaultSounds/default-background.wav");
        soundClip = MediaUtil.loadClip(file, false);
        assertNotNull(soundClip);
    }

    /**
     * Tests if an invalid file can be loaded. Expects an UnsupportedAudioFileException.
     */
    @Test
    void loadInvalidClipTest() {
        file = new File ("src/test/resources/unsupportedAudioFile.txt");
        assertThrows(UnsupportedAudioFileException.class, () -> MediaUtil.loadClip(file, false) );
    }

    /**
     * Tests if MediaUtil.loadClip can handle fictional paths.
     */
    @Test
    void loadClipFromInvalidPathTest(){
        assertThrows(IOException.class, () -> MediaUtil.loadClip(new File("src/fictionalPath/fiction.wav"), false) );
    }

    /**
     * Tests the start and stop Recording-Feature.
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    @Test
    void startAndStopRecordingTest() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        SoundClip recordedAudioClip = null;

        boolean recordingSetupSuccess = MediaUtil.initializeRecordingMixers();

        if(!recordingSetupSuccess){
            //If Virtual cable is not installed we simply fail the test
            fail("Virtual Audio Cable not found. Install the software and test again.");
        }

        new Thread(MediaUtil::startRecording).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        String recordedAudioTempPath =  MediaUtil.stopRecording();

        assertNotNull(recordedAudioTempPath);

        recordedAudioClip = MediaUtil.loadClip(new File(recordedAudioTempPath), false);
        assertNotNull(recordedAudioClip);
    }
}