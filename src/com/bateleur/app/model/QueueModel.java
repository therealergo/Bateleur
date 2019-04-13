package com.bateleur.app.model;

import java.util.ArrayList;
import java.util.Collections;

import com.bateleur.app.datatype.BAudio;

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
		queueProcessedIndex = ((queueProcessedIndex % queueProcessed.size()) + queueProcessed.size()) % queueProcessed.size();
    }

    public boolean skipForwards() {
    	if (isQueueEnabled()) {
    		int queueProcessedIndex_initial = queueProcessedIndex;
        	queueProcessedIndex++;
        	wrapQueueProcessedIndex();
        	return isRepeatEnabled() || queueProcessedIndex > queueProcessedIndex_initial;
    	}
    	return isRepeatEnabled();
    }

    public boolean skipBackwards() {
    	if (isQueueEnabled()) {
    		int queueProcessedIndex_initial = queueProcessedIndex;
    		queueProcessedIndex--;
        	wrapQueueProcessedIndex();
    		return isRepeatEnabled() || queueProcessedIndex < queueProcessedIndex_initial;
    	}
    	return isRepeatEnabled();
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
    		settings.set(settings.QUEUE_SHUFFLE_EN.to(shuffleEnabled));
    		recreateProcessedQueue(get());
    	}
    }

    public boolean isQueueEnabled() {
        return settings.get(settings.QUEUE_QUEUE_EN);
    }

    public void setQueueState(boolean queueEnabled) {
		settings.set(settings.QUEUE_QUEUE_EN.to(queueEnabled));
    }

    public boolean isRepeatEnabled() {
        return settings.get(settings.QUEUE_REPEAT_EN);
    }

    public void setRepeatState(boolean repeatEnabled) {
		settings.set(settings.QUEUE_REPEAT_EN.to(repeatEnabled));
    }
}
