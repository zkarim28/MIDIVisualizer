package Visualizer;

//import MidiHandler.*;
//import MidiHandler.MidiHandler;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainMidi {

    public static void main(String[] args) {
        MidiHandler midiDevices = new MidiHandler();
        List<PianoProcessor> receivers = midiDevices.getReceivers();

        receivers.remove(0);
        PianoProcessor kbd = receivers.get(0);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Piano Visualizer");
            PianoProcessor visualizer = kbd;
            frame.getContentPane().add(visualizer);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
