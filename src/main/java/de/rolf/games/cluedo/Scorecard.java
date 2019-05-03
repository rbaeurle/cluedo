package de.rolf.games.cluedo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.EqualsAndHashCode;

/**
 * @author brf
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class Scorecard {

  // Scorecard
  @EqualsAndHashCode.Include
  private final Status[][] scorecard;

  // Referenz auf Spieler und Karten
  private final Spieler[] spieler;
  private final Karte[] karten;

  Scorecard(final List<Karte> k, final List<Spieler> sp) {
    karten = k.toArray(new Karte[0]);
    spieler = sp.toArray(new Spieler[0]);
    scorecard = new Status[karten.length][spieler.length];
    for (int i = 0; i < scorecard.length; i++) {
      for (int j = 0; j < scorecard[i].length; j++) {
        scorecard[i][j] = Status.UNBEKANNT;
      }
    }
  }

  private int getIndex(Spieler s) {
    for (int i = 0; i < spieler.length; i++) {
      if (s.equals(spieler[i])) {
        return i;
      }
    }
    return -1;
  }

  private int getIndex(Karte k) {
    for (int i = 0; i < karten.length; i++) {
      if (k.equals(karten[i])) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Setzt den entsprechenden Status für Spieler-Karten-Kombination
   *
   * @return true, falls  Status-Setzen ohne Konflikt durchgeführt werden konnte
   */
  final boolean setKarte(Karte k, Spieler s, Status status) {

    int indexKarte = getIndex(k);
    int indexSpieler = getIndex(s);
    Status aktuellerStatus = scorecard[indexKarte][indexSpieler];

    if (status == aktuellerStatus) {
      return true;
    }

    // check auf Inkonsistens
    // TODO: wenn jemand anderes die Karte schon besitzt
    boolean statusChangedOk = true;
    if (((status == Status.BESITZT_NICHT) && (aktuellerStatus == Status.BESITZT)) ||
        ((status == Status.BESITZT) && (aktuellerStatus == Status.BESITZT_NICHT))) {
      System.err.println("Status für " + s.getName() + "/" + k + " wurde von " + aktuellerStatus + " nach " + status + " geändert !!!");
      statusChangedOk = false;
    }
    System.out.println(s.getName() + " " + status + " " + k);
    scorecard[indexKarte][indexSpieler] = status;
    return statusChangedOk;
  }

  /**
   * @return Status für Spieler-Karten-Kombination
   */
  final Status getStatus(Spieler s, Karte k) {
    return scorecard[getIndex(k)][getIndex(s)];
  }

  /**
   * @return Gibt den Besitzer der entsprechenden Karten zurück, oder null, wenn Besitzer noch unbekannt ist
   */
  final Spieler getBesitzer(final Karte k) {

    int indexKarte = getIndex(k);
    for (int i = 0; i < spieler.length; i++) {
      if (scorecard[indexKarte][i] == Status.BESITZT) {
        return spieler[i];
      }
    }
    return null;
  }

  /**
   * @return Alle Spieler, die die angegebene Karte im entsprechendem Zustand besitzen
   */
  final Spieler[] getSpieler(final Karte k, Status status) {

    List<Spieler> s = new ArrayList<>();
    int index = getIndex(k);
    for (int i = 0; i < scorecard[index].length; i++) {
      if (scorecard[index][i] == status) {
        s.add(spieler[i]);
      }
    }
    return s.toArray(new Spieler[0]);
  }

  /**
   * @return Alle Karten, die der Spieler im entsprechendem Zustand besitzt
   */
  final Karte[] getKarten(Spieler s, Status status) {
    List<Karte> k = new ArrayList<>();
    int indexSpieler = getIndex(s);
    for (int i = 0; i < scorecard.length; i++) {
      if (scorecard[i][indexSpieler] == status) {
        k.add(karten[i]);
      }
    }
    return k.toArray(new Karte[0]);
  }

  /**
   * @return Alle Karten vom Kartentyp, die der Spieler im entsprechendem Zustand besitzt
   */
  final Karte[] getKarten(Spieler s, Class<? extends Karte> kartenTyp, Status status) {
    return Arrays.stream(getKarten(s, status))
                 .filter(kartenTyp::isInstance)
                 .toArray(Karte[]::new);
  }


  /**
   * Status, die ein Feld annehmen kann
   */
  enum Status {
    UNBEKANNT, // bisher unbekannt
    BESITZT_NICHT, // Spieler hat Karte nicht
    BESITZT; // Spieler hat Karte
  }
}
