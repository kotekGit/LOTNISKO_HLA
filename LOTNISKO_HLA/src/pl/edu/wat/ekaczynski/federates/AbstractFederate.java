package pl.edu.wat.ekaczynski.federates;

import hla.rti.FederatesCurrentlyJoined;
import hla.rti.FederationExecutionDoesNotExist;
import hla.rti.LogicalTime;
import hla.rti.LogicalTimeInterval;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.ResignAction;
import hla.rti.jlc.RtiFactoryFactory;
import java.io.File;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;
import pl.edu.wat.ekaczynski.common.Constants;

/**
 *
 * @author ekaczynski
 */
public abstract class AbstractFederate<X extends AbstractAmbasador> {

	protected X ambasador;
	protected RTIambassador rti;
	protected String federateName = "AbstractFederate";

	public AbstractFederate(String federateName) {
		this.federateName = federateName;
		try {
			rti = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
		} catch (Exception e) {
		}
	}

	public void createFederation() {
		try {
			File fom = new File(Constants.FED_FILE_NAME);
			rti.createFederationExecution(Constants.FEDERATION_NAME, fom.toURI().toURL());
			log("Created Federation");
		} catch (Exception e) {
			log("Didn't create federation, it already existed");
		}
	}
	
	public void registerSynchronizationPoint(){
		
	}

	public void runFederate(X ambasador) throws RTIexception {
		this.ambasador = ambasador;

		rti.joinFederationExecution(federateName, Constants.FEDERATION_NAME, ambasador);
		log("Joined Federation as " + federateName);

		registerSynchronizationPoint();
		while (ambasador.isAnnounced == false) {
			rti.tick();
		}

		imReady();
		while(!allFederatesReady()){
			rti.tick();
		}
		
		rti.synchronizationPointAchieved(Constants.RUN_SYNCHRONIZATION_POINT);
		
		log("Achieved sync point: " + Constants.RUN_SYNCHRONIZATION_POINT + ", waiting for federation...");
		while (ambasador.isReadyToRun == false) {
			rti.tick();
		}

		enableTimePolicy();
		log("Time Policy Enabled");

		federateBody();

		rti.resignFederationExecution(ResignAction.NO_ACTION);
		log("Resigned from Federation");

		try {
			rti.destroyFederationExecution(Constants.FEDERATION_NAME);
			log("Destroyed Federation");
		} catch (FederationExecutionDoesNotExist dne) {
			log("No need to destroy federation, it doesn't exist");
		} catch (FederatesCurrentlyJoined fcj) {
			log("Didn't destroy federation, federates still joined");
		}
	}

	public abstract void federateBody() throws RTIexception;

	protected void advanceTime(double timestep) throws RTIexception {
		ambasador.isAdvancing = true;
		LogicalTime newTime = convertTime(ambasador.federateTime + timestep);
		rti.timeAdvanceRequest(newTime);

		while (ambasador.isAdvancing) {
			rti.tick();
		}
	}

	protected void deleteObject(int handle) throws RTIexception {
		rti.deleteObjectInstance(handle, generateTag());
	}

	protected double getLbts() {
		return ambasador.federateTime + ambasador.federateLookahead;
	}

	protected byte[] generateTag() {
		return ("" + System.currentTimeMillis()).getBytes();
	}

	protected void enableTimePolicy() throws RTIexception {
		LogicalTime currentTime = convertTime(ambasador.federateTime);
		LogicalTimeInterval lookahead = convertInterval(ambasador.federateLookahead);

		this.rti.enableTimeRegulation(currentTime, lookahead);

		while (ambasador.isRegulating == false) {
			rti.tick();
		}

		this.rti.enableTimeConstrained();

		while (ambasador.isConstrained == false) {
			rti.tick();
		}
	}

	protected void log(String message) {
		if (ambasador != null) {
			System.out.println("# " + ambasador.federateTime + "\t" + federateName + ": " + message);
		}
		else {
			System.out.println("# " + "\t" + federateName + ": " + message);
		}
	}

	protected LogicalTime convertTime(double time) {
		return new DoubleTime(time);
	}

	protected LogicalTimeInterval convertInterval(double time) {
		return new DoubleTimeInterval(time);
	}

	public abstract boolean allFederatesReady();

	public abstract void imReady();
}
