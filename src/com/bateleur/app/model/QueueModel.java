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

    public BAudio get() {

    }

    public QueueModel shuffle() {
        LinkedList<BAudio> shuffledQueueList = convertQueueToList(startingQueue);    // for creating new shuffle

    }

    private LinkedList<BAudio> convertQueueToList(Queue<BAudio> queue) {
        LinkedList<BAudio> list = new LinkedList<>(queue);
        Collections.shuffle(list);
        return list;
    }

    public void skipForwards() {

    }

    public void skipBackwards() {

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
