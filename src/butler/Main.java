// @author A0097836L
package butler;

import butler.common.LoggerPreset;
import butler.io.Storage;
import butler.io.StorageFacade;
import butler.logic.Logic;
import butler.logic.LogicFacade;
import butler.ui.UserInterface;

public class Main {

	public static void main(String[] args) {
		LoggerPreset.initializeGlobalLogger();
		Storage storage = initializeStorage();
		Logic logic = initializeLogic(storage);
		UserInterface window = initializeUserInterface(logic);
	}

	private static Storage initializeStorage() {
		Storage storage = new StorageFacade();
		return storage;
	}

	private static Logic initializeLogic(Storage storage) {
		Logic logic = new LogicFacade(storage);
		logic.initialize();
		return logic;
	}

	private static UserInterface initializeUserInterface(Logic logic) {
		UserInterface window = new UserInterface(logic);
		return window;
	}

}
