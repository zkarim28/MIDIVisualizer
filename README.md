# MIDIVisualizer
Project: MIDIVisualizer

Description: Processes MIDI input from the piano keyboard and dispalys notes in positions accordingly based on screen width. Notes are display in intensity of hue based on the veloiciy and fade away slowly after key is released.

Tools: Java Swing, javax.sound.midi module

Challenges: Concurrency issues, solved by replacing notes list type with thread safe data structures. The Midi interface and the Swing interface works on two separate threads, so it was import to make sure no errors occur when they read and write the note tracking data structures.

How to Install: Download the code (will update on soon)
How to Use: Plug in your MIDI Keyboard, run program, play!

Demo Video: https://youtu.be/tYoPVWI0KZE

Credits: 
- https://stackoverflow.com/questions/6937760/java-getting-input-from-midi-keyboard

