package pl.edu.wat.ekaczynski.federates;

import hla.rti.AttributeHandleSet;
import hla.rti.LogicalTime;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import pl.edu.wat.ekaczynski.common.Constants;
import pl.edu.wat.ekaczynski.common.Params;

public class WiezaKontrolna extends AbstractFederate {

	public static boolean READY = false;
	public static boolean DONE = false;

	public WiezaKontrolna(String federateName) {
		super(federateName);
	}

	@Override
	public void registerSynchronizationPoint() {
		super.registerSynchronizationPoint();
		try {
			rti.registerFederationSynchronizationPoint(Constants.RUN_SYNCHRONIZATION_POINT, null);
		} catch (Exception ex) {
		}
	}

	@Override
	public void federateBody() throws RTIexception {
		int intZmianaPasa = rti.getInteractionClassHandle(Constants.INTERKACJA_ZMIANA_PASA);
		rti.publishInteractionClass(intZmianaPasa);

		advanceTime(5.0);

		int intAkcjaSamolotu = rti.getInteractionClassHandle(Constants.INTERKACJA_AKCJA_SAMOLOTU);
		rti.subscribeInteractionClass(intAkcjaSamolotu);

		int pasStartowyHandle = rti.getObjectClassHandle(Constants.OBIEKT_PAS_STARTOWY);
		int samolotHandle = rti.getAttributeHandle("samolot", pasStartowyHandle);
		AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
		attributes.add(samolotHandle);
		rti.subscribeObjectClassAttributes(pasStartowyHandle, attributes);

		while (!WiezaKontrolna.DONE || !Samolot.DONE || !PasStartowy.DONE) {
			if (((WiezaKontrolnaAmbasador) ambasador).getLiczbaObsluzonychSamolotow() >= Params.LICZBA_SAMOLOTOW_DO_WYGENEROWANIA) {
				if (!DONE) {
					wpuscSamolotNaPas(-1);
				}
				DONE = true;
			}

			Integer samolotDoWpuszczenia = ((WiezaKontrolnaAmbasador) ambasador).getSamolotDoWpuszczeniaNaPas();
			if (samolotDoWpuszczenia != null) {
				wpuscSamolotNaPas(samolotDoWpuszczenia);
			}
			advanceTime(1.0);
		}
	}

	private void wpuscSamolotNaPas(int samolot) throws RTIexception {
		if (samolot > 0) {
			log("Zezwolenie na użycie pasa dla samolotu: " + samolot);
		}

		SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

		byte[] numerSamolotuValue = EncodingHelpers.encodeString("numerSamolotu:" + samolot);

		int classHandle = rti.getInteractionClassHandle(Constants.INTERKACJA_ZMIANA_PASA);
		int numerSamolotuHandle = rti.getParameterHandle("numerSamolotu", classHandle);

		parameters.add(numerSamolotuHandle, numerSamolotuValue);

		LogicalTime time = convertTime(ambasador.federateTime + ambasador.federateLookahead);
		rti.sendInteraction(classHandle, parameters, generateTag(), time);

		((WiezaKontrolnaAmbasador) ambasador).setSamolotDoWpuszczeniaNaPas(null);
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