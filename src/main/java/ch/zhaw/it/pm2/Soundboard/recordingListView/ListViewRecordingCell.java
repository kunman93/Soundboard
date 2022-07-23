package ch.zhaw.it.pm2.Soundboard.recordingListView;

import ch.zhaw.it.pm2.Soundboard.model.RecordingData;
import javafx.scene.control.ListCell;

/**
 * An extended ListCell that holds a RecordingCell and offers manipulation methods.
 */
public class ListViewRecordingCell extends ListCell<RecordingData> {
    private RecordingCell recordingCell;

    /**
     * Updates content of RecordingCell or inserts it if it was empty before.
     *
     * @param recordingData The RecordingData to be set.
     * @param empty         determines if Recording cell is empty.
     */
    @Override
    public void updateItem(RecordingData recordingData, boolean empty) {
        super.updateItem(recordingData, empty);

        if (recordingData != null && !empty) {
            recordingCell = new RecordingCell();
            recordingCell.setData(recordingData);
            setGraphic(recordingCell.getBox());
        } else {
            setText(null);
            if (recordingCell != null) {
                recordingCell.clear();
            }
        }
    }
}
