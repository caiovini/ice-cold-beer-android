package com.icecoldbeer.game;

import com.badlogic.gdx.Game;
import com.icecoldbeer.game.screens.GameScreen;
import com.icecoldbeer.game.screens.TransitionScreen;

public class IceColdBeerGame extends Game {

	private boolean didCall = false;
	
	@Override
	public void create () {

	}

	@Override
	public void render () {
		super.render();

		if (!didCall) {
			super.render();
			this.setScreen(new TransitionScreen(this, new GameScreen(this)));
			didCall = true;
		}
	}
	
	@Override
	public void dispose () {

	}
}
