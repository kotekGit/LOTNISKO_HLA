package pl.edu.wat.ekaczynski.federates;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.RTIexception;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import java.util.ArrayList;
import java.util.List;
import pl.edu.wat.ekaczynski.common.Constants;
import pl.edu.wat.ekaczynski.common.Utils;

public class WiezaKontrolnaAmbasador extends AbstractAmbasador {

	private List<Integer> terminalZwykly;
	private List<Integer> terminalSpecjalny;
	private List<Integer> ladowanieBrakPaliwa;
	private Integer samolotDoWpuszczeniaNaPas = null;
	private boolean pasWolny = false;
	private int ostatniWpuszczony = 0;
	private int liczbaObsluzonychSamolotow = 0;

	public WiezaKontrolnaAmbasador(String ambasadorName) {
		super(ambasadorName);

		ladowanieBrakPaliwa = new ArrayList<Integer>();

		terminalZwykly = new ArrayList<Integer>();
		terminalSpecjalny = new ArrayList<Integer>();
	}

	@Override
	public void handleInteraction(int interactionClass, double time, ReceivedInteraction theInteraction) {
		try {
			samolotAkcjaHandle(
				Utils.getIntValue(theInteraction.getValue(0)),
				Constants.AkcjaSamolotuEnum.values()[Utils.getIntValue(theInteraction.getValue(1))],
				Utils.getIntValue(theInteraction.getValue(2)) == 1);
		} catch (ArrayIndexOutOfBounds ex) {
		}
	}

	private void samolotAkcjaHandle(int numerSamolotu, Constants.AkcjaSamolotuEnum akcja, boolean czySpecjalny) {
		if (akcja.equals(Constants.AkcjaSamolotuEnum.ZADANIE_START)) {
			log("Samolot numer " + numerSamolotu + " zgłosił: " + akcja + " " + (czySpecjalny ? "[VIP]" : ""));
			if (czySpecjalny) {
				terminalSpecjalny.add(numerSamolotu);
			} else {
				terminalZwykly.add(numerSamolotu);
			}
		} else if (akcja.equals(Constants.AkcjaSamolotuEnum.ZADANIE_LADOWANIE)) {
			log("Samolot numer " + numerSamolotu + " zgłosił: " + akcja + " " + (czySpecjalny ? "[VIP]" : ""));
			if (czySpecjalny) {
				terminalSpecjalny.add(numerSamolotu);
			} else {
				terminalZwykly.add(numerSamolotu);
			}
		} else if (akcja.equals(Constants.AkcjaSamolotuEnum.WYLADOWAL)
			|| akcja.equals(Constants.AkcjaSamolotuEnum.WYSTARTOWAL)) {
			log("Samolot numer " + numerSamolotu + " zgłosił: " + akcja + " " + (czySpecjalny ? "[VIP]" : ""));
			pasWolny = true;
			liczbaObsluzonychSamolotow++;
		} else if (akcja.equals(Constants.AkcjaSamolotuEnum.ZGLOSZENIE_MALO_PALIWA)) {
			if (ostatniWpuszczony != numerSamolotu && usunZKolejek(numerSamolotu)) {
				log("Samolot numer " + numerSamolotu + " zgłosił: " + akcja + " " + (czySpecjalny ? "[VIP]" : ""));
				ladowanieBrakPaliwa.add(numerSamolotu);
			}
		} else if (akcja.equals(Constants.AkcjaSamolotuEnum.ZGLOSZENIE_LADUJE_GDZIE_INDZIEJ)) {
			if (ostatniWpuszczony != numerSamolotu && usunZKolejek(numerSamolotu)) {
				log("Samolot numer " + numerSamolotu + " zgłosił: " + akcja + " " + (czySpecjalny ? "[VIP]" : ""));
				liczbaObsluzonychSamolotow++;
			}
		}

		if (pasWolny) {
			try {
				zezwolNaUzyciePasa();
			} catch (RTIexception ex) {
			}
		}
	}

	@Override
	public void handleObjectParamsChanged(int pasHandle, double time, ReflectedAttributes theAttributes) {
		try {
			int samolot = Utils.getIntValue(theAttributes.getValue(0));
			if (samolot > 0) {
				log("Samolot na pasie: " + samolot);
			}
			ostatniWpuszczony = samolot;

			if (samolot == 0) {
				pasWolny = true;
				zezwolNaUzyciePasa();
			}
		} catch (Exception ex) {
		}
	}

	private void zezwolNaUzyciePasa() throws RTIexception {
		//najpierw malo paliwa
		if (!ladowanieBrakPaliwa.isEmpty()) {
			int samolot = ladowanieBrakPaliwa.get(0);
			ladowanieBrakPaliwa.remove(0);
			zajmijPasDlaSamolotu(samolot);
			return;
		}

		//terminal specjalny
		if (!terminalSpecjalny.isEmpty()) {
			int samolot = terminalSpecjalny.get(0);
			terminalSpecjalny.remove(0);
			zajmijPasDlaSamolotu(samolot);
			return;
		}

		//terminal zwykly
		if (!terminalZwykly.isEmpty()) {
			int samolot = terminalZwykly.get(0);
			terminalZwykly.remove(0);
			zajmijPasDlaSamolotu(samolot);
		}
	}

	private void zajmijPasDlaSamolotu(int samolot) throws RTIexception {
		pasWolny = false;
		samolotDoWpuszczeniaNaPas = samolot;
	}

	public Integer getSamolotDoWpuszczeniaNaPas() {
		return samolotDoWpuszczeniaNaPas;
	}

	public void setSamolotDoWpuszczeniaNaPas(Integer samolotDoWpuszczeniaNaPas) {
		this.samolotDoWpuszczeniaNaPas = samolotDoWpuszczeniaNaPas;
	}

	private boolean usunZKolejek(int numerSamolotu) {
		int result = terminalZwykly.indexOf(numerSamolotu);
		if (result >= 0) {
			terminalZwykly.remove(result);
			return true;
		}
		result = terminalSpecjalny.indexOf(numerSamolotu);
		if (result >= 0) {
			terminalSpecjalny.remove(result);
			return true;
		}
		result = ladowanieBrakPaliwa.indexOf(numerSamolotu);
		if (result >= 0) {
			ladowanieBrakPaliwa.remove(result);
			return true;
		}
		return false;
	}

	public int getLiczbaObsluzonychSamolotow() {
		return liczbaObsluzonychSamolotow;
	}

	public void setLiczbaObsluzonychSamolotow(int liczbaObsluzonychSamolotow) {
		this.liczbaObsluzonychSamolotow = liczbaObsluzonychSamolotow;
	}
}