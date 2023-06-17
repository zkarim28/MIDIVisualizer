import java.util.concurrent.BlockingQueue;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public class MidiInputReceiver implements Receiver {
    private String name;
    private BlockingQueue<MidiMessage> messageQueue;
//    private String storedMessage = "";

    public MidiInputReceiver(String name) {
        this.name = name;
    }

    public void send(MidiMessage message, long timeStamp) {
        try {
            messageQueue.put(message); // Enqueue the MIDI message
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        byte[] data = message.getMessage();
//        String translated = new String();
//        for (byte b : data) {
//            translated += (String.format("%02X", b));
//        }
//        storedMessage = translated;
//        System.out.println(storedMessage);
        //90 is a note type
        //B0 = 176 and B1 = 177 is a pedal type, on is 7F

        //6C is Highest ID = 108
        //15 is Lowest ID = 21
        //40 is sustain pedal ID = 64

        //7F is Highest velocity = 127
        //00 is note Off velocity = 0
    }

//    public String getMessage() {
//        return storedMessage;
//    }

    public void close() {}

    public void setMessageQueue(BlockingQueue<MidiMessage> messageQueue) {
        this.messageQueue = messageQueue;
    }
}