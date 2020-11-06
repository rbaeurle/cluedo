package de.rolf.games.cluedo;

import de.rolf.games.cluedo.Scorecard.Status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @author brf
 *
 */
@Component
public class Cluedo
{
  private final List<Class<? extends Karte>> kartenTypen;

   private final List<Karte> karten;

   private static final int               ANZAHL_KARTEN_X    = 3;

   // alle Spieler, ink. 'Dapp' und Lösung ('X')
   private final List<Spieler> spielers;

   // Mitspieler
   private final List<Spieler> mitSpieler;

   private Scorecard                      scorecard          = null;

   private final List<Verdacht> verdachtListe;

   // Lösung ist X
   private Spieler x;

   private Spieler dapp;

   private Spieler ich;


   public Cluedo()
   {
      karten = new ArrayList<>();
      karten.addAll(Arrays.asList(Karte.Taeter.values()));
      karten.addAll(Arrays.asList(Karte.Tatwaffe.values()));
      karten.addAll(Arrays.asList(Karte.Tatort.values()));

      spielers = new ArrayList<>();
      mitSpieler = new ArrayList<>();

      verdachtListe = new ArrayList<>();
      kartenTypen = Arrays.asList(Karte.Taeter.class, Karte.Tatwaffe.class, Karte.Tatort.class);
   }
   
   
   /**
    * Initialisiert das Spiel mit den angegeben Mitspielern
    * @param name Namen der Mitspieler, eigener Spieler muss 'ich' heissen
    */
   public List<Spieler> init(String... name) {
      mitSpieler.clear();
      spielers.clear();
      int anzahlKartenProSpieler = (karten.size() - ANZAHL_KARTEN_X) / (name.length+1);
      int anzahlKartenDapp = karten.size() - ANZAHL_KARTEN_X - anzahlKartenProSpieler*(name.length+1);

      ich = new Spieler("ich",anzahlKartenProSpieler);
      spielers.add(ich);
      for (String spielerName : name) {
         spielers.add(new Spieler(spielerName, anzahlKartenProSpieler));
      }
      mitSpieler.addAll(spielers);
      dapp = new Spieler("Dapp", anzahlKartenDapp);
      spielers.add(dapp);
      x = new Spieler("X", ANZAHL_KARTEN_X);
      spielers.add(x);

      scorecard = new Scorecard(karten, spielers);

      return spielers;
   }


   /**
    * @return Alle Karten
    */
   public final List<Karte> getKarten() {
      return karten;
   }


   /**
    * Gibt alle Karten eines Typs zurück
    *
    * @param kartenTyp Enum-Klasse des Typs
    * @return Array an Karten
    */
   public final Karte[] getAnzahlKarten(Class<? extends Karte> kartenTyp) {
      return kartenTyp.getEnumConstants();
   }


   public final List<Class<? extends Karte>> getKartenTypen() {
      return kartenTypen;
   }

   /**
    * @return Alle Mitspieler
    */
   public final List<Spieler> getMitspieler() {
      return mitSpieler;
   }

  /**
   * @return alle Spieler (inkl. Dapp und X)
   */
   List<Spieler> getSpieler() {
     return spielers;
   }

   Spieler getIch() {
      return ich;
   }

   Spieler getDapp() {
     return dapp;
   }

   /**
    * Gibt den Spieler mit dem entsprechenden Namen zurück
    * @param name Name des Spielers
    * @return Spieler mit dem entsprechenden Namen zurück
    */
   public final Spieler getSpieler(final String name) {
      return spielers.stream()
                     .filter(s -> s.getName().equalsIgnoreCase(name))
                     .findFirst().orElse(null);
   }
   
   /**
    *  @return Status für Spieler-Karten-Kombination
    */
   public Scorecard.Status getStatus(Spieler s, Karte k) {
      return scorecard.getStatus(s, k);
   }

   /**
    * @return Die Anzahl der Karten die der Spieler im entsprechendem Zustand besitzt.
    */
   public final int getAnzahlKarten(Spieler s, Status status) {
      return scorecard.getKarten(s, status).length;
   }


   /**
    * Setzt die angegebene Karte für den entsprechenden Spieler und analysiert scorecard
    */
   public void setKarte(Karte k, Spieler s) {
      setKarte4Spieler(k, s);
      analysiere();
   }

   /**
    * Verarbeitung eines neuen Verdachts
    */
   public void addVerdacht(Verdacht v) {
      verdachtListe.add(v);
      // - alle, die Verdacht nicht widerlegt haben, haben keine der Karten
      Spieler ersteller = v.getErstelltVon();
      Spieler next = getNextMitspieler(ersteller);
      while (!next.equals(ersteller) && !next.equals(v.getWiderlegtVon())) {
         scorecard.setKarte(v.getTaeter(), next, Status.BESITZT_NICHT);
         scorecard.setKarte(v.getWaffe(), next, Status.BESITZT_NICHT);
         scorecard.setKarte(v.getTatort(), next, Status.BESITZT_NICHT);
         next = getNextMitspieler(next);
      }
      analysiere();
   }


  public List<Verdacht> getVerdacht() {
    return java.util.Collections.unmodifiableList(verdachtListe);
  }


  private boolean isDappBekannt() {
     return scorecard.getKarten(dapp,Status.BESITZT).length > 0;
  }


