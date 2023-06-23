package Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TestPanel extends JPanel {

    private static final int RECT_WIDTH_WHITE = 9;
    private static final int RECT_WIDTH_BLACK = 6;
    private static final int NOTE_SPEED = 5;
    private static final int ANIMATION_DELAY = 12;
    private static final int PIXEL_MULT = 9;
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
    private String sustainStatus = "OFF";
    private Timer animationTimer;

    public TestPanel() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(screenSize.width/2,(int)(screenSize.height - 0.25* screenSize.height)/2));
        setDoubleBuffered(true);

        setBackground(Color.BLACK);
        try {
            backgroundImg = ImageIO.read(new File("/Users/zarifkarim/MIDIVisualizer/src/Visualizer/hamilton.jpeg"));
        } catch (IOException e) {}

        setLayout(new FlowLayout(FlowLayout.LEFT));
        sustainLabel = new JLabel("Sustain: " + sustainStatus);
        sustainLabel.setFont(sustainLabel.getFont().deriveFont(15.0f));
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
            prevNotes.get(i)[5] -= 3;

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
            int offset = rectWidth==6? 1 : 0;
            int rectHeight = note.getValue()[2];
            int vel = note.getValue()[3];

            g.setColor(new Color(235, 235, 235));
            g.fillRoundRect(noteID * PIXEL_MULT - 1 + offset + 20, y - 1 + offset, rectWidth + 2, rectHeight + 2, 10, 10);
            g.setColor(new Color(0, vel*2, vel*2));
            g.fillRoundRect(noteID * PIXEL_MULT + offset + 20, y + offset, rectWidth, rectHeight, 10, 10);
        }

        for (Integer[] data : prevNotes) {
            int noteID = data[0];
            int y = data[1];
            int rectWidth = data[2];
            int offset = rectWidth==6? 1 : 0;
            int rectHeight = data[3];
            int vel = data[4];
            int alpha = data[5] >= 0 ? data[5] : 0;

            g.setColor(new Color(235, 235, 235, alpha));
            g.fillRoundRect(noteID * PIXEL_MULT - 1 + offset + 20, y - 1 + offset, rectWidth + 2, rectHeight + 2, 10, 10);
            g.setColor(new Color(0, vel*2, vel*2, alpha));
            g.fillRoundRect(noteID * PIXEL_MULT + offset + 20, y + offset, rectWidth, rectHeight, 10, 10);
        }
    }

    public void setImage() {

    }

    public void stopPanel() {
        animationTimer.stop();
        repaint();
    }
}