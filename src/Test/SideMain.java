package Test;

import Visualizer.PianoProcessor;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public class SideMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Piano Visualizer");

            JMenuItem exitItem = new JMenuItem("Exit");
            JMenuItem openImgItem = new JMenuItem("Open Image File");
            JMenu optionsMenu = new JMenu("Options");
            JMenuBar menuBar = new JMenuBar();
            menuBar.add(optionsMenu);
            optionsMenu.add(openImgItem);
            optionsMenu.add(exitItem);
            frame.setJMenuBar(menuBar);

            TestPanel panel = new TestPanel();
            frame.getContentPane().add(panel);


            exitItem.addActionListener((ActionEvent ae) -> panel.stopPanel());
            exitItem.addActionListener((ActionEvent ae) -> frame.dispose());

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
