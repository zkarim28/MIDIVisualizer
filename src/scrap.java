public class scrap {

}
//        Iterator<Integer[]> iter = prevNotes.iterator();
//        int i = 0;
//        while (i < prevNotes.size()) {
//            Integer[] data = prevNotes.get(i);
//            int y = data[1];
//            int rectHeight = data[3];
//            y-= 2; // adjust
//            if (y + rectHeight <= 0) { //y-coord becomes negative when above top border
//                prevNotes.remove(i);
////                    notesToRemove.add(data);
//            } else {
//                data[1] = y;
//            }
//            i++;
//        }

//            prevNotes.removeAll(notesToRemove);

// Update the UI after moving the notes
//            SwingUtilities.invokeLater(() -> {
//                repaint();
//            });
//        });
//
//        moveNotesThread.start();

//            activeNotes1.clear();
//            activeNotes1.putAll(updatedNotes);

//            List<Integer[]> notesToRemove = new ArrayList<>();
//            Iterator<Integer[]> iter = prevNotes.iterator();

//                updatedNotes.put(key, new Integer[]{y, rectWidth, rectHeight});

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

//        Thread moveNotesThread = new Thread(() -> {
//            HashMap<Integer, Integer[]> updatedNotes = new HashMap<>();
//            HashMap<Integer, Integer[]> copy = new HashMap<Integer, Integer[]>();
//            for (Map.Entry<Integer, Integer[]> entry : activeNotes1.entrySet()) {
//                Integer[] src = entry.getValue();
//                Integer[] dest = new Integer[src.length];
//                System.arraycopy(src, 0, dest, 0, src.length);
//                copy.put(entry.getKey(), dest);
//            }

//        HashMap<Integer, Integer[]> updatedNotes = new HashMap<>();
//        HashMap<Integer, Integer[]> copy1 = new HashMap<Integer, Integer[]>();
//        for (Map.Entry<Integer, Integer[]> entry : activeNotes1.entrySet()) {
//            Integer[] src = entry.getValue();
//            Integer[] dest = new Integer[src.length];
//            System.arraycopy(src, 0, dest, 0, src.length);
//            copy1.put(entry.getKey(), dest);