   /**
    * Setzt die angegebene Karte für den entsprechenden Spieler
    */
   private void setKarte4Spieler(Karte karte, Spieler spieler) {

     scorecard.setKarte(karte, spieler, Status.BESITZT);
     // X besitzt dann keine andere Karte gleichen Typs
     if (spieler.equals(x)) {
       Arrays.stream(karte.getClass().getEnumConstants())
             .filter(k -> !k.equals(karte))
             .forEach(k -> scorecard.setKarte(k, x, Status.BESITZT_NICHT));
     }
     // alle anderen Spieler haben Karte zwangsläufig nicht
     spielers.stream()
             .filter(s -> !s.equals(spieler))
             .forEach(s -> scorecard.setKarte(karte, s, Status.BESITZT_NICHT));

     //
     // wenn fÜr den Spieler alle Karten bekannt sind, besitzt er die restlichen Karten nicht mehr
     //
     if (scorecard.getKarten(spieler, Status.BESITZT).length == spieler.getAnzahlKarten()) {
       karten.stream()
             .filter(k -> getStatus(spieler, k) != Status.BESITZT)
             .forEach(k -> scorecard.setKarte(k, spieler, Status.BESITZT_NICHT));
     }
   }

  /**
   * Analysiert Scorecard und Verdacht-Liste so lange, bis sich keine Veränderungen mehr ergeben
   */
  private void analysiere() {

    // hat sich Status in scorecard geändert
    long before = scorecard.hashCode();

    // check ...
    // zuerst Spieler
    for (Spieler s : spielers) {

      Karte[] kartenUnbkt = scorecard.getKarten(s, Status.UNBEKANNT);
      if (kartenUnbkt.length == 0) {
        // alles bekannt für Spieler
        System.out.println("Kenne alles von " + s.getName());
        continue;
      }

      Karte[] karteBeknt = scorecard.getKarten(s, Status.BESITZT);
      // wenn Anzahl der unbekannten Karten gleich der Anzahl der 'fehlenden' besitzten Karten ist
      // dann müssen diese Karten die richtigen sein
      if (kartenUnbkt.length == (s.getAnzahlKarten() - karteBeknt.length)) {
        Arrays.stream(kartenUnbkt)
              .forEach(k -> setKarte4Spieler(k,s));
      }

      // Für 'X' gilt obige Aussage sogar für Karten gleichen Typs
      if (x.equals(s)) {
        // Wenn genau EINE Karte von einem Typ den Status unbekannt hat, dann MUSS diese Karte 'X'
        // gehören ....
        kartenTypen.stream()
              .map( cz -> scorecard.getKarten(s,cz,Status.UNBEKANNT))
              .filter( k -> k.length == 1)
              .forEach( k -> setKarte4Spieler(k[0],x));
      }
    }

    //
    // Überprüfe Karten
    //
    for (Karte k : karten) {
      if (scorecard.getBesitzer(k) != null) {
        // Alles über Karte bekannt
        continue;
      }
      // Falls Karte keinen Besitzer hat und alle außer einem Spieler die Karte Nicht besitzen,
      // dann hat dieser verbeleibende die Karte
      Spieler[] unbknt = scorecard.getSpieler(k, Status.UNBEKANNT);
      if (unbknt.length == 1) {
        setKarte4Spieler(k, unbknt[0]);
      }
    }

    //
    // Überprüfe Verdacht Liste
    //
    for (Verdacht v : verdachtListe) {
      if (v.isWiderlegt()) {
        final Spieler widerlegtVon = v.getWiderlegtVon();
        // wenn genau eine Karte keinem Spieler zugeordnet ist und der Spieler, der widerlegt hat
        // keine der anderen zwei Karten bsesitzt, dann muss der 'Widerleger' diese Karte besitzen
        Spieler s1 = scorecard.getBesitzer(v.getTaeter());
        Spieler s2 = scorecard.getBesitzer(v.getWaffe());
        Spieler s3 = scorecard.getBesitzer(v.getTatort());
        if ((s1 == null) && (s2 != null) && !s2.equals(widerlegtVon) && (s3 != null) && !s3.equals(widerlegtVon)) {
          setKarte4Spieler(v.getTaeter(), widerlegtVon);
        } else if ((s1 != null) && !s1.equals(widerlegtVon) && (s2 == null) && (s3 != null) && !s3.equals(widerlegtVon)) {
          setKarte4Spieler(v.getWaffe(), widerlegtVon);
        } else if ((s1 != null) && !s1.equals(widerlegtVon) && (s2 != null) && !s2.equals(widerlegtVon) && (s3 == null)) {
          setKarte4Spieler(v.getTatort(), widerlegtVon);
        }
      } else {
        // nicht widerlegt
        if (isDappBekannt()) {
          // Dapp ist bekannt
          // 1. falls 'ich'
          // oder
          // 2. jemand anderes dessen Karten bekannt sind (also auch ich)
          // dann müssen die nicht zugeordneten Karten des Verdachts die gesuchten sein ('X')
          if (v.getErstelltVon().equals(ich)
              || scorecard.getKarten(v.getErstelltVon(),Status.BESITZT).length == v.getErstelltVon().getAnzahlKarten()) {
            // 'Ich' habe Verdacht gestellt -> falls Dapp bekannt sind die nicht-zugeordneten Karten
            // die Gesuchten
            v.getKarten()
             .filter(k -> scorecard.getBesitzer(k) == null)
             .forEach( k -> setKarte4Spieler(k,x));
          }
        }
      }
    }

    if (before != scorecard.hashCode()) {
      analysiere();
    }
  }

   private Spieler getNextMitspieler(Spieler sp) {
      int index = mitSpieler.indexOf(sp);
      return mitSpieler.get((index+1)%mitSpieler.size());
   }
   
}
