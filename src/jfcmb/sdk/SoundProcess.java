package jfcmb.sdk;

/* * Copyright (c) 2018 VR Fortaleza. All rights reserved. * */


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Date: Feb 22, 2018
 * @author Derick Felix
 */
public class SoundProcess implements Runnable {

    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb

    private String filename;
////    private Position currPosition;

    public SoundProcess(String wavFile)
    {
        this.filename = wavFile;
    }

//    public SoundProcess(String wavFile, Position position)
//    {
//        this.filename = wavFile;
//        this.currPosition = position;
//    }

    @Override
    public void run()
    {
        File soundFile = new File(filename);
        if (!soundFile.exists()) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Wave file {0} not found", filename);
            return;
        }

        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile)) {

            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            try (SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info)) {
                sourceDataLine.open(format);

                if (sourceDataLine.isControlSupported(FloatControl.Type.PAN)) {
                    FloatControl pan = (FloatControl) sourceDataLine.getControl(FloatControl.Type.PAN);
//                    if (currPosition == Position.RIGHT) {
//                        pan.setValue(1.0f);
//                    } else if (currPosition == Position.LEFT) {
//                        pan.setValue(-1.0f);
//                    }
                }

                sourceDataLine.start();

                int nBytesRead = 0;
                byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];

                while (nBytesRead != -1) {
                    nBytesRead = audioInputStream.read(abData, 0, abData.length);
                    if (nBytesRead >= 0) {
                        sourceDataLine.write(abData, 0, nBytesRead);
                    }
                }

                sourceDataLine.drain();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to play sound " + filename, e);
        }
    }
}
