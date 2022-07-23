package ch.zhaw.it.pm2.Soundboard.model;

import ch.zhaw.it.pm2.Soundboard.enums.PlayStates;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * SoundClip Object that offers methods related to handling playback.
 */
public class SoundClip {

    private final Clip clip;
    private final Clip clipForRecording;

    private Long pausedFramePosition = null;
    private boolean canBeRecorded = false;
    private boolean loopClip = false;

    private PlayStates state = PlayStates.READY_TO_PLAY;

    private final Set<SoundClipListener> soundClipEventListeners = new HashSet<>();


    public SoundClip(Clip clip, Clip clipForRecording, boolean canBeRecorded) {
        this.clip = clip;
        this.clipForRecording = clipForRecording;
        this.canBeRecorded = canBeRecorded;

        clip.addLineListener(this::clipLineListener);
    }

    public void setLoop(boolean enableLoop) {
        loopClip = enableLoop;
    }

    public boolean isPlaying() {
        return state == PlayStates.PLAYING;
    }

    public Long getPausedFramePosition() {
        return pausedFramePosition;
    }

    /**
     * Pauses current Clip.
     *
     * @return true if paused, false if not possible or not currently playing
     */
    public boolean pause() {
        if (loopClip) {
            return false;
        }

        if (state == PlayStates.PLAYING) {
            pausedFramePosition = clip.getMicrosecondPosition();
            clip.stop();
            if (clipForRecording != null && canBeRecorded) {
                clipForRecording.stop();
            }
            this.state = PlayStates.PAUSED;
            return true;
        }

        return false;

    }

    /**
     * Sets up all clip data for playing, setting up a paused clip sets playback position where it was paused.
     */
    public void play() {
        if (state == PlayStates.PAUSED) {
            playPausedClip();
        } else {
            playClipFromStart();
        }

        state = PlayStates.PLAYING;

    }

    private void playClipFromStart() {
        pausedFramePosition = null;
        clip.setMicrosecondPosition(0);
        if (clipForRecording != null && canBeRecorded) {
            clipForRecording.setMicrosecondPosition(0);
        }
        playClip();
    }

    private void playPausedClip() {
        clip.setMicrosecondPosition(pausedFramePosition);
        if (clipForRecording != null && canBeRecorded) {
            clipForRecording.setMicrosecondPosition(pausedFramePosition);
        }
        pausedFramePosition = null;
        playClip();
    }

    /**
     * Actually starts playing the clip.
     */
    private void playClip() {
        if (loopClip) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            if (clipForRecording != null && canBeRecorded) {
                clipForRecording.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } else {
            clip.start();
            if (clipForRecording != null && canBeRecorded) {
                clipForRecording.start();
            }
        }
    }

    /**
     * Stops the currently playing clip.
     *
     * @return true if clip was stopped, false if clip was not stopped cause it was not currently playing
     */
    public boolean stop() {
        if (state == PlayStates.PLAYING) {
            clip.stop();
            if (clipForRecording != null && canBeRecorded) {
                clipForRecording.stop();
            }
            state = PlayStates.READY_TO_PLAY;
            pausedFramePosition = null;
            return true;
        }
        pausedFramePosition = null;
        return false;
    }


    public void addListener(SoundClipListener soundClipListener) {
        soundClipEventListeners.add(soundClipListener);
    }

    public void removeListener(SoundClipListener soundClipListener) {
        soundClipEventListeners.remove(soundClipListener);
    }

    private void clipLineListener(LineEvent e) {
        if (state == PlayStates.PLAYING) {
            if (e.getType() == LineEvent.Type.STOP) {
                for (SoundClipListener listener : soundClipEventListeners) {
                    listener.stoppedPlayback();
                }
                state = PlayStates.READY_TO_PLAY;
            }
        }
    }
}
