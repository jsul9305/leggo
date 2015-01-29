package com.leggo.fingo;

import android.content.Intent;

import com.leggo.LocationFindService;
import com.leggo.MainActivity;

public class FinGoActionReceiver extends AbstractActionReceiver {


	@Override
	public void action1() {
        context.startService(new Intent(context, LocationFindService.class));
	}

	@Override
	public void action2() {
	}

	@Override
	public void action3() {
	}

	@Override
	protected String getClassName() {
		return MainActivity.class.getName();
	}
}
