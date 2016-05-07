package com.akjava.gwt.clothhair.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public abstract class ScheduleCommand {
boolean scheduled;


public void scheduleExecute(){
	if(!scheduled){
		scheduled=true;
		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			@Override
			public void execute() {
				scheduled=false;
				fireExecute();
			}
			
		});
	}
}


public abstract void fireExecute();
}
