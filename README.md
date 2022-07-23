# Soundboard

This project is a soundboard on which you can create your own sounds with a easy-to-use GUI.

## Instructions

### Welcome

Welcome to our project: Soundboard. We are 4 engineering students at the ZHAW. First you will get an instruction on how to set-up the project, how-to-use, testing and the project outlines and how far we've progressed.

### Set-Up (IMPORTANT)

For this project to run you will need to install the following program first:  [VirtualAudioCable](https://www.vb-audio.com/Cable/).
This will help our program record and save the recordings of the sounds you create. For further details on why we made this choice, click [here](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/19).

You can run this project in an IDE (gradle preinstalled) and start by entering "gradle run" in the terminal.

### How To Use

#### Usability

- When you start the project there will be a button on the top called "Load Default Sound Set", which fills the buttons and background sample with a default sound set. You can use these to start off and test things a little.
![load_default_sound_set](./Instruction_Resources/load_default_sound_set.png)
- If you choose to load your own files into the buttons you can use the drag and drop feature (**IMPORTANT: USE WAVE FILES ONLY**):
![drag_drop](./Instruction_Resources/drag_drop_buttons.gif)
  or you can choose a file by right clicking on a button and choosing "Choose File..":
  ![choose_file_button](./Instruction_Resources/choose_file_buttons.gif)
- You can load a Background Sample by either clicking on it and choosing from your file manager, or drag and drop Wave files as shown above (**IMPORTANT: USE WAVE FILES ONLY**):  
![file_manager](./Instruction_Resources/file_manager.gif)
- You can set HotKeys to each of the Buttons:
![set_key](./Instruction_Resources/set_key.gif)
- You can use a metronome which helps you get a better sense of the beat:  
![metronome](./Instruction_Resources/metronome_slider.gif)

#### Recording

1. Simply press "Start Recording" whenever you want to start recording, and press "Stop Recording" when you're done recording. Enter a name for your recording as shown:
![start_stop_recording](./Instruction_Resources/start_stop_recording.gif)
2. After pressing "Start Recording" press the assigned sound buttons or play your Background Sample to record your sound.
3. Play / Stop / Save your recordings anytime in the Reordings List below:  
![play_stop_save](./Instruction_Resources/play_stop_save_recording.png)

## Pull Requests
**Pull requests were always reviewed together in Discord or Microsoft Teams with screen sharing.**

## Testing
We tested the classes that we could, but there are a lot of functions which could not be tested. This is because we cant check if the audio really plays or the recording has the correct data in Bytes. (It is difficult to test audiodata)
### Mocking Test
The tests were not mocked because there was no need. We could test everything without mocking.

## Class Diagramm
The current class diagramm:
![here](./documentation/UML_Soundboard.png)

## Project outlines

Following below the outlines of the project will be layed out.

### Mock-Up

![Mock-Up]( ./Mock-Up/Mock-Up.png)

### Brief summary

The goal of the project is to develop a Soundboard-Application for the desktop PC, laptop or tablet, which allows customers to create their own music. The soundboard is a GUI (Graphical User Interface) with various Sound-Buttons, which offers a collection of sounds. The sounds can be played by pressing one of the nine Sound-Buttons. The end user has also the choice to program his Sound-Buttons with his own desired tones.

### Functional principle

- The user starts the Soundboard-Application and selects a preinstalled soundboard or he/she decides to create own sounds by dragging and dropping them onto one of the nine Sound-Buttons.

- ***Start-Recording-Button***: Starts the sound-recordingCell.
- ***Stop-Recording-Button***: Stops the sound-recordingCell.
- ***Sound-Buttons 1-9***: 
  - The user can listen to the preprogrammed sounds by pressing the Sound-Buttons 1-9.
  - It is possible for the user to drag and drop the individual sounds onto the Sound-Buttons.
  - During recordingCell, the user can press the individual Sound-Buttons so that the generated sounds are stored on the recordingCell.
- ***Background-Sample-Play-Button***: A background sample is started, which can be added to the recordingCell and played in the background next to the recorded sound sequence.
- ***Background-Sample-Pause-Button***: Pauses the background sample.
- ***Metronome-Slider***: The speed of the metronome can be adjusted with the slider.
- ***Metronome-Play-Button***: Starts the metronome.
- ***Metronome-Pause-Button***: Pauses the metronome.
- ***Recordings***: All recordings (in Mock-Up they are named First Try, Second Try and Third Try) are listed here. The user has four options for each of the recordingCell, that are available to him: 
  - The user can play the generated recordingCell by pressing the Play-Button.
  - The user can pause the recordingCell at any time by pressing the Pause-Button.
  - The recordingCell can be saved by pressing the Save-Button.
  - The recordingCell can be discarded by pressing the Discard-Button.

### Features

| Feature  | Links | Status |
| --------- | ----- | ------ |
| GUI that fits the functionality of a soundboard | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/2) and [Mock-Up](./Mock-Up/Mock-Up.png) | done
| Loading preinstalled Soundboard | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/3) | done
| Adding custom sounds | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/7) | done
| Background Sample | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/4) | done
| Start and Stop Recording | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/5) | done
| Saving Recordings | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/6) | done
| List of recordings | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/11) | done
| Metronome | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/1) | done

#### Nice-to-have

| Feature  | Links | Status |
| --------- | ----- | ------ |
| Log of played sounds | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/8) | not assigned yet
| Hotkeys for playback | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/9) | done
| Minimizing response times | [Feature-Link](https://github.zhaw.ch/pm2-it19awin-muon-rayi-scmy/gruppe03-bettermouret-projekt2-Soundboard/issues/10) | done

### Meetings

- Meetings as well as all important design decisions are documented in Issues tagged as Meetings and Decisions.
