package jfcmb.sdk;

/* * Copyright (c) 2018 VR Fortaleza. All rights reserved. * */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responsible for executing operations of the <code>Fcmb</code> program.
 *
 * @author Derick Felix
 */
public class FcmbProcess {

    private String nameTemplate = "viewer";

    public void setNameTemplate(String nameTemplate) {
        this.nameTemplate = nameTemplate;
    }
    
    /**
     * Starts the <code>fcmb</code> program, it's responsible for communicating
     * with a fingerprint scanner and reading the image into a bitmap, and also
     * to extract its minutiae from the image and write it to a file.
     *
     * @throws jfcmb.sdk.FcmbException
     */
    public void start() throws FcmbException
    {
        String path = "c:";
        File executable = new File(path + "/fcmb");
        String[] command = {path + "/fcmb/fcmb.exe", path + "/fingerprints", this.nameTemplate};

        try {
            Process process = Runtime.getRuntime().exec(command, null, executable);

            if (printOutput(process.getInputStream())) {
                throw new FcmbException("Falha ao se comunicar com o leitor");
            }

        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to execute fcmb program", e);
        }
    }

    /**
     * Reads an input stream into a list of strings
     *
     * @param inputStream the input stream of the <code>fcmb</code> program
     * @throws IOException if a reading error occurs
     * @return whether the process has an error or not
     */
    private boolean printOutput(InputStream inputStream) throws IOException
    {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line = bufferedReader.readLine();
            System.out.println(line);
            while (line != null) {
                if (line.contains("Failed")) {
                    return true;
                }
                line = bufferedReader.readLine();
            }
        }
        return false;
    }
}
