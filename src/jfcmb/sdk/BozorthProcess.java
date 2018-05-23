package jfcmb.sdk;

/* * Copyright (c) 2018 VR Fortaleza. All rights reserved. * */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Responsible for executing operations of the <code>Bozorth3</code> program.
 *
 * @author Derick Felix
 * @author Samuelson
 */
public class BozorthProcess {

    private String template1, template2;

    public void templateCompare(String template1, String template2) {
        this.template1 = template1;
        this.template2 = template2;
    }

    /**
     * Starts the <code>Bozorth3</code> program, it's responsible for making
     * one-to-one and one-to-many fingerprint matching
     *
     * @return the number of the file with the highest score
     * @throws jfcmb.sdk.FcmbException
     */
    public String start() throws FcmbException {

        File executableFingers = new File("fcmb\\tmp");
        File executable = new File("fcmb\\exec");
        System.out.println(executableFingers.getAbsolutePath());
        String[] command = {executable.getAbsolutePath()+"\\bozorth3.exe", executableFingers.getAbsolutePath()+"\\" + this.template1 + ".xyt", executableFingers.getAbsolutePath()+"\\" + this.template2 + ".xyt"};

        try {

            Process process = Runtime.getRuntime().exec(command, null, executable);

            if (printOutput(process.getInputStream())) {
                throw new FcmbException("Falha ao se comunicar com o leitor");
            }

        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "Failed to execute Bozorth3 program", e);
        }
        return "0";
    }

    /**
     * Calculates the highest score of a list of strings that contains a score
     * and a file path as shown on the line below:
     * {@code [score] C:\vrponto\fingerprints\[number].xyt}
     *
     * @param output a list of strings that contains each line as above
     * @return the file [number] of the highest [score]
     * @throws VRPontoException
     */
    private String calculate(List<String> output) throws FcmbException {
        String path = "";
        int highScore = 0;

        for (String line : output) {
            String[] tokens = line.split(" ");
            int score = Integer.parseInt(tokens[0]);

            if (highScore < score) {
                highScore = score;
                path = tokens[1];
            }
        }

        // minimum score accepted
        if (highScore < 25) {
            throw new FcmbException("Digital não encontrado");
        }

        return path.substring(24, 26); // the file number
    }

    /**
     * Reads an input stream into a list of strings
     *
     * @param inputStream the input stream of the <code>Bozorth3</code> program
     * @return a list of string containing the content of input stream
     * @throws IOException if a reading error occurs
     */
    private List<String> getOutput(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        List<String> output = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(inputStreamReader)) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                output.add(line);
            }
        }
        return output;
    }

    private boolean printOutput(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String line = bufferedReader.readLine();
            System.out.println(line);
            Integer score = Integer.valueOf(line);

            if (score > 25) {
                System.out.println("Digitais são iguais");
                JOptionPane.showMessageDialog(null, "São iguais");
            } else {
                System.out.println("Digitais diferentes");
                JOptionPane.showMessageDialog(null, "São diferentes");
            }

            inputStreamReader.close();
            bufferedReader.close();
        }
        return false;
    }

    private String printOutputScore(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            return bufferedReader.readLine();

        }
    }
}
