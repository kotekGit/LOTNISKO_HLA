package pl.edu.wat.ekaczynski.federates;

import hla.rti.AttributeHandleSet;
import hla.rti.LogicalTime;
import hla.rti.RTIexception;
import hla.rti.SuppliedAttributes;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import pl.edu.wat.ekaczynski.common.Constants;

/**
 *
 * @author ekaczynski
 */
public class PasStartowy extends AbstractFederate<PasStartowyAmbasador> {

	public static boolean READY = false;
	public static boolean DONE = false;
	
	private Integer samolotPrev = -2;

	public PasStartowy(String federateName) {
		super(federateName);
	}

	@Override
	public void federateBody() throws RTIexception {
		//publish obiektu pasa
		int pasHandle = rti.getObjectClassHandle(Constants.OBIEKT_PAS_STARTOWY);
		int samolotHandle = rti.getAttributeHandle("samolot", pasHandle);
		AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
		attributes.add(samolotHandle);
		rti.publishObjectClass(pasHandle, attributes);
		int pasStartowyHandle = rti.registerObjectInstance(pasHandle);

		//rejestracja na interkacje z wieza
		int intZmianaPasa = rti.getInteractionClassHandle(Constants.INTERKACJA_ZMIANA_PASA);
		rti.subscribeInteractionClass(intZmianaPasa);

		advanceTime(6.0);

		while (!WiezaKontrolna.DONE || !Samolot.DONE || !PasStartowy.DONE) {
			if (DONE || (samolotPrev == -1)) {
				DONE = true;
			}
			Integer samolot = ((PasStartowyAmbasador) ambasador).getSamolot();

			if (samolot != samolotPrev) {
				zmienStatusPasa(pasStartowyHandle, samolot);
			}
			advanceTime(1.0);

			samolotPrev = samolot;
		}

		deleteObject(pasStartowyHandle);
	}

	private void zmienStatusPasa(int pasStartowyHandle, int samolot) throws RTIexception {
		SuppliedAttributes attributes = RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();

		byte[] samolotValue = EncodingHelpers.encodeString("samolot:" + samolot);

		int classHandle = rti.getObjectClass(pasStartowyHandle);
		int samolotHandle = rti.getAttributeHandle("samolot", classHandle);

		attributes.add(samolotHandle, samolotValue);

		LogicalTime time = convertTime(ambasador.federateTime + ambasador.federateLookahead);
		rti.updateAttributeValues(pasStartowyHandle, attributes, generateTag(), time);
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