package pl.edu.wat.ekaczynski.threading;

import hla.rti.RTIexception;
import pl.edu.wat.ekaczynski.federates.AbstractAmbasador;
import pl.edu.wat.ekaczynski.federates.AbstractFederate;

/**
 *
 * @author ekaczynsi
 */
public class FederateStarter<X extends AbstractFederate, Y extends AbstractAmbasador> extends Thread{

	private X federate;
	private Y ambasador;

	public FederateStarter(X federate, Y ambasador) {
		this.federate = federate;
		this.ambasador = ambasador;
	}
	
	@Override
	public void run() {
		try {
			federate.runFederate(ambasador);
		} catch (RTIexception ex) {}
	}

	public void createFederation() {
		federate.createFederation();
	}
	
	
}
