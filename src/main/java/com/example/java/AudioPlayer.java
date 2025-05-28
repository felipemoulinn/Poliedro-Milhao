package com.example.java;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {

    private Clip clip;

    public void play(String pathInResources) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }

        try {
            URL url = getClass().getResource(pathInResources);

            if (url != null) {
                System.out.println(" URL do recurso encontrada: " + url.toExternalForm());
            } else {
                
                System.err.println(" Erro: Arquivo de áudio NÃO encontrado no classpath: " + pathInResources + ". Verifique o caminho e a localização do arquivo.");
                return; 
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            System.out.println("? Música iniciada: " + pathInResources);

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Erro: Formato de arquivo de áudio não suportado para " + pathInResources + ". " + e.getMessage());
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Erro: Linha de áudio não disponível para " + pathInResources + ". " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Erro de E/S ao carregar o arquivo de áudio " + pathInResources + ". " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Um erro inesperado ocorreu ao tentar tocar o áudio " + pathInResources + ". " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            System.out.println("️ Música parada.");
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            System.out.println("️ Música pausada.");
        }
    }

    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
            System.out.println("️ Música retomada.");
        }
    }
}