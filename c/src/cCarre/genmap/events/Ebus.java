package cCarre.genmap.events;

import com.google.common.eventbus.EventBus;

public class Ebus {
	private static EventBus Ebus = new EventBus();
	
	public static EventBus get() {
		return Ebus;
	}
}
