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
        System.out.println(receivers.toString());

        PianoProcessor kbd = receivers.get(1);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Piano Visualizer");
            PianoProcessor visualizer = kbd;
            frame.getContentPane().add(visualizer);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });



//    	BlockingQueue<MidiMessage> messageQueue = new LinkedBlockingQueue<>();




//    	PianoProcessor kbd = new PianoProcessor();



//    	Start a separate thread to continuously retrieve and process MIDI messages
//    	Thread messageProcessingThread = new Thread(() -> {
//    	  while (true) {
//    	      try {
//    	          MidiMessage midiMessage = messageQueue.take(); // Blocks until a message is available
//    	          // Process the MIDI message
//    	          byte[] data = midiMessage.getMessage();
//    	          String translated = new String();
//    	          for (byte b : data) {
//    	              translated += (String.format("%02X", b));
//    	          }
//    	          System.out.println("Received MIDI message: " + translated);
//    	      } catch (InterruptedException e) {
//    	          e.printStackTrace();
//    	      }
//    	  }
//    	});
//    	messageProcessingThread.start();
//    	// Assign the messageQueue to each MidiInputReceiver
//    	for (MidiInputReceiver receiver : receivers) {
//    	  receiver.setMessageQueue(messageQueue);
//    }
    }
}
