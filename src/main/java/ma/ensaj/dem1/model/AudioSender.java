package ma.ensaj.dem1.model;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.OutputStream;
import java.net.Socket;

public class AudioSender {
    private TargetDataLine microphone;
    private Socket socket;

    boolean stop = false;

    public void start() {
        stop = false;
        try {
            // Initialize the audio capture
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            // Establish the socket connection
            String host = "localhost";
            int port = 8000;
            socket = new Socket(host, port);

            // Send the audio data
            OutputStream out = socket.getOutputStream();
            byte[] buffer = new byte[4096];
            outerloop:
            while (!stop) {
                int count = microphone.read(buffer, 0, buffer.length);
                if (count > 0) {
                    out.write(buffer, 0, count);
                }
                continue outerloop;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        stop = !stop;
        microphone.close();
        microphone.stop();
        socket = null;

    }
}