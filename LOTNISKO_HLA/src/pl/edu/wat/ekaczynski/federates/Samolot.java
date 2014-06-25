package pl.edu.wat.ekaczynski.federates;

import hla.rti.AttributeHandleSet;
import hla.rti.LogicalTime;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import pl.edu.wat.ekaczynski.common.Constants;
import pl.edu.wat.ekaczynski.common.Params;
import pl.edu.wat.ekaczynski.common.Utils;

public class Samolot extends AbstractFederate {

	private int LICZBA_WYGENEROWANYCH_SAMOLOTOW = 0;
	private int LICZBA_NIEOBSLUZONYCH_SAMOLOTOW = 0;
	private int NUMER_SAMOLOTU_INDEKS = 1;
	private double CZAS_WYGENEROWANIA_KOLEJNEGO_SAMOLOTU = 0;
	public static boolean READY = false;
	public static boolean DONE = false;

	public Samolot(String federateName) {
		super(federateName);
	}

	@Override
	public void federateBody() throws RTIexception {
		int interactionHandle = rti.getInteractionClassHandle(Constants.INTERKACJA_AKCJA_SAMOLOTU);
		rti.publishInteractionClass(interactionHandle);

		advanceTime(5.0);

		int pasStartowyHandle = rti.getObjectClassHandle(Constants.OBIEKT_PAS_STARTOWY);
		int samolotHandle = rti.getAttributeHandle("samolot", pasStartowyHandle);
		AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
		attributes.add(samolotHandle);
		rti.subscribeObjectClassAttributes(pasStartowyHandle, attributes);

		while (!WiezaKontrolna.DONE || !Samolot.DONE || !PasStartowy.DONE) {
			if (LICZBA_WYGENEROWANYCH_SAMOLOTOW >= Params.LICZBA_SAMOLOTOW_DO_WYGENEROWANIA) {
				DONE = true;
			}
			if (LICZBA_WYGENEROWANYCH_SAMOLOTOW < Params.LICZBA_SAMOLOTOW_DO_WYGENEROWANIA) {
				//czy osiągnięto limit nieobsłużonych
				if (LICZBA_NIEOBSLUZONYCH_SAMOLOTOW < Params.LICZBA_NIEOBSLUZONYCH_SAMOLOTOW_MAX) {
					//czy przyszedł czas na kolejny samolot?
					if (ambasador.federateTime >= CZAS_WYGENEROWANIA_KOLEJNEGO_SAMOLOTU) {
						int n = Utils.rand(Params.LICZBA_SAMOLOTOW_W_LANCUCHU_MIN, Params.LICZBA_SAMOLOTOW_W_LANCUCHU_MAX);
						for (int i = 0; i < n; i++) {
							if (LICZBA_WYGENEROWANYCH_SAMOLOTOW < Params.LICZBA_SAMOLOTOW_DO_WYGENEROWANIA) {
								wygenerujSamolot();
							}
						}

						CZAS_WYGENEROWANIA_KOLEJNEGO_SAMOLOTU = ambasador.federateTime + Utils.rand(Params.ODSTEP_POMIEDZY_SAMOLOTAMI_MIN, Params.ODSTEP_POMIEDZY_SAMOLOTAMI_MAX);
					}
				}
			}

			Integer samolot = ((SamolotAmbasador) ambasador).getSamolotDoObslugi();
			if (samolot != 0) {
				zmienStatusSamolotu(samolot);
				((SamolotAmbasador) ambasador).setSamolotDoObslugi(0);
			}

			advanceTime(1.0);
		}
	}

	private void wygenerujSamolot() throws RTIexception {

		double rand = Math.random();

		SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

		byte[] numerSamolotuValue = EncodingHelpers.encodeString("numerSamolotu:" + NUMER_SAMOLOTU_INDEKS);

		byte[] czySpecjalnyValue;
		if (rand <= Params.SZANSA_WYGENEROWANIA_SAMOLOTU_SPECJALNEGO) {
			((SamolotAmbasador) ambasador).getCzySpecjalnyMap().put(NUMER_SAMOLOTU_INDEKS, true);
			czySpecjalnyValue = EncodingHelpers.encodeString("czySpecjalny:" + 1);
		} else {
			((SamolotAmbasador) ambasador).getCzySpecjalnyMap().put(NUMER_SAMOLOTU_INDEKS, false);
			czySpecjalnyValue = EncodingHelpers.encodeString("czySpecjalny:" + 0);
		}

		rand = Math.random();
		byte[] numerAkcjiValue;

		//samolot startujacy
		if (rand <= Params.SZANSA_WYGENEROWANIA_SAMOLOTU_STARTUJACEGO) {
			((SamolotAmbasador) ambasador).getStatusMap().put(NUMER_SAMOLOTU_INDEKS, Constants.AkcjaSamolotuEnum.ZADANIE_START);
			numerAkcjiValue = EncodingHelpers.encodeString("numerAkcji:" + Constants.AkcjaSamolotuEnum.ZADANIE_START.ordinal());
		} //samolot ladujacy
		else {
			((SamolotAmbasador) ambasador).getStatusMap().put(NUMER_SAMOLOTU_INDEKS, Constants.AkcjaSamolotuEnum.ZADANIE_LADOWANIE);
			numerAkcjiValue = EncodingHelpers.encodeString("numerAkcji:" + Constants.AkcjaSamolotuEnum.ZADANIE_LADOWANIE.ordinal());
			dodajZadaniaPaliwa();
		}

		int classHandle = rti.getInteractionClassHandle(Constants.INTERKACJA_AKCJA_SAMOLOTU);
		int numerSamolotuHandle = rti.getParameterHandle("numerSamolotu", classHandle);
		int numerAkcjiHandle = rti.getParameterHandle("numerAkcji", classHandle);
		int czySpecjalnyHandle = rti.getParameterHandle("czySpecjalny", classHandle);

		parameters.add(numerSamolotuHandle, numerSamolotuValue);
		parameters.add(numerAkcjiHandle, numerAkcjiValue);
		parameters.add(czySpecjalnyHandle, czySpecjalnyValue);

		LogicalTime time = convertTime(ambasador.federateTime + ambasador.federateLookahead);
		rti.sendInteraction(classHandle, parameters, generateTag(), time);

		LICZBA_WYGENEROWANYCH_SAMOLOTOW++;
		LICZBA_NIEOBSLUZONYCH_SAMOLOTOW++;
		NUMER_SAMOLOTU_INDEKS++;
	}

