package de.rolf.games.cluedo;

import de.rolf.games.cluedo.Karte.Taeter;
import de.rolf.games.cluedo.Karte.Tatort;
import de.rolf.games.cluedo.Karte.Tatwaffe;
import de.rolf.games.cluedo.Scorecard.Status;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;
import org.springframework.stereotype.Component;

/**
 * @author brf
 *
 */
@Component
public class CluedoGUI
{

   private Scanner scanner;
   private Formatter formatter;

   private final Cluedo cluedo;

   public CluedoGUI(Cluedo cluedo)
   {
      this.cluedo = cluedo;
      scanner = new Scanner(System.in);
      formatter = new Formatter(System.out);
   }

   public void init()
   {
      // TODO: Test
      List<String> spielerNamen = new ArrayList<>();
      String source = "Maike" + System.lineSeparator() +
                      "Annso" + System.lineSeparator() +
                      "Emilie" + System.lineSeparator() +
                      "" + System.lineSeparator() +
                      "0" + System.lineSeparator() +
                      "4" + System.lineSeparator() +
                      "6" + System.lineSeparator() +
                      "15" + System.lineSeparator() +
                      "21" + System.lineSeparator() +
                      "" + System.lineSeparator() +
                      // DAPP
                      "2" + System.lineSeparator() +
                      "1" + System.lineSeparator() +
                      // Verdacht
                      "1" + System.lineSeparator() +
                      "0" + System.lineSeparator() +
                      "1" + System.lineSeparator() +
                      "0" + System.lineSeparator() +
                      "3" + System.lineSeparator() +
                      "n" + System.lineSeparator() +
                      "j"


          // "" + System.lineSeparator()
          ;
      scanner = new Scanner(source);
      formatter.format("%nAndere Spieler in Reihenfolge eingeben:%n");
      String n = scanner.nextLine();
      while ( (n != null) && !n.trim().isEmpty())
      {
         spielerNamen.add( n);
         n = scanner.nextLine();
      }
      cluedo.init( spielerNamen.toArray( new String[0]));

      leseKarten( cluedo.getIch());
      // TODO: test
      //scanner = new Scanner( System.in);
      printScorecard();

   }

   void run() {
      boolean exit = false;
      do {
         formatter.format("[0]: Ende - [1]: Verdacht - [2]: Dapp - [3]: Status%n");
         String input = readNextLine();
         if ("0".equalsIgnoreCase(input)) {
            if (getBestaetigung()) {
               exit = true;
               formatter.format("%nVerlasse Cluedo .... %n%n%n");
            }
         } else if ("1".equalsIgnoreCase(input)) {
            leseVerdacht();
            printScorecard();
            scanner = new Scanner(System.in);
         } else if ("2".equalsIgnoreCase(input)) {
            leseKarten(cluedo.getDapp());
            printScorecard();
         } else if ("3".equalsIgnoreCase(input)) {
            printStatus();
         } else {
            formatter.format("Falsche Eingabe !!%n%n");
         }
      } while (!exit);
   }

   private boolean getBestaetigung() {
      boolean b = false;
      formatter.format("%nEingabe OK (j/n)? ");
      String eingabe = readNextLine();
      if ("j".equalsIgnoreCase(eingabe)) {
         b = true;
      }
      return b;
   }

   private String readNextLine() {
      String eingabe = scanner.nextLine();
      while (eingabe.trim().isEmpty()) {
         eingabe = scanner.nextLine();
      }
      return eingabe;
   }

   private void leseVerdacht()
   {
      formatter.format("%n+++ Neuer Verdacht ++++%n");
      Verdacht v = null;
      do {
         formatter.format( "%s", "Wer hat Verdacht gestellt: ");
         printMitspieler();
         formatter.format( "%n");
         int idx = scanner.nextInt();
         Spieler von = cluedo.getMitspieler().get(idx);

         int i = 0;
         Karte[] kartenVerdacht = new Karte[3];
         for (Class<? extends Karte> cz : cluedo.getKartenTypen()) {
            printKartenTyp(cz);
            idx = scanner.nextInt();
            kartenVerdacht[i++] = cluedo.getAnzahlKarten(cz)[idx];
         }
         v = new Verdacht(von, (Taeter) kartenVerdacht[0], (Tatwaffe) kartenVerdacht[1], (Tatort) kartenVerdacht[2]);

         formatter.format( "%s", "Verdacht widerlegt (j/n)? ");
         String a = readNextLine();
         if ( "j".equalsIgnoreCase( a)) {
            formatter.format( "%s", "Spieler: ");
            printMitspieler();
            idx = scanner.nextInt();
            Spieler widerlegtVon = cluedo.getMitspieler().get(idx);
            v.setWiderlegtVon( widerlegtVon);
         }

         formatter.format( "%s", "Folgender Verdacht wurde erstellt: ");
         printVerdacht( v);

      } while ( !getBestaetigung());

      cluedo.addVerdacht( v);

      // falls ich den Verdacht hervorgebracht habe, dann bekomme ich eine Karte zu sehen
      // kann unabhängig von Verdacht gesetzt werden ...
      if ( v.isWiderlegt() && "ich".equalsIgnoreCase( v.getErstelltVon().getName())) {
         leseKarte( v.getWiderlegtVon(), v.getTaeter(), v.getWaffe(), v.getTatort());
      }
   }

