package org.discordfalante.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

@Service
public class TTSService {

    @Value("${tts.command.path}")
    private String edgeTtsExe;

    private static final Logger log = LoggerFactory.getLogger(TTSService.class);


    public File generateAudio(String text) throws IOException, InterruptedException {

        String outputFileName = "output.mp3";
        File outputFile = new File(outputFileName);
        log.debug("Texto a ser processado pelo TTS: {}", text);

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
                log.debug("[edge-tts logs]: {}", line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            log.error("O executável do edge-tts falhou. Código de saída: {}", exitCode);
            throw new RuntimeException("Erro ao gerar áudio na engine TTS. Código: " + exitCode);
        }

        return outputFile;
    }
}