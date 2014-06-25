package pl.edu.wat.ekaczynski.common;

/**
 *
 * @author ekaczynski
 */
public class Params {
	
	public static int LICZBA_SAMOLOTOW_DO_WYGENEROWANIA = 50;
	public static int LICZBA_NIEOBSLUZONYCH_SAMOLOTOW_MAX = 1000;
	public static int LICZBA_SAMOLOTOW_W_LANCUCHU_MIN = 1;
	public static int LICZBA_SAMOLOTOW_W_LANCUCHU_MAX = 15;
	public static float SZANSA_WYGENEROWANIA_SAMOLOTU_SPECJALNEGO = 0.2f;
	public static float SZANSA_WYGENEROWANIA_SAMOLOTU_STARTUJACEGO = 0.7f;
	public static int ODSTEP_POMIEDZY_SAMOLOTAMI_MIN = 10;
	public static int ODSTEP_POMIEDZY_SAMOLOTAMI_MAX = 70;
	public static int CZAS_LADOWANIA = 10;
	public static int CZAS_STARTU = 8;
	public static int CZAS_DO_ZGLOSZENIA_BRAKU_PALIWA_MIN = 10;
	public static int CZAS_DO_ZGLOSZENIA_BRAKU_PALIWA_MAX = 20 + Params.CZAS_DO_ZGLOSZENIA_BRAKU_PALIWA_MIN;
	public static int CZAS_DO_ZMIANY_LADOWISKA_MIN = 1 + Params.CZAS_DO_ZGLOSZENIA_BRAKU_PALIWA_MAX;
	public static int CZAS_DO_ZMIANY_LADOWISKA_MAX = 5 + Params.CZAS_DO_ZMIANY_LADOWISKA_MIN;
}