	private void zmienStatusSamolotu(Integer samolot) throws RTIexception {
		if (!(samolot > 0)) {
			return;
		}

		Constants.AkcjaSamolotuEnum obecnyStatus = ((SamolotAmbasador) ambasador).getStatusMap().get(samolot);

		if (obecnyStatus.equals(Constants.AkcjaSamolotuEnum.ZADANIE_START)) {
			//startuje
			wyslijZadanie(samolot,
				Constants.AkcjaSamolotuEnum.WYSTARTOWAL.ordinal(),
				((SamolotAmbasador) ambasador).getCzySpecjalnyMap().get(samolot) ? 1 : 0,
				ambasador.federateTime + Params.CZAS_STARTU);
			LICZBA_NIEOBSLUZONYCH_SAMOLOTOW--;
		}

		if (obecnyStatus.equals(Constants.AkcjaSamolotuEnum.ZADANIE_LADOWANIE)
			|| obecnyStatus.equals(Constants.AkcjaSamolotuEnum.ZGLOSZENIE_MALO_PALIWA)) {
			//laduje
			wyslijZadanie(samolot,
				Constants.AkcjaSamolotuEnum.WYLADOWAL.ordinal(),
				((SamolotAmbasador) ambasador).getCzySpecjalnyMap().get(samolot) ? 1 : 0,
				ambasador.federateTime + Params.CZAS_LADOWANIA);

			LICZBA_NIEOBSLUZONYCH_SAMOLOTOW--;
		}
	}

	private void wyslijZadanie(int numerSamolotu, int numerAkcji, int czySpecjalny, double eventTime) throws RTIexception {

		SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

		byte[] numerSamolotuValue = EncodingHelpers.encodeString("numerSamolotu:" + numerSamolotu);
		byte[] numerAkcjiValue = EncodingHelpers.encodeString("numerAkcji:" + numerAkcji);
		byte[] czySpecjalnyValue = EncodingHelpers.encodeString("czySpecjalny:" + czySpecjalny);

		int classHandle = rti.getInteractionClassHandle(Constants.INTERKACJA_AKCJA_SAMOLOTU);
		int numerSamolotuHandle = rti.getParameterHandle("numerSamolotu", classHandle);
		int numerAkcjiHandle = rti.getParameterHandle("numerAkcji", classHandle);
		int czySpecjalnyHandle = rti.getParameterHandle("czySpecjalny", classHandle);

		parameters.add(numerSamolotuHandle, numerSamolotuValue);
		parameters.add(numerAkcjiHandle, numerAkcjiValue);
		parameters.add(czySpecjalnyHandle, czySpecjalnyValue);

		LogicalTime time = convertTime(eventTime);
		rti.sendInteraction(classHandle, parameters, generateTag(), time);
	}

	private void dodajZadaniaPaliwa() throws RTIexception {
		wyslijZadanie(NUMER_SAMOLOTU_INDEKS,
			Constants.AkcjaSamolotuEnum.ZGLOSZENIE_MALO_PALIWA.ordinal(),
			((SamolotAmbasador) ambasador).getCzySpecjalnyMap().get(NUMER_SAMOLOTU_INDEKS) ? 1 : 0,
			ambasador.federateTime + Utils.rand(Params.CZAS_DO_ZGLOSZENIA_BRAKU_PALIWA_MIN, Params.CZAS_DO_ZGLOSZENIA_BRAKU_PALIWA_MAX));

		wyslijZadanie(NUMER_SAMOLOTU_INDEKS,
			Constants.AkcjaSamolotuEnum.ZGLOSZENIE_LADUJE_GDZIE_INDZIEJ.ordinal(),
			((SamolotAmbasador) ambasador).getCzySpecjalnyMap().get(NUMER_SAMOLOTU_INDEKS) ? 1 : 0,
			ambasador.federateTime + Utils.rand(Params.CZAS_DO_ZMIANY_LADOWISKA_MIN, Params.CZAS_DO_ZMIANY_LADOWISKA_MAX));
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