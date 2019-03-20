package com.bateleur.app.model;

import com.bateleur.app.datatype.BAudio;

import java.util.*;

public class QueueModel {

    private boolean shuffleEnabled;

    private boolean repeatEnabled;

    /**
     * Holds original forwardQueue when the model is created, used for shuffling
     */
    private Queue<BAudio> startingQueue;

    /**
     * Holds current active forwardQueue of songs
     */
    private Queue<BAudio> forwardQueue;

    /**
     * Holds previously played songs in the forwardQueue
     */
    private Queue<BAudio> previousQueue;

    /**
     * Returns a new QueueModel based on a new starting forwardQueue
     * @param startingQueue the newly created starting forwardQueue for the model
     */
    public QueueModel(Queue<BAudio> startingQueue, boolean shuffleEnabled) {
        this.startingQueue = startingQueue;
        forwardQueue = startingQueue;
        previousQueue = new ArrayDeque<>();
        this.shuffleEnabled = shuffleEnabled;
    }

    public BAudio get() {
        return forwardQueue.poll();
    }

    public QueueModel shuffle() {
        ArrayList<BAudio> shuffledQueueList = convertQueueToList(startingQueue);    // for creating new shuffle
        ArrayDeque<BAudio> shuffledQueue = new ArrayDeque<>(shuffledQueueList);
        return new QueueModel(shuffledQueue, shuffleEnabled);
    }

    public void unshuffle() {
        if (shuffleEnabled) {
            recreateQueueState(null);    // TODO: get current song to dequeue to this point
        }
    }

    /**
     * When unshuffling, this should load the original forwardQueue in the correct state. i.e., if the user shuffled on the
     * last song in their forwardQueue and unshuffled, the unshuffled forwardQueue should be at the last song.
     * @param audio current audio file to dequeue to
     */
    private void recreateQueueState(BAudio audio) {
        Queue<BAudio> queue = new LinkedList<>(startingQueue);    // create a new queue to poll from

        BAudio file;
        while ((file = queue.poll()) != null) {
            if (!file.equals(audio)) {
                previousQueue.add(file);
            }
            else {
                previousQueue.add(file);
                forwardQueue.addAll(queue);    // adds remaining queue elements to the forward queue
            }
        }
    }

    private ArrayList<BAudio> convertQueueToList(Queue<BAudio> queue) {
        ArrayList<BAudio> list = new ArrayList<>(queue);
        Collections.shuffle(list);
        return list;
    }

    public void skipForwards() {
        previousQueue.add(forwardQueue.poll());
    }

    public void skipBackwards() {
        forwardQueue.add(previousQueue.poll());
    }

    public void setQueue(LibraryModel libraryModel, int startingIndex) {

    }

    public void setShuffleState(boolean enabled) {

    }

    public void setQueueState(boolean enabled) {

    }

    public void setRepeat(boolean enabled) {

    }

    public boolean isShuffleEnabled() {
        return shuffleEnabled;
    }

    public boolean isRepeatEnabled() {
        return repeatEnabled;
    }
}
