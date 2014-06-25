package pl.edu.wat.ekaczynski.common;

import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RTIambassadorEx;
import hla.rti.jlc.RtiFactoryFactory;

/**
 *
 * @author ekaczynski
 */
public class Utils {

	private static RTIambassadorEx RTI;

	public static void LOG(String message) {
		System.out.println(message);
	}

	public static RTIambassadorEx getRTI() {
		if (RTI == null) {
			try {
				RTI = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
			} catch (Exception e) {
				LOG("Nie udało się utworzyć RTI");
			}
		}

		return RTI;
	}

	public static int rand(int min, int max) {
		int result = min;
		
		double r = Math.random() * 10000.0;
		if(max > min) {
			result += (r % (max - min));
		}
		
		return result;
	}
	
	public static int getIntValue(byte[] val){
		String param = EncodingHelpers.decodeString(val);
		String[] parts = param.split(":");
		return Integer.parseInt(parts[1]);
	}
}
