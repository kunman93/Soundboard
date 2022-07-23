package ch.zhaw.it.pm2.Soundboard.utils;

import ch.zhaw.it.pm2.Soundboard.utils.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileUtilTest {
    private static String path = "src/test/resources/";
    private static String tempPath= "src/test/resources/temp/";



    @BeforeEach
     void setup(){
    cleanFolder(tempPath);

    }


    @Test
    public void testIsValidAudioFileValid(){
        File file = new File(path+"default1.wav");
       assertEquals(true,FileUtil.isValidAudioFile(file));

    }

    @Test
    public void testIsValidAudioFileNonExistent(){
        File file = new File(path+"NotHere.wav");
        assertEquals(false,FileUtil.isValidAudioFile(file));

    }

    @Test
    public void testIsValidAudioFileWrongExtention(){
        File file = new File(path+"salamisound-1953981-bach-plaetschern.mp3");
        assertEquals(false,FileUtil.isValidAudioFile(file));

    }

    @Test
    public void testIsValidAudioFileNotAFile(){
        File file = new File(path);
        assertEquals(false,FileUtil.isValidAudioFile(file));

    }

    @Test
    public void testIsValidAudioFileNull(){

        assertEquals(false,FileUtil.isValidAudioFile(null));

    }

    @Test
    public void testSaveAudioFile() throws IOException, UnsupportedAudioFileException {
        File directory = new File(tempPath);

        if(!directory.exists()){
            directory.mkdir();
        }

        String newpath =tempPath+"outfile.wav";
        File file = new File(path+"default1.wav");
        AudioInputStream inStream = AudioSystem.getAudioInputStream(file);

        FileUtil.saveAudioFile(inStream,newpath);

        File newfile = new File(newpath);
        assertEquals(true,newfile.exists()&&newfile.isFile());

    }

    @Test
    public void testSaveAudioFileNullStream() throws IOException, UnsupportedAudioFileException {
        String newpath =tempPath+"outfilenull.wav";

        assertThrows(NullPointerException.class, () -> {
            FileUtil.saveAudioFile(null, newpath);
        });

    }


    private static void cleanFolder(String path){
        File tempDir = new File(path);
        if(tempDir.exists()){
            for(File tempFile : Objects.requireNonNull(tempDir.listFiles())){
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }}}
    }





}
