package com.bateleur.app.model;

import com.bateleur.app.datatype.BAudio;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueueModel {

    private boolean shuffleEnabled;

    private boolean repeatEnabled;

    /**
     * Holds original queue when the model is created, used for shuffling
     */
    private Queue<BAudio> startingQueue;

    /**
     * Holds current active queue of songs
     */
    private Queue<BAudio> queue;

    /**
     * Holds previously played songs in the queue
     */
    private Queue<BAudio> previousQueue;

    /**
     * Returns a new QueueModel based on a new starting queue
     * @param startingQueue the newly created starting queue for the model
     */
    public QueueModel(Queue<BAudio> startingQueue) {
        this.startingQueue = startingQueue;
        queue = startingQueue;
        previousQueue = new LinkedList<>();
    }

    public BAudio get() {
        return queue.poll();
    }

    public QueueModel shuffle() {
        LinkedList<BAudio> shuffledQueue = convertQueueToList(startingQueue);    // for creating new shuffle
        return new QueueModel(shuffledQueue);
    }

    private LinkedList<BAudio> convertQueueToList(Queue<BAudio> queue) {
        LinkedList<BAudio> list = new LinkedList<>(queue);
        Collections.shuffle(list);
        return list;
    }

    public void skipForwards() {
        previousQueue.add(queue.poll());
    }

    public void skipBackwards() {
        queue.add(previousQueue.poll());
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
