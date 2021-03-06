package com.bateleur.app.view.list;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.bateleur.app.datatype.BReference;
import com.bateleur.app.model.QueueModel;

public class BListOptionFolder_Queue extends BListOptionFolder {
	public BListOptionFolder_Queue(BListTab bListTab, BListOptionFolder parentFolder) {
		super(bListTab, parentFolder);
		
		bListTab.musicListController.master.queue.queueChangedEvent.addListener(() -> {
			bListTab.rebuildList(this);
		});
	}
	
	public String getText() {
		return "Queue";
	}
	
	public List<BListOption> listOptions() {
		QueueModel queue = bListTab.musicListController.master.queue;
		
		List<BListOption> options = new LinkedList<BListOption>();
		Iterator<BReference> queueIterator = queue.getQueueIterator();
		while (queueIterator.hasNext()) {
			options.add(new BListOptionAudio(bListTab, bListTab.musicListController.master.library.getByReference(queueIterator.next())));
		};
		return options;
	}
}
