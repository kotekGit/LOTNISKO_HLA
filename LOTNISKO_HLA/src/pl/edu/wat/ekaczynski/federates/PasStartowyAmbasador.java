package pl.edu.wat.ekaczynski.federates;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import pl.edu.wat.ekaczynski.common.Utils;

/**
 *
 * @author ekaczynski
 */
public class PasStartowyAmbasador extends AbstractAmbasador {

	private Integer samolot = 0;

	public PasStartowyAmbasador(String ambasadorName) {
		super(ambasadorName);
	}

	@Override
	public void handleInteraction(int interactionClass, double time, ReceivedInteraction theInteraction) {
		try {
			samolot = Utils.getIntValue(theInteraction.getValue(0));
			if (samolot > 0) {
				log("Rezerwacja dla: " + samolot);
			}
		} catch (ArrayIndexOutOfBounds ex) {
		}
	}

	@Override
	public void handleObjectParamsChanged(int theObject, double time, ReflectedAttributes theAttributes) {
	}

	public Integer getSamolot() {
		return samolot;
	}

	public void setSamolot(Integer samolot) {
		this.samolot = samolot;
	}
}