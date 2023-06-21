package Visualizer;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

public class MidiHandler
{
    private List<PianoProcessor> receivers = new ArrayList<>();
    public MidiHandler()
    {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(infos[i]);

                System.out.println(infos[i]);

                //get all transmitters
                List<Transmitter> transmitters = device.getTransmitters();
                //and for each transmitter

                for (int j = 0; j < transmitters.size(); j++) {
                    //create a new receiver
                    PianoProcessor receiver = new PianoProcessor(device.getDeviceInfo().toString());
                    transmitters.get(j).setReceiver(receiver);
                    receivers.add(receiver);
                }

                PianoProcessor receiver = new PianoProcessor(device.getDeviceInfo().toString());
                Transmitter trans = device.getTransmitter();
                trans.setReceiver(receiver);
                receivers.add(receiver);


                device.open();
                System.out.println(device.getDeviceInfo() + " Was Opened");

            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public List<PianoProcessor> getReceivers() {
        return receivers;
    }
}