   /**
    * @param v
    */
   private void printVerdacht( Verdacht v)
   {
      String widerLeger = (v.getWiderlegtVon() != null) ? v.getWiderlegtVon().getName() : "(niemand)";
      formatter.format( " %-7s [%-7s, %-12s, %-12s] --> %-7s", v.getErstelltVon().getName(), v.getTaeter(), v.getWaffe(), v.getTatort(), widerLeger);
   }

   /**
    * Lese Karten für den angegeben Spieler
    *
    * @param s Spieler
    */
   private void leseKarten(final Spieler s) {
      formatter.format("%nGib alle %s Karten von Spieler '%s' ein:%n", s.getAnzahlKarten(), s.getName());
      printKartenAuswahl();
      formatter.format("%n");
      for (int i = 0; i < s.getAnzahlKarten(); i++) {
         formatter.format("%s", "Nummer: ");
         int idx = scanner.nextInt();
         cluedo.setKarte(cluedo.getKarten().get(idx), s);
      }
   }

   private void leseKarte( final Spieler s, Karte...k )
   {
      formatter.format("%nGib gezeigte Karte ein:");
      for ( int i=0; i<k.length; i++)
      {
         formatter.format( "[%d]: %-17s", i, k[i]);
      }
      formatter.format( "%n");
      formatter.format( "%s", "Nummer: ");
      int idx = scanner.nextInt();
      cluedo.setKarte( k[idx], s);
   }

   private void printMitspieler() {
      List<Spieler> ms = cluedo.getMitspieler();
      for (int i = 0; i < ms.size(); i++) {
         formatter.format("[%d]: %-12s", i, ms.get(i).getName());
      }
   }

   private void printKartenTyp(Class<? extends Karte> kartenTyp) {
      Karte[] k = cluedo.getAnzahlKarten(kartenTyp);
      formatter.format("%n%-10s : ", kartenTyp.getSimpleName());
      for (int i = 0; i < k.length; i++) {
         formatter.format("[%2d]: %-17s", i, k[i]);
      }
      formatter.format("%n");
   }

   private void printKartenAuswahl() {
      List<Karte> k = cluedo.getKarten();
      String kartenTyp = null;
      for (int i = 0; i < k.size(); i++) {
         if (!k.get(i).getClass().getSimpleName().equals(kartenTyp)) {
            formatter.format("%n%-10s : ", k.get(i).getClass().getSimpleName());
            kartenTyp = k.get(i).getClass().getSimpleName();
         }
         formatter.format("[%2d]: %-17s", i, k.get(i));
      }
   }

   private void printScorecard() {
      formatter.format("%n%n%15s","");
      cluedo.getSpieler()
            .stream()
            .map(s -> String.format("%15s%-8s", s.getName(), "(" + s.getAnzahlKarten() + '/' + cluedo.getAnzahlKarten(s, Status.BESITZT) + '/' +
                                                             cluedo.getAnzahlKarten(s, Status.UNBEKANNT) + ')'))
            .forEach(formatter::format);
      formatter.format("%n");

      for (Karte karte: cluedo.getKarten()) {
         formatter.format("%15s", karte);
         for (Spieler spieler : cluedo.getSpieler()) {
            formatter.format("%15s%8s", getStatusSign(cluedo.getStatus(spieler, karte)), "");
         }
         formatter.format("%n%n");
      }
   }

   private char getStatusSign(Status status) {
      char c = ' ';
      if (status == Status.BESITZT) {
         c = 'x';
      } else if (status == Status.BESITZT_NICHT) {
         c = '-';
      }
      return c;
   }

   private void printStatus() {
      formatter.format("%s", " **** S T A T U S *****");
      formatter.format("%s", " **** Verdaechtigungen ");
      for (Verdacht v : cluedo.getVerdacht()) {
         formatter.format("%n");
         printVerdacht(v);
      }
      formatter.format("%n");
      printScorecard();
   }
}