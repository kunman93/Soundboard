package ch.zhaw.it.pm2.Soundboard.recordingListView;

import ch.zhaw.it.pm2.Soundboard.SoundboardUIController;
import ch.zhaw.it.pm2.Soundboard.model.RecordingData;
import ch.zhaw.it.pm2.Soundboard.model.SoundClip;
import ch.zhaw.it.pm2.Soundboard.model.SoundClipListener;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * An object that contains all elements that are used in each element of the ListView.
 */
public class RecordingCell implements SoundClipListener {
    private static final Logger logger = Logger.getLogger(RecordingCell.class.getName());
    private RecordingData recordingData;

    private HBox recordingHBox;
    private Label recordingNameLabel;
    private Button playPauseRecordingButton;

    private final Set<RecordingListener> recordingListeners = new HashSet<>();
    private static SoundboardUIController mainApplication;


    /**
     * Sets all events and fill local variables.
     */
    public RecordingCell() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../RecordingCell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        }

        recordingHBox = fxmlLoader.getRoot();
        setupUI();
    }

    public static void setMainApplication(SoundboardUIController app) {
        mainApplication = app;

    }

    private void setupUI() {
        recordingNameLabel = (Label) recordingHBox.getChildren().stream().filter((child) -> child.getId().equals("recordingNameLabel")).collect(Collectors.toList()).get(0);
        playPauseRecordingButton = (Button) recordingHBox.getChildren().stream().filter((child) -> child.getId().equals("playPauseRecordingButton")).collect(Collectors.toList()).get(0);
        Button stopRecordingButton = (Button) recordingHBox.getChildren().stream().filter((child) -> child.getId().equals("stopRecordingButton")).collect(Collectors.toList()).get(0);
        Button saveRecordingButton = (Button) recordingHBox.getChildren().stream().filter((child) -> child.getId().equals("saveRecordingButton")).collect(Collectors.toList()).get(0);
        Button deleteRecordingButton = (Button) recordingHBox.getChildren().stream().filter((child) -> child.getId().equals("deleteRecordingButton")).collect(Collectors.toList()).get(0);

        playPauseRecordingButton.setOnMouseClicked(this::playPauseSavedRecording);
        stopRecordingButton.setOnMouseClicked(this::stopSavedRecording);
        saveRecordingButton.setOnMouseClicked(this::saveRecording);
        deleteRecordingButton.setOnMouseClicked(this::deleteRecording);

        logger.log(Level.INFO, " Setup for ListView Ui elements has been executed");
    }

    /**
     * Sets data and text value of Gui element.
     *
     * @param recordingData Data to be set.
     */
    public void setData(RecordingData recordingData) {
        this.addListener(mainApplication);
        this.recordingData = recordingData;
        recordingHBox.setVisible(true);
        recordingNameLabel.setText(recordingData.getName());
    }

    private void deleteRecording(MouseEvent e) {
        for (RecordingListener listener : recordingListeners) {
            listener.deleteRecording(this.recordingData);
        }
        this.removeListener(mainApplication);
    }

    public void addListener(RecordingListener recordingListener) {
        recordingListeners.add(recordingListener);
    }

    public void removeListener(RecordingListener recordingListener) {
        recordingListeners.remove(recordingListener);
    }

    public HBox getBox() {
        return recordingHBox;
    }

    public void clear() {
        recordingHBox.setVisible(false);
    }

    /**
     * Handles MouseClick event on Cells in the ListView for playPauseButton.
     *
     * @param e MouseEvent object.
     */
    private void playPauseSavedRecording(MouseEvent e) {

        SoundClip recordingToPlay = recordingData.getRecording();


        if (recordingToPlay.isPlaying()) {
            recordingToPlay.removeListener(this);
            recordingData.getRecording().pause();
            playPauseRecordingButton.setText("Play");
        } else {
            recordingToPlay.addListener(this);
            recordingData.getRecording().play();
            playPauseRecordingButton.setText("Pause");
        }
    }

    /**
     * Handles MouseClick event on Cells in the ListView for StopButton.
     *
     * @param e MouseEvent object.
     */
    private void stopSavedRecording(MouseEvent e) {
        SoundClip recordingToPlay = recordingData.getRecording();
        if (recordingToPlay.isPlaying()) {
            recordingData.getRecording().stop();
            playPauseRecordingButton.setText("Play");
        }
    }

    /**
     * Handles MouseClick event on Cells in the ListView for saveRecordingButton
     *
     * @param mouseEvent MouseEvent object
     */
    private void saveRecording(MouseEvent mouseEvent) {
        // parent component of the dialog
        JFrame parentFrame = new JFrame();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("wav", "wav");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(new File(recordingData.getName()));

        int userSelection = fileChooser.showSaveDialog(parentFrame);

        processJFileChooserSaveRecordingResult(fileChooser, userSelection);
    }

    private void processJFileChooserSaveRecordingResult(JFileChooser fileChooser, int userSelection) {
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getAbsolutePath().endsWith(".wav")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".wav");
            }

            try {
                Files.copy(Path.of(recordingData.getRecordingAudioPathTemp()), Path.of(fileToSave.getAbsolutePath()));
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error copying the temp file to the desired destination: " + fileToSave.getAbsolutePath(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stoppedPlayback() {
        recordingData.getRecording().removeListener(this);
        recordingData.getRecording().stop();
        Platform.runLater(() -> {
            playPauseRecordingButton.setText("Play");
        });
    }
}
