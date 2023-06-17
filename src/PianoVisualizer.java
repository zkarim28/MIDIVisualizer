import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PianoVisualizer extends JPanel implements Receiver {
    private List<Note> notes; // List to hold the MIDI note data

    public PianoVisualizer() {
        setPreferredSize(new Dimension(800, 400));

        // Initialize the notes list

        // Set up MIDI device and receiver
        try {
            MidiDevice device = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[0]);
            device.open();
            Transmitter transmitter = device.getTransmitter();
            transmitter.setReceiver(this);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Render the notes on the piano visualizer based on the MIDI data
        for (Note note : notes) {
            // Render each note on the visualizer
        }
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int note = shortMessage.getData1();

            if (command == ShortMessage.NOTE_ON) {
                // Add the played note to the MIDI data and refresh the GUI
                notes.add(new Note(note));
                repaint();
            } else if (command == ShortMessage.NOTE_OFF) {
                // Remove the released note from the MIDI data and refresh the GUI
                notes.removeIf(n -> n.getNote() == note);
                repaint();
            }
        }
    }

    @Override
    public void close() {
        // Cleanup resources if needed
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Piano Visualizer");
            PianoVisualizer visualizer = new PianoVisualizer();
            frame.getContentPane().add(visualizer);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }

    // Inner class representing a single note
    private static class Note {
        private int note;

        public Note(int note) {
            this.note = note;
        }

        public int getNote() {
            return note;
        }

        // Implement the note's appearance and behavior
    }
}