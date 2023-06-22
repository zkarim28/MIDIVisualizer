package Visualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class PianoProcessor extends JPanel implements Receiver{

    private static final int RECT_WIDTH_WHITE = 20;
    private static final int RECT_WIDTH_BLACK = 15;
    private static final int NOTE_SPEED = 5;
    private static final int ANIMATION_DELAY = 15;
    private static final Color NOTE_BORDER_COLOR = Color.WHITE;
    private static final Color NOTE_COLOR = new Color(0, 128, 128);

    private static final Set<Integer> WHITE_NOTE_IDS = new HashSet<>(Arrays.asList(1, 3, 4, 6, 8, 9, 11, 13, 15, 16, 18, 20, 21,
            23, 25, 27, 28, 30, 32, 33, 35, 37, 39, 40, 42, 44, 45, 47, 49,
            51, 52, 54, 56, 57, 59, 61, 63, 64, 66, 68, 69, 71, 73, 75, 76,
            78, 80, 81, 83, 85, 87, 88)); // better data struc for .contains
    private Map<Integer, Integer[]> activeNotes1 = new ConcurrentHashMap<Integer, Integer[]>();; //better data struc insert and retreive
    private List<Integer[]> prevNotes = new CopyOnWriteArrayList<Integer[]>();; //better data struc for random access iterating through
    private Image backgroundImg;
    private JLabel sustainLabel;
    private String name;
    private Timer animationTimer;
    private String sustainStatus = "OFF";

    public PianoProcessor(String name) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(screenSize.width,(int)(screenSize.height - 0.25* screenSize.height)));
        setDoubleBuffered(true);

        this.name = name;
        try {
            backgroundImg = ImageIO.read(new File("/Users/zarifkarim/MIDIVisualizer/src/Visualizer/hamilton.jpeg"));
        } catch (IOException e) {}

        setLayout(new FlowLayout(FlowLayout.LEFT));
        sustainLabel = new JLabel("Sustain: " + sustainStatus);
        sustainLabel.setFont(sustainLabel.getFont().deriveFont(30.0f));
        sustainLabel.setForeground(Color.WHITE);

        add(sustainLabel);

        startAnimation();
    }

    private void startAnimation() {
        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveNotes();
                repaint();
            }
        });
        animationTimer.start();
    }

    private void moveNotes() {

        for (Map.Entry<Integer, Integer[]> data : activeNotes1.entrySet()) { // copy.entrySet()
            int key = data.getKey();
            int y = data.getValue()[0];
            int rectWidth = data.getValue()[1];
            int rectHeight = data.getValue()[2];
            int velocity = data.getValue()[3];
            int alpha = data.getValue()[4];

            y-= NOTE_SPEED; //adjust
            rectHeight+= NOTE_SPEED; //adjust
            activeNotes1.replace(key, new Integer[]{y, rectWidth, rectHeight, velocity, alpha});
        }

        int i = 0;
        while (i < prevNotes.size()) {
            int y = prevNotes.get(i)[1];
            int rectHeight = prevNotes.get(i)[3];
            prevNotes.get(i)[5] -= 2;

            y-= NOTE_SPEED; // adjust
            if (y + rectHeight <= 0) { //y-coord becomes negative when above top border
                prevNotes.remove(i);
            } else {
                prevNotes.get(i)[1] = y;
            }
            i++;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg!=null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
        for (Map.Entry<Integer, Integer[]> note : activeNotes1.entrySet()) { // copy1
            int noteID = note.getKey();
            int y = note.getValue()[0];
            int rectWidth = note.getValue()[1];
            int rectHeight = note.getValue()[2];
            int vel = note.getValue()[3];

            g.setColor(new Color(235, 235, 235));
            g.fillRoundRect(noteID * 19 - 1, y - 1, rectWidth + 2, rectHeight + 2, 10, 10);
            g.setColor(new Color(0, vel*2, vel*2));
            g.fillRoundRect(noteID * 19, y, rectWidth, rectHeight, 10, 10);
        }

        for (Integer[] data : prevNotes) {
            int noteID = data[0];
            int y = data[1];
            int rectWidth = data[2];
            int rectHeight = data[3];
            int vel = data[4];
            int alpha = data[5] >= 0? data[5] : 0;

            g.setColor(new Color(235, 235, 235, alpha));
            g.fillRoundRect(noteID * 19 - 1, y - 1, rectWidth + 2, rectHeight + 2, 10, 10);
            g.setColor(new Color(0, vel*2, vel*2, alpha));
            g.fillRoundRect(noteID * 19, y, rectWidth, rectHeight, 10, 10);
        }
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        try {
            String translated = new String();
            for (byte b : message.getMessage()) {
                translated += (String.format("%02X", b));
            }
            if (!translated.equals("FE")) {
                int type = Integer.parseInt(translated.substring(0, 2), 16);
                int noteID = Integer.parseInt(translated.substring(2, 4), 16) - 20;
                int vel = Integer.parseInt(translated.substring(4), 16);

//                System.out.println(type + " " + noteID + " " + vel);
                if (vel>0) {
                    if (type == 144) { //if type note
                        int rectWidth = WHITE_NOTE_IDS.contains(noteID) ? RECT_WIDTH_WHITE : RECT_WIDTH_BLACK;
                        activeNotes1.put(noteID, new Integer[]{getHeight(), rectWidth, 0, vel, 255}); // (x: [y, w, h, vel, alpha])
                    } else if (type == 176 && noteID == 44 && vel == 127) { //else type pedal
                        sustainStatus = "ON";
                        sustainLabel.setText("Sustain: " + sustainStatus);
                    }
                } else {
                    if (type == 144) { //if type note
                        if (activeNotes1.containsKey(noteID)) { // if previously played
                            int y = activeNotes1.get(noteID)[0];
                            int rectWidth = activeNotes1.get(noteID)[1];
                            int rectHeight = activeNotes1.get(noteID)[2];
                            int velocity = activeNotes1.get(noteID)[3];
                            int alpha = activeNotes1.get(noteID)[4];

                            activeNotes1.remove(noteID); //remove from old list
                            prevNotes.add(new Integer[]{noteID, y, rectWidth, rectHeight, velocity, alpha}); //place into new list

                        }
                    } else if (type == 176 && noteID == 44) {
                        sustainStatus = "OFF";
                        sustainLabel.setText("Sustain: " + sustainStatus);
                    }
                }
            }
        } catch (Exception e) {}
    }
    @Override
    public void close() {}

    @Override
    public String getName() {
        return name;
    }
}