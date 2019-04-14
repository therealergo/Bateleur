package com.bateleur.app.view.list;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.QueueModel;

public class BListOptionFolderQueue extends BListOptionFolder {
	public BListOptionFolderQueue(BListTab bListTab, BListOptionFolder parentFolder) {
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
		Iterator<BAudio> queueIterator = queue.getQueueIterator();
		while (queueIterator.hasNext()) {
			options.add(new BListOptionFile(bListTab, queueIterator.next()));
		};
		return options;
	}
}
