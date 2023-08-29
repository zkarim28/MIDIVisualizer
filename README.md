# MIDIVisualizer
Project: MIDIVisualizer

Description: Processes MIDI input from the piano keyboard and displays notes in positions accordingly based on screen width. Notes are displayed in the intensity of a hue based on the velocity and fade away slowly after the key is released.

Tools: Java Swing, javax.sound.midi module

Challenges: Concurrency issues, solved by replacing notes list type with thread-safe data structures. A ConcurrentHashMap was used to store which keys are held down, and a CopyOnWriteArrayList was used to store all of the released notes with their respective positionings. The Midi interface and the Swing interface work on two separate threads, so ensuring no errors occur when they read and write the note-tracking data structures was essential.

Demo Video: https://youtu.be/tYoPVWI0KZE

Credits: 
- https://stackoverflow.com/questions/6937760/java-getting-input-from-midi-keyboard

