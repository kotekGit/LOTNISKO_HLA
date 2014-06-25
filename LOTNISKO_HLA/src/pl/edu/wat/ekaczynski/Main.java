package pl.edu.wat.ekaczynski;

import pl.edu.wat.ekaczynski.federates.PasStartowy;
import pl.edu.wat.ekaczynski.federates.PasStartowyAmbasador;
import pl.edu.wat.ekaczynski.federates.Samolot;
import pl.edu.wat.ekaczynski.federates.SamolotAmbasador;
import pl.edu.wat.ekaczynski.federates.Statystyka;
import pl.edu.wat.ekaczynski.federates.StatystykaAmbasador;
import pl.edu.wat.ekaczynski.federates.WiezaKontrolna;
import pl.edu.wat.ekaczynski.federates.WiezaKontrolnaAmbasador;
import pl.edu.wat.ekaczynski.threading.FederateStarter;

/**
 * @author ekaczynski
 */
public class Main {

	public static void main(String[] args) {
		FederateStarter<Samolot, SamolotAmbasador> federateSamolot = new FederateStarter<Samolot, SamolotAmbasador>(new Samolot("SamolotFederate"), new SamolotAmbasador("SamolotAmbasador"));
		FederateStarter<WiezaKontrolna, WiezaKontrolnaAmbasador> federateWiezaKontrolna = new FederateStarter<WiezaKontrolna, WiezaKontrolnaAmbasador>(new WiezaKontrolna("WiezaKontrolnaFederate"), new WiezaKontrolnaAmbasador("WiezaKontrolnaAmbasador"));
		FederateStarter<PasStartowy, PasStartowyAmbasador> federatePasStartowy = new FederateStarter<PasStartowy, PasStartowyAmbasador>(new PasStartowy("PasStartowyFederate"), new PasStartowyAmbasador("PasStartowyAmbasador"));
		FederateStarter<Statystyka, StatystykaAmbasador> federateStatystyka = new FederateStarter<Statystyka, StatystykaAmbasador>(new Statystyka("StatystykaFederate"), new StatystykaAmbasador("StatystykaAmbasador"));

		federateWiezaKontrolna.createFederation();
		
		federateSamolot.start();
		federateWiezaKontrolna.start();
		federatePasStartowy.start();
		federateStatystyka.start();
	}
}
