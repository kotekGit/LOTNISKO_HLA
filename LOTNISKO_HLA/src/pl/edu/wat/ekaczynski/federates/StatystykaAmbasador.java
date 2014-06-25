package pl.edu.wat.ekaczynski.federates;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.edu.wat.ekaczynski.common.Params;
import pl.edu.wat.ekaczynski.common.Utils;

/**
 *
 * @author ekaczynski
 */
public class StatystykaAmbasador extends AbstractAmbasador {

	private Map<Integer, int[]> statsMap;
	int n_specjalnych = 0;
	int n_malo_paliwa = 0;
	int n_odlecialo = 0;
	int n_startujacych = 0;
	int n_ladujacych = 0;
	int n_wyladowalo = 0;
	int min_opoznienie_startu = 9999999;
	int max_opoznienie_startu = 0;
	int min_opoznienie_ladowania = 9999999;
	int max_opoznienie_ladowania = 0;
	float sr_opoznienie_startu = 0.0f;
	float sr_opoznienie_ladowania = 0.0f;

	public StatystykaAmbasador(String ambasadorName) {
		super(ambasadorName);
		statsMap = new HashMap<Integer, int[]>();
	}

	@Override
	public void handleInteraction(int interactionClass, double time, ReceivedInteraction theInteraction) {
		try {
			samolotAkcjaHandle(
				Utils.getIntValue(theInteraction.getValue(0)),
				Utils.getIntValue(theInteraction.getValue(1)),
				Utils.getIntValue(theInteraction.getValue(2)) == 1);
		} catch (ArrayIndexOutOfBounds ex) {
		} catch (NullPointerException e) {
		}
	}

	private void samolotAkcjaHandle(int numerSamolotu, int akcja, boolean czySpecjalny) {
		if (akcja == 0 || akcja == 1) {
			statsMap.put(numerSamolotu, new int[7]);
		}

		if (akcja == 4 || akcja == 5) {
			if (statsMap.get(numerSamolotu)[3] != 0) {
				return;
			}
		}

		statsMap.get(numerSamolotu)[akcja] = (int) federateTime;
		statsMap.get(numerSamolotu)[6] = czySpecjalny ? 1 : 0;

		if (akcja == 2 || akcja == 3) {
			addStat(numerSamolotu);
		}
	}

	@Override
	public void handleObjectParamsChanged(int theObject, double time, ReflectedAttributes theAttributes) {
	}

	public void getStats() {
		StringBuilder builder;

		log("--------------------------------------------------");
		log("Statystyka");

		n_ladujacych = Params.LICZBA_SAMOLOTOW_DO_WYGENEROWANIA - n_startujacych;
		sr_opoznienie_ladowania /= n_wyladowalo * 1.0;
		sr_opoznienie_startu /= n_startujacych * 1.0;

		log("--------------------------------------------------------------");
		log("Liczba samolotow startujacych: " + n_startujacych);
		log("Liczba samolotow ladujacych: " + n_ladujacych);
		log("Liczba samolotow specjalnych: " + n_specjalnych);
		log("Liczba samolotow, ktore zglosily malo paliwa: " + n_malo_paliwa);
		log("Liczba samolotow, ktore odlecialy na inne lotnisko: " + (n_ladujacych - n_wyladowalo));
		log("Liczba samolotow, ktore wyladowaly: " + n_wyladowalo);
		log("Min opoznienie startu: " + min_opoznienie_startu);
		log("Max opoznienie startu: " + max_opoznienie_startu);
		log("Srednie opoznienie startu: " + sr_opoznienie_startu);
		log("Min opoznienie ladowania: " + min_opoznienie_ladowania);
		log("Max opoznienie ladowania: " + max_opoznienie_ladowania);
		log("Srednie opoznienie ladowania: " + sr_opoznienie_ladowania);
		log("--------------------------------------------------------------");
	}

	private void addStat(int numerSamolotu) {

		int[] stats = statsMap.get(numerSamolotu);

		n_specjalnych += stats[6];
		if (stats[4] > 0) {
			n_malo_paliwa++;
		}
		if (stats[0] > 0) {
			n_startujacych++;
		}
		if (stats[1] > 0) {
			n_ladujacych++;
		}
		if (stats[3] > 0) {
			n_wyladowalo++;
		}

		int opoznienie_startu = stats[2] - stats[0] - Params.CZAS_STARTU + 2;
		if (opoznienie_startu > 0 && opoznienie_startu < min_opoznienie_startu) {
			min_opoznienie_startu = opoznienie_startu;
		}
		if (opoznienie_startu > max_opoznienie_startu) {
			max_opoznienie_startu = opoznienie_startu;
		}
		sr_opoznienie_startu += opoznienie_startu;
		if (stats[3] > 0) {
			int opoznienie_ladowania = stats[3] - stats[1] - Params.CZAS_LADOWANIA + 2;
			sr_opoznienie_ladowania += opoznienie_ladowania;
			if (opoznienie_ladowania > 0 && opoznienie_ladowania < min_opoznienie_ladowania) {
				min_opoznienie_ladowania = opoznienie_ladowania;
			}
			if (opoznienie_ladowania > max_opoznienie_ladowania) {
				max_opoznienie_ladowania = opoznienie_ladowania;
			}
		}

		statsMap.remove(numerSamolotu);
	}
}