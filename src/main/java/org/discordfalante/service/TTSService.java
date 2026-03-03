package org.discordfalante.service;

import java.io.File;
import java.io.IOException;

public class TTSService {

    public File generateAudio(String text) throws IOException, InterruptedException {

        String outputFileName = "output.mp3";
        File outputFile = new File(outputFileName);
        String edgeTtsExe = "mockurl\\pythoncore-3.14-64\\Scripts\\edge-tts.exe";

        ProcessBuilder processBuilder = new ProcessBuilder(
                edgeTtsExe,
                "--voice", "pt-BR-AntonioNeural",
                "--text", text,
                "--write-media", outputFileName
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[edge-tts logs]: " + line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Erro ao gerar áudio");
        }

        return outputFile;
    }
}