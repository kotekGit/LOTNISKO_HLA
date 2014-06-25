package pl.edu.wat.ekaczynski.common;

/**
 *
 * @author ekaczynski
 */
public class Constants {
	
	public static final String FED_FILE_NAME = "lotnisko.fed";
	
	public static final String FEDERATION_NAME = "LOTNISKO";
	public static final String RUN_SYNCHRONIZATION_POINT = "RUN_SYNCHRONIZATION_POINT";
	
	public static final String OBIEKT_PAS_STARTOWY = "ObjectRoot.PasStartowy";
	public static final String INTERKACJA_AKCJA_SAMOLOTU = "InteractionRoot.AkcjaSamolotu";
	public static final String INTERKACJA_ZMIANA_PASA = "InteractionRoot.ZmianaPasa";

	public static enum AkcjaSamolotuEnum {
		ZADANIE_START,
		ZADANIE_LADOWANIE,
		WYSTARTOWAL,
		WYLADOWAL,
		ZGLOSZENIE_MALO_PALIWA,
		ZGLOSZENIE_LADUJE_GDZIE_INDZIEJ
	}
}