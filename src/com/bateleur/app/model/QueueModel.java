package com.bateleur.app.model;

import com.bateleur.app.datatype.BAudio;

import java.util.*;

public class QueueModel {
    private boolean shuffleEnabled;
    private boolean queueEnabled;
    private boolean repeatEnabled;
    
    private ArrayList<BAudio> queueSet;
    private ArrayList<BAudio> queueProcessed;
    private int queueProcessedIndex;
    
    public QueueModel() {
    	shuffleEnabled = false;
    	queueEnabled = true;
    	repeatEnabled = true;
    	
    	queueSet = new ArrayList<BAudio>();
    	queueProcessed = new ArrayList<BAudio>();
    	queueProcessedIndex = -1;
    }
    
    private void recreateProcessedQueue(BAudio startingAudio) {
    	queueProcessed.clear();
    	queueProcessed.addAll(queueSet);
    	
    	if (shuffleEnabled) {
    		Collections.shuffle(queueProcessed);
    	}
    	
    	queueProcessedIndex = queueProcessed.indexOf(startingAudio);
    }
    
    public BAudio get() {
        return queueProcessed.get(queueProcessedIndex);
    }
    
    private void wrapQueueProcessedIndex() {
		if (repeatEnabled) {
			queueProcessedIndex = ((queueProcessedIndex % queueProcessed.size()) + queueProcessed.size()) % queueProcessed.size();
		} else {
			queueProcessedIndex = Math.min(Math.max(queueProcessedIndex, 0), queueProcessed.size()-1);
		}
    }

    public void skipForwards() {
    	if (isQueueEnabled()) {
        	queueProcessedIndex++;
        	wrapQueueProcessedIndex();
    	}
    }

    public void skipBackwards() {
    	if (isQueueEnabled()) {
    		queueProcessedIndex--;
        	wrapQueueProcessedIndex();
    	}
    }

    public void setQueue(LibraryModel libraryModel, BAudio startingAudio) {
    	queueSet.clear();
    	libraryModel.forEach( (BAudio audio) -> queueSet.add(audio) );
    	
    	recreateProcessedQueue(startingAudio);
    }

    public boolean isShuffleEnabled() {
        return shuffleEnabled;
    }

    public void setShuffleState(boolean shuffleEnabled) {
    	if (this.shuffleEnabled != shuffleEnabled) {
        	this.shuffleEnabled = shuffleEnabled;
    		recreateProcessedQueue(get());
    	}
    }

    public boolean isQueueEnabled() {
        return queueEnabled;
    }

    public void setQueueState(boolean queueEnabled) {
    	this.queueEnabled = queueEnabled;
    }

    public boolean isRepeatEnabled() {
        return repeatEnabled;
    }

    public void setRepeatState(boolean repeatEnabled) {
    	this.repeatEnabled = repeatEnabled;
    }
}
