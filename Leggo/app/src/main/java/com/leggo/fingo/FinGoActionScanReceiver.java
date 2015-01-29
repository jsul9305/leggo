package com.leggo.fingo;

import com.leggo.MainActivity;
import com.leggo.R;

import java.util.HashMap;

import fingo.plugin.IExternalFingoAction;

public class FinGoActionScanReceiver extends AbstractActionScanReceiver
		implements IExternalFingoAction {

	@Override
	public String getClassName() {
		return MainActivity.class.getName();
	}

	@Override
	public String getDescription() {
		return context.getResources().getString(R.string.action_description);
	}

	@Override
	public String getPackageName() {
		return context.getPackageName();
	}

	@Override
	public String getIcon() {
		return "mirror";
	}

	@Override
	public String getSubject() {
		return context.getResources().getString(R.string.action_title);
	}

	@Override
	public Type getType() {
		return Type.LAUNCHER;
	}

	@Override
	public HashMap<State, String> getIcons() {
		HashMap<State, String> icons = new HashMap<State, String>();
		icons.put(State.DEFAULT, "mirror");
		icons.put(State.TOGGLE_FIRST, "mirror");
		icons.put(State.TOGGLE_SECOND, "mirror");
		icons.put(State.TOGGLE_THIRD, "mirror");
		return icons;
	}

}
