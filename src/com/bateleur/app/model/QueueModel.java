package com.bateleur.app.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BReference;
import com.therealergo.main.NilEvent;

public class QueueModel {
	private SettingsModel settings;
    
    public final NilEvent queueChangedEvent;
    
    public QueueModel(SettingsModel settings) {
    	this.settings = settings;
    	
    	this.queueChangedEvent = new NilEvent();
    }
    
    private void recreateProcessedQueue(BReference startingAudio) {
    	settings.get(settings.QUEUE_PROCES).clear();
    	settings.get(settings.QUEUE_PROCES).addAll(settings.get(settings.QUEUE_ACTUAL));
    	
    	if (isShuffleEnabled()) {
    		Collections.shuffle(settings.get(settings.QUEUE_PROCES));
    	}
    	
    	int computedIndex = settings.get(settings.QUEUE_PROCES).indexOf(startingAudio);
    	settings.set(settings.QUEUE_PROCES_INDEX.to(computedIndex));
    	
    	queueChangedEvent.accept();
    }
    
    public BReference get() {
        return settings.get(settings.QUEUE_PROCES).get( settings.get(settings.QUEUE_PROCES_INDEX) );
    }
    
    public Iterator<BReference> getQueueIterator() {
    	return settings.get(settings.QUEUE_ACTUAL).iterator();
    }
    
    private void wrapQueueProcessedIndex() {
    	int currentIndex = settings.get(settings.QUEUE_PROCES_INDEX);
    	int currentSize  = settings.get(settings.QUEUE_PROCES).size();
    	
    	currentIndex = ((currentIndex % currentSize) + currentSize) % currentSize;
    	
    	settings.set(settings.QUEUE_PROCES_INDEX.to(currentIndex));
    }

    public boolean skipForwards() {
    	if (isQueueEnabled()) {
    		int queueProcessedIndex_initial = settings.get(settings.QUEUE_PROCES_INDEX);
    		settings.set(settings.QUEUE_PROCES_INDEX.to( queueProcessedIndex_initial + 1 ));
        	wrapQueueProcessedIndex();
        	return isRepeatEnabled() || settings.get(settings.QUEUE_PROCES_INDEX) > queueProcessedIndex_initial;
    	}
    	return isRepeatEnabled();
    }

    public boolean skipBackwards() {
    	if (isQueueEnabled()) {
    		int queueProcessedIndex_initial = settings.get(settings.QUEUE_PROCES_INDEX);
    		settings.set(settings.QUEUE_PROCES_INDEX.to( queueProcessedIndex_initial - 1 ));
        	wrapQueueProcessedIndex();
    		return isRepeatEnabled() || settings.get(settings.QUEUE_PROCES_INDEX) < queueProcessedIndex_initial;
    	}
    	return isRepeatEnabled();
    }

    public void setQueue(LibraryModel library, BAudio startingAudio) {
    	setQueue( library, startingAudio.get(settings.AUDIO_REFERENCE) );
    }

    public void setQueue(LibraryModel library, BReference startingAudio) {
    	settings.get( settings.QUEUE_ACTUAL ).clear();
    	library.forEach( (BAudio audio) -> settings.get(settings.QUEUE_ACTUAL).add(audio.get(settings.AUDIO_REFERENCE)) );
    	
    	recreateProcessedQueue(startingAudio);
    }
    
    public void setQueue(List<BAudio> audioList, BAudio startingAudio) {
    	setQueue( audioList, startingAudio.get(settings.AUDIO_REFERENCE) );
    }
    
    public void setQueue(List<BAudio> audioList, BReference startingAudio) {
    	settings.get( settings.QUEUE_ACTUAL ).clear();
    	audioList.forEach( (BAudio audio) -> settings.get(settings.QUEUE_ACTUAL).add(audio.get(settings.AUDIO_REFERENCE)) );
    	
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
