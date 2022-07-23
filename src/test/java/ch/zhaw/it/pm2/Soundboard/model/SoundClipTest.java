package ch.zhaw.it.pm2.Soundboard.model;

import ch.zhaw.it.pm2.Soundboard.utils.MediaUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the SoundClip-Class.
 */
class SoundClipTest {

    private File file;
    private SoundClip soundClip;

    @BeforeEach
    void setUp() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        file = new File("src/main/resources/ch/zhaw/it/pm2/Soundboard/defaultSounds/default-background.wav");
        soundClip = MediaUtil.loadClip(file, false);
    }

    /**
     * Tests if a clip which is not being played can be played successfully.
     */
    @Test
    void playClipWhichIsNotBeingPlayedTest(){
        soundClip.play();
        assertTrue(soundClip.isPlaying());
        assertNull(soundClip.getPausedFramePosition());
    }

    /**
     * Tests if a clip which is being played can be paused successfully. This test expects that
     * the soundClip.pause()-method should return true.
     */
    @Test
    void pauseClipWhichIsBeingPlayedTest(){
        soundClip.play();
        assertTrue(soundClip.pause());
        assertFalse(soundClip.isPlaying());
        assertNotEquals(null, soundClip.getPausedFramePosition());
    }

    /**
     * Tests if a loop clip which is being played can be paused successfully. This test expects that
     * the soundClip.pause()-method should return false.
     */
    @Test
    void pauseLoopClipWhichIsBeingPlayedTest(){
        soundClip.setLoop(true);
        soundClip.play();
        assertFalse(soundClip.pause());
        assertTrue(soundClip.isPlaying());
        assertEquals(null, soundClip.getPausedFramePosition());
    }

    /**
     * Tests if a clip which is not being played can be paused successfully. This test expects that
     * the soundClip.pause() should return false, because the soundClip is not currently playing.
     */
    @Test
    void pauseClipWhichIsNotBeingPlayedTest(){
        assertFalse(soundClip.pause());
        assertFalse(soundClip.isPlaying());
        assertNull(soundClip.getPausedFramePosition());
    }

    /**
     * Tests what happens when a pause is performed on a clip which has been already stopped. This test expects that
     * the soundClip.stop() should return false.
     */
    @Test
    void pauseClipWhichIsAlreadyPausedTest(){
        soundClip.play();
        soundClip.pause();
        assertFalse(soundClip.pause());
        assertFalse(soundClip.isPlaying());
        assertNotEquals(null, soundClip.getPausedFramePosition());
    }

    /**
     * Tests if a clip which is being played can be stopped successfully. This test expects that
     * the soundClip.stop() should return true.
     */
    @Test
    void stopClipWhichIsBeingPlayedTest(){
        soundClip.play();
        assertTrue(soundClip.stop());
        assertFalse(soundClip.isPlaying());
        assertNull(soundClip.getPausedFramePosition());
    }

    /**
     * Tests if a clip which is being played can be stopped successfully. This test expects that
     * the soundClip.stop() should return true.
     */
    @Test
    void stopLoopClipWhichIsBeingPlayedTest(){
        soundClip.setLoop(true);
        soundClip.play();
        assertTrue(soundClip.stop());
        assertFalse(soundClip.isPlaying());
        assertNull(soundClip.getPausedFramePosition());
    }

    /**
     * Tests if a paused clip can be stopped successfully.
     */
    @Test
    void stopAPausedClip(){
        soundClip.play();
        soundClip.pause();
        assertNotNull(soundClip.getPausedFramePosition());
        soundClip.stop();
        assertFalse(soundClip.isPlaying());
        assertNull(soundClip.getPausedFramePosition());
    }

    /**
     * Tests what happens when a stop is performed on a clip which has been already stopped. This test expects that
     * the soundClip.stop() should return false.
     */
    @Test
    void stopClipWhichIsAlreadyStopped(){
        soundClip.play();
        soundClip.stop();
        assertFalse(soundClip.stop());
        assertFalse(soundClip.isPlaying());
        assertNull(soundClip.getPausedFramePosition());
    }
}