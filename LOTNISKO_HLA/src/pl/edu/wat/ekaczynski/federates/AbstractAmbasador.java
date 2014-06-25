package pl.edu.wat.ekaczynski.federates;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.NullFederateAmbassador;
import org.portico.impl.hla13.types.DoubleTime;
import pl.edu.wat.ekaczynski.common.Constants;

/**
 *
 * @author ekaczynski
 */
public abstract class AbstractAmbasador extends NullFederateAmbassador {

	private boolean logEnable = false;
	
	protected double federateTime = 0.0;
	protected double federateLookahead = 1.0;
	protected boolean isRegulating = false;
	protected boolean isConstrained = false;
	protected boolean isAdvancing = false;
	protected boolean isAnnounced = false;
	protected boolean isReadyToRun = false;
	protected String ambasadorName = "AbstractAmbasador";

	public AbstractAmbasador(String ambasadorName) {
		this.ambasadorName = ambasadorName;
	}

	private double convertTime(LogicalTime logicalTime) {
		return ((DoubleTime) logicalTime).getTime();
	}

	protected void log(String message) {
		System.out.println("# " + federateTime + "\t" + ambasadorName + ": " + message);
	}

	@Override
	public void synchronizationPointRegistrationFailed(String label) {
		if (logEnable) {
			log("Failed to register sync point: " + label);
		}
	}

	@Override
	public void synchronizationPointRegistrationSucceeded(String label) {
		if (logEnable) {
			log("Successfully registered sync point: " + label);
		}
	}

	@Override
	public void announceSynchronizationPoint(String label, byte[] tag) {
		if (logEnable) {
			log("Synchronization point announced: " + label);
		}
		if (label.equals(Constants.RUN_SYNCHRONIZATION_POINT)) {
			this.isAnnounced = true;
		}
	}

	@Override
	public void federationSynchronized(String label) {
		if (logEnable) {
			log("Federation Synchronized: " + label);
		}
		if (label.equals(Constants.RUN_SYNCHRONIZATION_POINT)) {
			this.isReadyToRun = true;
		}
	}

	@Override
	public void timeRegulationEnabled(LogicalTime theFederateTime) {
		this.federateTime = convertTime(theFederateTime);
		this.isRegulating = true;
	}

	@Override
	public void timeConstrainedEnabled(LogicalTime theFederateTime) {
		this.federateTime = convertTime(theFederateTime);
		this.isConstrained = true;
	}

	@Override
	public void timeAdvanceGrant(LogicalTime theTime) {
		this.federateTime = convertTime(theTime);
		this.isAdvancing = false;
	}

	@Override
	public void discoverObjectInstance(int theObject,
		int theObjectClass,
		String objectName) {
		if (logEnable) {
			log("Discoverd Object: handle=" + theObject + ", classHandle="
				+ theObjectClass + ", name=" + objectName);
		}
	}

	@Override
	public void reflectAttributeValues(int theObject,
		ReflectedAttributes theAttributes,
		byte[] tag) {
		reflectAttributeValues(theObject, theAttributes, tag, null, null);
	}

	@Override
	public void reflectAttributeValues(int theObject,
		ReflectedAttributes theAttributes,
		byte[] tag,
		LogicalTime theTime,
		EventRetractionHandle retractionHandle) {
		StringBuilder builder = new StringBuilder("Reflection for object:");

		builder.append(" handle=").append(theObject);
		builder.append(", tag=").append(EncodingHelpers.decodeString(tag));
		double time = convertTime(theTime);
		if (theTime != null) {
			builder.append(", time=").append(time);
		}

		builder.append(", attributeCount=").append(theAttributes.size());
		builder.append("\n");
		for (int i = 0; i < theAttributes.size(); i++) {
			try {
				builder.append("\tattributeHandle=");
				builder.append(theAttributes.getAttributeHandle(i));
				builder.append(", attributeValue=");
				builder.append(
					EncodingHelpers.decodeString(theAttributes.getValue(i)));
				builder.append("\n");
			} catch (ArrayIndexOutOfBounds aioob) {
			}
		}

		if (logEnable) {
			log(builder.toString());
		}

		handleObjectParamsChanged(theObject, time, theAttributes);
	}

	@Override
	public void receiveInteraction(int interactionClass,
		ReceivedInteraction theInteraction,
		byte[] tag) {
		receiveInteraction(interactionClass, theInteraction, tag, null, null);
	}

	@Override
	public void receiveInteraction(int interactionClass,
		ReceivedInteraction theInteraction,
		byte[] tag,
		LogicalTime theTime,
		EventRetractionHandle eventRetractionHandle) {
		StringBuilder builder = new StringBuilder("Interaction Received:");

		String tagString = EncodingHelpers.decodeString(tag);
		builder.append(" handle=").append(interactionClass);
		builder.append(", tag=").append(tagString);
		double time = convertTime(theTime);
		if (theTime != null) {
			builder.append(", time=").append(time);
		}

		builder.append(", parameterCount=").append(theInteraction.size());
		builder.append("\n");
		for (int i = 0; i < theInteraction.size(); i++) {
			try {
				builder.append("\tparamHandle=");
				builder.append(theInteraction.getParameterHandle(i));
				builder.append(", paramValue=");
				builder.append(
					EncodingHelpers.decodeString(theInteraction.getValue(i)));
				builder.append("\n");
			} catch (ArrayIndexOutOfBounds aioob) {
			}
		}

		if (logEnable) {
			log(builder.toString());
		}

		handleInteraction(interactionClass, time, theInteraction);
	}

	@Override
	public void removeObjectInstance(int theObject, byte[] userSuppliedTag) {
		if (logEnable) {
			log("Object Removed: handle=" + theObject);
		}
	}

	@Override
	public void removeObjectInstance(int theObject,
		byte[] userSuppliedTag,
		LogicalTime theTime,
		EventRetractionHandle retractionHandle) {
		if (logEnable) {
			log("Object Removed: handle=" + theObject);
		}
	}

	public abstract void handleInteraction(int interactionClass, double time, ReceivedInteraction theInteraction);

	public abstract void handleObjectParamsChanged(int theObject, double time, ReflectedAttributes theAttributes);
}
