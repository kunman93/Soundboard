package ch.zhaw.it.pm2.Soundboard;

import ch.zhaw.it.pm2.Soundboard.model.Metronome;
import ch.zhaw.it.pm2.Soundboard.model.RecordingData;
import ch.zhaw.it.pm2.Soundboard.model.SoundButton;
import ch.zhaw.it.pm2.Soundboard.model.SoundClip;
import ch.zhaw.it.pm2.Soundboard.recordingListView.ListViewRecordingCell;
import ch.zhaw.it.pm2.Soundboard.recordingListView.RecordingCell;
import ch.zhaw.it.pm2.Soundboard.recordingListView.RecordingListener;
import ch.zhaw.it.pm2.Soundboard.utils.FileUtil;
import ch.zhaw.it.pm2.Soundboard.utils.MediaUtil;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SoundboardUIController extends Application implements RecordingListener {

    private static final Logger logger = Logger.getLogger(SoundboardUIController.class.getName());

    private static final String SOUND_BUTTON_STYLE_CLASS = "sound-button";
    private static ObservableList<RecordingData> observableList = FXCollections.observableArrayList();
    private static List<RecordingData> recordingCells = new ArrayList<>();
    private boolean isRecording = false;
    private static boolean isPlayingMetronome = false;

    private KeyCode keyCodeButton1 = KeyCode.NUMPAD7;
    private KeyCode keyCodeButton2 = KeyCode.NUMPAD8;
    private KeyCode keyCodeButton3 = KeyCode.NUMPAD9;
    private KeyCode keyCodeButton4 = KeyCode.NUMPAD4;
    private KeyCode keyCodeButton5 = KeyCode.NUMPAD5;
    private KeyCode keyCodeButton6 = KeyCode.NUMPAD6;
    private KeyCode keyCodeButton7 = KeyCode.NUMPAD1;
    private KeyCode keyCodeButton8 = KeyCode.NUMPAD2;
    private KeyCode keyCodeButton9 = KeyCode.NUMPAD3;
    private static List<KeyCode> soundButtonKeyCodes = new ArrayList<>();

    @FXML
    private Button startStopRecordingButton;
    @FXML
    private ListView<RecordingData> listView;

    private static List<SoundButton> soundButtons = new ArrayList<>();

    private static List<ContextMenu> soundButtonContextMenus = new ArrayList<>();

    private static List<Label> soundButtonLabels = new ArrayList<>();

    // Background Sample datafields
    private SoundClip backgroundSample;
    @FXML
    private Button playStopBackgroundSampleButton;
    @FXML
    private Button loadSampleButton;
    @FXML
    private Label backgroundSampleLabel;

    // Metronome datafields
    private static final Metronome metronome = new Metronome();
    @FXML
    private Slider metronomeSlider;
    @FXML
    private Label metronomeSpeedLabel;
    @FXML
    private Button metronomeStartStopButton;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        mainWindow(primaryStage);


    }

    private void mainWindow(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SoundboardUI.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("ch/zhaw/it/pm2/Soundboard/stylesheet.css");

            primaryStage.setTitle("Soundboard BetterMouret");
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();

            setSoundButtonList(scene);
            setSoundButtonLabelList();
            setSoundButtonKeyCodes();
            setSoundButtonContextMenus();
            setStyleClassForSoundButtons();

            primaryStage.show();

            primaryStage.setMinHeight(765);
            primaryStage.setMinWidth(690);


            boolean recordingSetupResult = MediaUtil.initializeRecordingMixers();
            if (!recordingSetupResult) {
                disableRecordButtonAndShowVirtualCableSetupMessage(scene);
            }

            MediaUtil.cleanTempFolder();
            RecordingCell.setMainApplication(this);
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }

    private void disableRecordButtonAndShowVirtualCableSetupMessage(Scene scene) {
        logger.log(Level.INFO, "Virtual Audio Cable Driver not found --> disabling recording and showing alert");

        scene.lookup("#startStopRecordingButton").setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("VB-CABLE Driver missing");
        alert.setHeaderText(null);
        alert.setContentText("For the recording functionality the VB-CABLE Driver is required. Download at https://www.vb-audio.com/Cable/ and install. :)\n The record button is now disabled. After installing restart the Soundboard and record your Music!");
        alert.showAndWait();

    }

    private void setSoundButtonContextMenus() {
        for (int i = 0; i < soundButtons.size(); i++) {
            soundButtonContextMenus.add(soundButtons.get(i).getContextMenu());
        }
    }

    private void setSoundButtonKeyCodes() {
        soundButtonKeyCodes.add(keyCodeButton1);
        soundButtonKeyCodes.add(keyCodeButton2);
        soundButtonKeyCodes.add(keyCodeButton3);
        soundButtonKeyCodes.add(keyCodeButton4);
        soundButtonKeyCodes.add(keyCodeButton5);
        soundButtonKeyCodes.add(keyCodeButton6);
        soundButtonKeyCodes.add(keyCodeButton7);
        soundButtonKeyCodes.add(keyCodeButton8);
        soundButtonKeyCodes.add(keyCodeButton9);
    }

    private void setSoundButtonLabelList() {
        for (int i = 0; i < soundButtons.size(); i++) {
            soundButtonLabels.add((Label) soundButtons.get(i).getGraphic().lookup("#soundButton" + (i + 1) + "Label"));
        }
    }

    private void setSoundButtonList(Scene scene) {
        soundButtons.add((SoundButton) scene.lookup("#soundButton1"));
        soundButtons.add((SoundButton) scene.lookup("#soundButton2"));
        soundButtons.add((SoundButton) scene.lookup("#soundButton3"));
        soundButtons.add((SoundButton) scene.lookup("#soundButton4"));
        soundButtons.add((SoundButton) scene.lookup("#soundButton5"));
        soundButtons.add((SoundButton) scene.lookup("#soundButton6"));
        soundButtons.add((SoundButton) scene.lookup("#soundButton7"));
        soundButtons.add((SoundButton) scene.lookup("#soundButton8"));
        soundButtons.add((SoundButton) scene.lookup("#soundButton9"));

    }

    private void setStyleClassForSoundButtons() {
        for (SoundButton soundButton : soundButtons) {
            soundButton.getStyleClass().add(SOUND_BUTTON_STYLE_CLASS);
        }
    }

    /**
     * Explicitly stops the metronome when the Application stops.
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();

        if (isPlayingMetronome) {
            metronome.stop();
        }

    }

    @FXML
    private void handleSoundButton(ActionEvent event) {
        SoundButton source = (SoundButton) event.getSource();

        if (source.getFile() != null) {
            SoundClip toPlay = null;
            try {
                toPlay = MediaUtil.loadClip(source.getFile(), true);
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                handleLoadClipError(e);
            }
            if (toPlay != null) {
                toPlay.play();
            }
        }
    }

    private void handleLoadClipError(Exception e) {
        logger.log(Level.SEVERE, e.getClass().toString() + " when loading clip...", e);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error playing the sound");
        alert.setHeaderText(null);
        alert.setContentText("An error has occurred while trying to play the sound of the sound button.\n\n" + e.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void handleDragDropped(DragEvent event) {
        if (event.getSource().getClass().equals(SoundButton.class)) {

            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                SoundButton source = (SoundButton) event.getSource();
                if (FileUtil.isValidAudioFile(file)) {
                    setFileOnButton(file, source);
                } else {
                    alertIncompatibleFile();
                }
            }
        }
    }

    private void setFileOnButton(File file, SoundButton source) {
        source.setFile(file);
        source.setText(file.getName());
    }

    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
    }

    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        for (int i = 0; i < soundButtonKeyCodes.size(); i++) {
            if (keyEvent.getCode() == soundButtonKeyCodes.get(i)) {
                soundButtons.get(i).getStyleClass().remove("hotkey_pressed");
                soundButtons.get(i).getStyleClass().add("hotkey_pressed");
                soundButtons.get(i).fire();

                keyEvent.consume();
            }
        }
    }


    @FXML
    private void handleKeyRelease(KeyEvent keyEvent) {
        for (int i = 0; i < soundButtonKeyCodes.size(); i++) {
            if (keyEvent.getCode() == soundButtonKeyCodes.get(i)) {
                soundButtons.get(i).getStyleClass().remove("hotkey_pressed");
                keyEvent.consume();
            }
        }

    }


    @FXML
    private void handleDropOnLoadSampleButton(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        if (dragboard.hasFiles()) {
            File dragAndDroppedFile = dragboard.getFiles().get(0);
            if (FileUtil.isValidAudioFile(dragAndDroppedFile)) {
                setBackgroundSample(dragAndDroppedFile);
            } else {
                alertIncompatibleFile();
            }
        }
    }

    @FXML
    private void handleLoadFile(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        File selectedBackgroundSampleFile = fileChooser.showOpenDialog(null);

        if (selectedBackgroundSampleFile == null) {
            return;
        }

        if (FileUtil.isValidAudioFile(selectedBackgroundSampleFile)) {
            setBackgroundSample(selectedBackgroundSampleFile);
        } else {
            alertIncompatibleFile();
        }
    }

    private void alertIncompatibleFile() {
        logger.log(Level.INFO, "Tried to add Incompatible File, printed alert");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File not compatible");
        alert.setHeaderText(null);
        alert.setContentText("The File that was just selected was either not a file at all or not a wav file.");
        alert.showAndWait();
    }

    private void setBackgroundSample(File selectedBackgroundSampleFile) {
        try {
            backgroundSampleLabel.setText(selectedBackgroundSampleFile.getName());
            backgroundSample = MediaUtil.loadClip(selectedBackgroundSampleFile, true);
            backgroundSample.setLoop(true);
            playStopBackgroundSampleButton.setDisable(false);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            logger.log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePlayOrStopBackgroundSampleButton(ActionEvent event) {
        if (!backgroundSample.isPlaying()) {
            loadSampleButton.setDisable(true);
            backgroundSample.play();
            if (isPlayingMetronome) {
                metronome.stop();
                metronome.start((int) metronomeSlider.getValue());
            }
            playStopBackgroundSampleButton.setText("Stop");
        } else {
            backgroundSample.stop();
            loadSampleButton.setDisable(false);
            playStopBackgroundSampleButton.setText("Play");
        }
    }

    @FXML
    private void handleMetronomeSliderMouseDragged(MouseEvent event) {
        metronomeSlider.valueProperty().addListener((observable, oldValue, newValue) -> metronomeSpeedLabel.setText("Speed: " + Math.round((Double) newValue * 0.5) / 0.5f + "bpm"));
        metronome.setTempo((int) metronomeSlider.getValue());
    }

    @FXML
    private void handleMetronomeSliderMouseClicked(MouseEvent event) {
        metronomeSpeedLabel.setText("Speed: " + Math.round((Double) metronomeSlider.getValue() * 0.5) / 0.5f + "bpm");
        metronome.setTempo((int) metronomeSlider.getValue());
    }

    @FXML
    private void handleStartMetronomeButton(ActionEvent event) {
        if (!isPlayingMetronome) {
            metronome.start((int) metronomeSlider.getValue());
            metronomeStartStopButton.setText("Stop");
        } else {
            metronome.stop();
            metronomeStartStopButton.setText("Start");
        }
        isPlayingMetronome = !isPlayingMetronome;
    }

    @FXML
    private void loadDefaultSoundSet() {
        setBackgroundSample(new File("src/main/resources/ch/zhaw/it/pm2/Soundboard/defaultSounds/default-background.wav"));

        for (int i = 0; i < soundButtons.size(); i++) {
            setFileOnButton(new File("src/main/resources/ch/zhaw/it/pm2/Soundboard/defaultSounds/default" + (i + 1) + ".wav"), soundButtons.get(i));
        }
        logger.log(Level.INFO, "Default Sounds loaded!");
    }

    @FXML
    private void startStopRecording() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        if (isRecording) {
            String recordedAudioTempPath = MediaUtil.stopRecording();
            if (recordedAudioTempPath == null) {

                //Should never happen. cant really tell anything else to the user ^^
                //If it happens there are so many reasons...
                logger.log(Level.SEVERE, "Error stopping recording recordedAudioTempPath was null. The temp file cant be loaded.");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error stop recording");
                alert.setHeaderText(null);
                alert.setContentText("We are sorry!\n Error stop recording. Please try again. :( ");
                alert.showAndWait();
                return;
            }

            SoundClip recordedAudioClip = MediaUtil.loadClip(new File(recordedAudioTempPath), false);

            String nameOfRecording = askUserToEnterRecordingName();

            RecordingData recordingData = new RecordingData(nameOfRecording, recordedAudioClip, recordedAudioTempPath);
            recordingCells.add(recordingData);
            setListView();

            startStopRecordingButton.setText("Start Recording");
        } else {
            new Thread(MediaUtil::startRecording).start();
            startStopRecordingButton.setText("Stop Recording");
        }

        isRecording = !isRecording;
    }


    @Override
    public void deleteRecording(RecordingData recordingData) {
        logger.log(Level.INFO, "Deleting record \"" + recordingData.getName() + "\"");
        recordingData.getRecording().stop();
        recordingCells.remove(recordingData);
        observableList.setAll(recordingCells);
    }

    private String askUserToEnterRecordingName() {
        TextInputDialog dialog = new TextInputDialog("Recording");
        dialog.setTitle("Recording Text Input Dialog");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the name of your recording: ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return dialog.getDefaultValue();
    }

    private void setListView() {
        observableList.setAll(recordingCells);
        listView.setItems(observableList);
        listView.setCellFactory(listView -> new ListViewRecordingCell());
    }

    @FXML
    private void showDialogSetKey(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        ContextMenu contextMenu = menuItem.getParentPopup();

        String keyPressedPrompt = "Please press a key: ";
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Set key..");
        alert.setContentText(keyPressedPrompt);

        Button buttonOK = getNewButtonForAlertBox(alert);

        // Whenever a Key is pressed, change Hotkey
        changeHotKey(contextMenu, keyPressedPrompt, alert, buttonOK);

        alert.showAndWait();
    }

    private void changeHotKey(ContextMenu contextMenu, String keyPressedPrompt, Alert alert, Button buttonOK) {
        buttonOK.setOnKeyPressed(keyEvent -> {
            alert.setContentText(keyPressedPrompt + keyEvent.getCode());
            for (int i = 0; i < soundButtonContextMenus.size(); i++) {
                if (contextMenu.getId().equals(soundButtonContextMenus.get(i).getId())) {
                    soundButtonKeyCodes.set(i, keyEvent.getCode());
                    soundButtonLabels.get(i).setText(keyEvent.getCode().toString());
                }
            }
        });
    }

    private Button getNewButtonForAlertBox(Alert alert) {
        ButtonType buttonType = new ButtonType("OK");
        alert.getButtonTypes().setAll(buttonType);
        return (Button) alert.getDialogPane().lookupButton(buttonType);
    }

    @FXML
    private void chooseFileForButtonPopUp(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        ContextMenu contextMenu = menuItem.getParentPopup();
        SoundButton button = new SoundButton();
        for (int i = 0; i < soundButtonContextMenus.size(); i++) {
            if (contextMenu.getId().equals(soundButtonContextMenus.get(i).getId())) {
                button = soundButtons.get(i);
            }
        }
        final FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile == null) {
            return;
        }

        if (FileUtil.isValidAudioFile(selectedFile)) {
            setFileOnButton(selectedFile, button);
        } else {
            alertIncompatibleFile();
        }
    }
}
