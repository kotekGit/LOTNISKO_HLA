package pl.edu.wat.ekaczynski.federates;

import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import java.util.HashMap;
import java.util.Map;
import pl.edu.wat.ekaczynski.common.Constants;
import pl.edu.wat.ekaczynski.common.Constants.AkcjaSamolotuEnum;
import pl.edu.wat.ekaczynski.common.Utils;

public class SamolotAmbasador extends AbstractAmbasador {

	private Map<Integer, Constants.AkcjaSamolotuEnum> statusMap;
	private Map<Integer, Boolean> czySpecjalnyMap;
	private Integer samolotDoObslugi = 0;

	public SamolotAmbasador(String ambasadorName) {
		super(ambasadorName);

		statusMap = new HashMap<Integer, Constants.AkcjaSamolotuEnum>();
		czySpecjalnyMap = new HashMap<Integer, Boolean>();
	}

	@Override
	public void handleInteraction(int interactionClass, double time, ReceivedInteraction interaction) {
	}

	@Override
	public void handleObjectParamsChanged(int theObject, double time, ReflectedAttributes theAttributes) {
		try {
			int samolot = Utils.getIntValue(theAttributes.getValue(0));
			samolotDoObslugi = samolot;
			if (samolot > 0) {
				log("Samolot zajmuje pas: " + samolot);
			}
		} catch (Exception ex) {
		}
	}

	public Map<Integer, AkcjaSamolotuEnum> getStatusMap() {
		return statusMap;
	}

	public Map<Integer, Boolean> getCzySpecjalnyMap() {
		return czySpecjalnyMap;
	}

	public Integer getSamolotDoObslugi() {
		return samolotDoObslugi;
	}

	void setSamolotDoObslugi(int samolotDoObslugi) {
		this.samolotDoObslugi = samolotDoObslugi;
	}
}