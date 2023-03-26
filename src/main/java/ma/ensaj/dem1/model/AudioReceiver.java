package ma.ensaj.dem1.model;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AudioReceiver {
    SourceDataLine speakers;
    ServerSocket serverSocket;
    Socket socket;

    boolean stop = false;



    public void start() {
        stop = false;
        try {

            // Initialize the audio playback
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(info);
            speakers.open(format);
            speakers.start();

            // Establish the socket connection
            int port = 8000;
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();

            // Receive the audio data
            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[4096];

            while (true) {
                System.out.println("receiver working");
                int count = in.read(buffer, 0, buffer.length);
                if (count > 0) {
                    speakers.write(buffer, 0, count);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        stop = !stop;
        speakers.close();
        speakers.stop();
        serverSocket = null;
    }
}