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
import javax.swing.JFrame;
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

    //another idea, make a dictionary for all key note names
    //but the issue here is what scale are we in?
    //make some sort of menu that you can choose the scale and a color scheme

//    Map<Integer, String> notesDict= new HashMap<>()
//    {{
//        put(1, "A");
//        ...
//    }};

    private String name;
    private Map<Integer, Integer[]> activeNotes1; //[x, y, width, height, activeBit]
    private List<Integer[]> prevNotes;

    public PianoProcessor(String name) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(screenSize.width,(int)(screenSize.height - 0.25* screenSize.height)));
        setBackground(Color.GRAY);
        this.name = name;
        activeNotes1 = new HashMap<Integer, Integer[]>();
        prevNotes = new ArrayList<Integer[]>();
        startAnimation();
    }

    private void startAnimation() {
        new Thread(() -> {
            while (true) {
                moveNotes();
                repaint();
                try {
                    Thread.sleep(10); // Adjust the delay as needed
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void moveNotes() {

        for (Map.Entry<Integer, Integer[]> data : activeNotes1.entrySet()) {
            Integer key = data.getKey();
            Integer[] coord = data.getValue();
            int y = coord[0];
            y--;
            int rectHeight = coord[1];
            rectHeight++;
            activeNotes1.replace(key, new Integer[]{y, rectHeight});
        }

        int i = 0;
        while (i < prevNotes.size()) {
            Integer[] data = prevNotes.get(i);
            int y = data[1];
            int rectHeight = data[2];
            y--;
            if (y + rectHeight <= 0) { //y-coord becomes negative when above top border
                prevNotes.remove(i);
            } else {
                prevNotes.get(i)[1] = y;
            }
            i++;
        }

//        for (Map.Entry<Integer, Integer[]> data : prevNotes.entrySet()) {
//            Integer key = data.getKey();
//            Integer[] coord = data.getValue();
//            int y = coord[0];
//            int height = coord[1];
//            y--;
//            if (y + height <= 0){
//                prevNotes.remove(key);
//            } else {
//                prevNotes.get(key)[0] = y;
//            }
//        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        int rectWidth;

        for (Map.Entry<Integer, Integer[]> data : activeNotes1.entrySet()) {
            //is it a white or black key?
            int noteID = data.getKey();
            Integer[] coord = data.getValue();
            int y = coord[0];
            int rectHeight = coord[1];
            if (whiteNotesIds.contains(noteID)) {
                rectWidth = 20;
            } else {
                rectWidth = 15;
            }
            g.fillRoundRect(noteID * 19, y, rectWidth, rectHeight, 10, 10);
        }

        for (Integer[] data: prevNotes) {
            int noteID = data[0];
            int y = data[1];
            int rectHeight = data[2];
            if (whiteNotesIds.contains(noteID)) {
                rectWidth = 20;
            } else {
                rectWidth = 15;
            }
            g.fillRoundRect(noteID * 19, y, rectWidth, rectHeight, 10, 10);
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
            if (!translated.equals("FE")) {
                int type = Integer.parseInt(translated.substring(0, 2), 16);
                int noteID = Integer.parseInt(translated.substring(2, 4), 16) - 20;
                int vel = Integer.parseInt(translated.substring(4), 16);
                if (vel>0) {
                    activeNotes1.put(noteID, new Integer[]{getHeight(), 0});
                } else {
                    if (activeNotes1.containsKey(noteID)) { // if previously played
                            Integer[] noteInfo = activeNotes1.get(noteID);
                            int y = noteInfo[0];
                            int rectHeight = noteInfo[1];
                            Integer[] newNote = new Integer[]{noteID, y, rectHeight};
                            activeNotes1.remove(noteID); //remove from old list
                            prevNotes.add(newNote); //place into new list
                    }
                }
            }
            System.out.println(activeNotes1);
            System.out.println(prevNotes.toString());
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
