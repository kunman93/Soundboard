package ch.zhaw.it.pm2.Soundboard.model;

/**
 * Object to hold Recording info and data used to populate the list view of recordings
 */
public class RecordingData {
    private final SoundClip recording;
    private String name;
    private String recordingAudioPathTemp;

    public RecordingData(String name, SoundClip recording, String recordingAudioPathTemp) {
        this.name = name;
        this.recording = recording;
        this.recordingAudioPathTemp = recordingAudioPathTemp;
    }

    public SoundClip getRecording() {
        return recording;
    }

    public String getName() {
        return name;
    }

    public String getRecordingAudioPathTemp() {
        return recordingAudioPathTemp;
    }

}
