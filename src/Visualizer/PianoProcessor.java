package Visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class PianoProcessor extends JPanel implements Receiver{

    private Set<Integer> whiteNotesIds = new HashSet<>(Arrays.asList(1, 3, 4, 6, 8, 9, 11, 13, 15, 16, 18, 20, 21,
            23, 25, 27, 28, 30, 32, 33, 35, 37, 39, 40, 42, 44, 45, 47, 49,
            51, 52, 54, 56, 57, 59, 61, 63, 64, 66, 68, 69, 71, 73, 75, 76,
            78, 80, 81, 83, 85, 87, 88)); // better data struc for .contains
    private Map<Integer, Integer[]> activeNotes1; //better data struc for .contains
    private List<Integer[]> prevNotes; //better data struc for random access iterating through
    private Image backgroundImg;
    private String name;
    private Timer animationTimer;

    public PianoProcessor(String name) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(screenSize.width,(int)(screenSize.height - 0.25* screenSize.height)));
        this.name = name;
        activeNotes1 = new HashMap<Integer, Integer[]>();
        prevNotes = new ArrayList<Integer[]>();

        //background settings
//        setBackground(Color.ORANGE);
        try {
            backgroundImg = ImageIO.read(new File("/Users/zarifkarim/MIDIVisualizer/src/Visualizer/hamilton.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        startAnimation();
    }

    private void startAnimation() {
        animationTimer = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveNotes();
                repaint();
            }
        });

        animationTimer.start();
    }

    private void moveNotes() {

//        HashMap<Integer, Integer[]> updatedNotes = new HashMap<>();
////        HashMap<Integer, Integer[]> copy = new HashMap<>(activeNotes1);
//
//        HashMap<Integer, Integer[]> copy = new HashMap<Integer, Integer[]>();
//        for (Map.Entry<Integer, Integer[]> entry : activeNotes1.entrySet()) {
//            Integer[] src = entry.getValue();
//            Integer[] dest = new Integer[src.length];
//            System.arraycopy(src, 0, dest, 0, src.length);
//            copy.put(entry.getKey(), dest);
//        }
//
//        for (Map.Entry<Integer, Integer[]> data : copy.entrySet()) {
//            Integer key = data.getKey();
//            Integer[] coord = data.getValue();
//            int y = coord[0];
//            y--;
//            int rectWidth = coord[1];
//            int rectHeight = coord[2];
//            rectHeight++;
//            updatedNotes.put(key, new Integer[]{y, rectWidth, rectHeight});
//        }
//        activeNotes1.clear();
//        activeNotes1.putAll(updatedNotes);

        Thread moveNotesThread = new Thread(() -> {
            HashMap<Integer, Integer[]> updatedNotes = new HashMap<>();
            HashMap<Integer, Integer[]> copy = new HashMap<Integer, Integer[]>();
            for (Map.Entry<Integer, Integer[]> entry : activeNotes1.entrySet()) {
                Integer[] src = entry.getValue();
                Integer[] dest = new Integer[src.length];
                System.arraycopy(src, 0, dest, 0, src.length);
                copy.put(entry.getKey(), dest);
            }

            for (Map.Entry<Integer, Integer[]> data : copy.entrySet()) {
                Integer key = data.getKey();
                Integer[] coord = data.getValue();
                int y = coord[0];
                y-= 2; //adjust
                int rectWidth = coord[1];
                int rectHeight = coord[2];
                rectHeight+= 2; //adjust
                updatedNotes.put(key, new Integer[]{y, rectWidth, rectHeight});
            }
            activeNotes1.clear();
            activeNotes1.putAll(updatedNotes);

            List<Integer[]> notesToRemove = new ArrayList<>();
            int i = 0;
            while (i < prevNotes.size()) {
                Integer[] data = prevNotes.get(i);
                int y = data[1];
                int rectHeight = data[3];
                y-= 2; // adjust
                if (y + rectHeight <= 0) { //y-coord becomes negative when above top border
//                prevNotes.remove(i);
                    notesToRemove.add(data);
                } else {
                    data[1] = y;
                }
                i++;
            }
            prevNotes.removeAll(notesToRemove);

            // Update the UI after moving the notes
            SwingUtilities.invokeLater(() -> {
                repaint();
            });
        });

        moveNotesThread.start();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);

        HashMap<Integer, Integer[]> updatedNotes = new HashMap<>();
        HashMap<Integer, Integer[]> copy1 = new HashMap<Integer, Integer[]>();
        for (Map.Entry<Integer, Integer[]> entry : activeNotes1.entrySet()) {
            Integer[] src = entry.getValue();
            Integer[] dest = new Integer[src.length];
            System.arraycopy(src, 0, dest, 0, src.length);
            copy1.put(entry.getKey(), dest);
        }
        for (Map.Entry<Integer, Integer[]> note : copy1.entrySet()) {
            int noteID = note.getKey();
            Integer[] data = note.getValue();
            int y = data[0];
            int rectWidth = data[1];
            int rectHeight = data[2];
            g.setColor(Color.WHITE);
            g.fillRoundRect(noteID * 19 - 1, y - 1, rectWidth + 2, rectHeight + 2, 10, 10);
            g.setColor(new Color(0, 128, 128));
            g.fillRoundRect(noteID * 19, y, rectWidth, rectHeight, 10, 10);
//            g.fill3DRect(noteID * 19, y, rectWidth, rectHeight, true);
        }

        List<Integer[]> copy2 = new ArrayList<>(prevNotes);
        for (Integer[] data : copy2) { //need to create a copy of prevNotes list to avoid ConcurrentModificationException
            int noteID = data[0];
            int y = data[1];
            int rectWidth = data[2];
            int rectHeight = data[3];
            g.setColor(Color.WHITE);
            g.fillRoundRect(noteID * 19 - 1, y - 1, rectWidth + 2, rectHeight + 2, 10, 10);
            g.setColor(new Color(0, 128, 128));
            g.fillRoundRect(noteID * 19, y, rectWidth, rectHeight, 10, 10);
//            g.fill3DRect(noteID * 19, y, rectWidth, rectHeight, true);
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
                int rectWidth;

                if (vel>0) {
                    if (type == 144) { //if type note
                        if (whiteNotesIds.contains(noteID)) {
                            rectWidth = 20;
                        } else {
                            rectWidth = 15;
                        }
                        activeNotes1.put(noteID, new Integer[]{getHeight(), rectWidth, 0}); // (x: [y, w, h, vel])
                    } else if (type == 176 && noteID == 64 && vel == 127) { //else type pedal
                        //do pedal things
                    }
                } else {
                    if (type == 144) { //if type note
                        if (activeNotes1.containsKey(noteID)) { // if previously played
                            Integer[] noteInfo = activeNotes1.get(noteID);
                            int y = noteInfo[0];
                            rectWidth = noteInfo[1];
                            int rectHeight = noteInfo[2];
                            activeNotes1.remove(noteID); //remove from old list
                            prevNotes.add(new Integer[]{noteID, y, rectWidth,
                                    rectHeight}); //place into new list
                        }
                    } else if (type == 176 && noteID == 64) {
                        //do pedal things
                    }
                }
            }
            try {
                System.out.println(activeNotes1);
                System.out.println(prevNotes);
            } catch (ConcurrentModificationException e){}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void close() {}

    @Override
    public String getName() {
        return name;
    }
}