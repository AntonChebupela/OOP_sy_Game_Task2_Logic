package game.SY.model;

import java.util.Collection;


public interface ScotlandYardGame extends ScotlandYardView {


	void registerSpectator(Spectator spectator);


	void unregisterSpectator(Spectator spectator);


	void startRotate();


	Collection<Spectator> getSpectators();

}
