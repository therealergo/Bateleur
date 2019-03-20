package com.bateleur.app.model;

import com.bateleur.app.datatype.BAudio;

import java.io.IOException;
import java.util.*;

public class QueueModel {
	private SettingsModel settings;
	
    private ArrayList<BAudio> queueSet;
    private ArrayList<BAudio> queueProcessed;
    private int queueProcessedIndex;
    
    public QueueModel(SettingsModel settings) {
    	this.settings = settings;
    	
    	this.queueSet = new ArrayList<BAudio>();
    	this.queueProcessed = new ArrayList<BAudio>();
    	this.queueProcessedIndex = -1;
    }
    
    private void recreateProcessedQueue(BAudio startingAudio) {
    	queueProcessed.clear();
    	queueProcessed.addAll(queueSet);
    	
    	if (isShuffleEnabled()) {
    		Collections.shuffle(queueProcessed);
    	}
    	
    	queueProcessedIndex = queueProcessed.indexOf(startingAudio);
    }
    
    public BAudio get() {
        return queueProcessed.get(queueProcessedIndex);
    }
    
    private void wrapQueueProcessedIndex() {
		if (isRepeatEnabled()) {
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
        return settings.get(settings.QUEUE_SHUFFLE_EN);
    }

    public void setShuffleState(boolean shuffleEnabled) {
    	if (isShuffleEnabled() != shuffleEnabled) {
        	try {
    			settings.set(settings.QUEUE_SHUFFLE_EN.to(shuffleEnabled));
    		} catch (IOException e) {
    			//TODO: In future this won't be thrown here
    		}
    		recreateProcessedQueue(get());
    	}
    }

    public boolean isQueueEnabled() {
        return settings.get(settings.QUEUE_QUEUE_EN);
    }

    public void setQueueState(boolean queueEnabled) {
    	try {
			settings.set(settings.QUEUE_QUEUE_EN.to(queueEnabled));
		} catch (IOException e) {
			//TODO: In future this won't be thrown here
		}
    }

    public boolean isRepeatEnabled() {
        return settings.get(settings.QUEUE_REPEAT_EN);
    }

    public void setRepeatState(boolean repeatEnabled) {
    	try {
			settings.set(settings.QUEUE_REPEAT_EN.to(repeatEnabled));
		} catch (IOException e) {
			//TODO: In future this won't be thrown here
		}
    }
}
