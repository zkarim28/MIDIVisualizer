package Visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.JPanel;

public class PianoProcessor extends JPanel implements Receiver{

//    private int[] whiteNotes = {21, 23, 24, 26, 28, 29, 31, 33, 35, 36, 38, 40, 41,
//            43, 45, 47, 48, 50, 52, 53, 55, 57, 59, 60, 62, 64, 65, 67, 69,
//            71, 72, 74, 76, 77, 79, 81, 83, 84, 86, 88, 89, 91, 93, 95, 96,
//            98, 100, 101, 103, 105, 107, 108};
    private Set<Integer> whiteNotesIds = new HashSet<>(Arrays.asList(1, 3, 4, 6, 8, 9, 11, 13, 15, 16, 18, 20, 21,
            23, 25, 27, 28, 30, 32, 33, 35, 37, 39, 40, 42, 44, 45, 47, 49,
            51, 52, 54, 56, 57, 59, 61, 63, 64, 66, 68, 69, 71, 73, 75, 76,
            78, 80, 81, 83, 85, 87, 88));

//    Map<Integer, String> notesDict= new HashMap<>()
//    {{
//        put(1, "A");
//        ...
//        //another idea, make a dictionary for all key note names
//        //but the issue here is what scale are we in?
//    }};

    private String name;
    private List<Integer[]> notes;

    public PianoProcessor(String name) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(screenSize.width,(int)(screenSize.height - 0.25* screenSize.height)));
        this.name = name;
        notes = new ArrayList<>();
        startAnimation();
    }

    private void startAnimation() {
        new Thread(() -> {
            while (true) {
                moveCircles();
                repaint();
                try {
                    Thread.sleep(10); // Adjust the delay as needed
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void moveCircles() {

        int i = 0;
        while (i < notes.size()) {
            int y = notes.get(i)[1];
            y -= 1; // Adjust the vertical movement speed as needed
            if (y <= 0) {
                notes.remove(i);
            } else {
                notes.get(i)[1] = y;
            }
            i++;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        int size;
        for (Integer[] coord : notes) {
            if (whiteNotesIds.contains(coord[0])) {
                size = 20;
            } else {
                size = 15;
            }
            g.fillOval(coord[0] * 19, coord[1], size, size);
        }
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        try {
            byte[] data = message.getMessage();
            String translated = new String();
            for (byte b : data) {
                translated += (String.format("%02X", b));
            }
//            System.out.println(translated);
            if (!translated.equals("FE")) {
                int type = Integer.parseInt(translated.substring(0, 2), 16);
                int noteID = Integer.parseInt(translated.substring(2, 4), 16) - 20;
                int vel = Integer.parseInt(translated.substring(4), 16);
                if (vel>0) {
                    notes.add(new Integer[]{noteID,getHeight()});
//	    			notesOn.add(new Integer[]{noteID, 0});
                } else {
//        			notesOff.add(new Integer[] {noteID, 0});
                }
            }
//            System.out.println(notes);
//            System.out.println(notesOn);
//            System.out.println(notesOff);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Override
    public void close() {}
}

//idea in mind
//create a list of notes that are being held currently
//once finished, send to a stillOnScreen list to make sure that keep streaming up the screen
