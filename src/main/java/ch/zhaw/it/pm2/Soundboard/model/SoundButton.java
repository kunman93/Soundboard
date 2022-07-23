package ch.zhaw.it.pm2.Soundboard.model;

import javafx.scene.control.Button;

import java.io.File;

/**
 * A button with added data fields to save a File object and a Keycode for each button.
 */

public class SoundButton extends Button {

    private File file;

    public SoundButton() {
        super();
    }

    public File getFile() {
        return this.file;
    }

    public final void setFile(File file) {
        this.file = file;
    }
}