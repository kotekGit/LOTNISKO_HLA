package pl.edu.wat.ekaczynski.federates;

import hla.rti.RTIexception;
import pl.edu.wat.ekaczynski.common.Constants;

/**
 *
 * @author ekaczynski
 */
public class Statystyka extends AbstractFederate<AbstractAmbasador> {

	public static boolean READY = false;

	public Statystyka(String federateName) {
		super(federateName);
	}

	@Override
	public void federateBody() throws RTIexception {
		int intAkcjaSamolotu = rti.getInteractionClassHandle(Constants.INTERKACJA_AKCJA_SAMOLOTU);
		rti.subscribeInteractionClass(intAkcjaSamolotu);
		
		while (!WiezaKontrolna.DONE || !Samolot.DONE || !PasStartowy.DONE) {
			advanceTime(1.0);
		}
		
		((StatystykaAmbasador)ambasador).getStats();
	}

	@Override
	public boolean allFederatesReady() {
		return WiezaKontrolna.READY && Samolot.READY && PasStartowy.READY && Statystyka.READY;
	}

	@Override
	public void imReady() {
		READY = true;
	}
}