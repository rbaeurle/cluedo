package de.rolf.games.cluedo

import de.rolf.games.cluedo.Karte.Taeter
import de.rolf.games.cluedo.Karte.Tatort
import de.rolf.games.cluedo.Karte.Tatwaffe

class Cluedo(name: List<String>) {

  val kartenTypen: List<Class<out Karte?>> = listOf(Taeter::class.java, Tatwaffe::class.java, Tatort::class.java)
  private val karten: List<Karte> = arrayOf(*Taeter.values(), *Tatwaffe.values(), *Tatort.values()).asList()
  private val _verdachtListe = mutableListOf<Verdacht>()
  val verdachtListe: List<Verdacht>
    get() = _verdachtListe.toList()
  val scorecard: Scorecard
  private var spieler: MitSpieler

  init {
    val anzahlKartenProSpieler = (karten.size - MitSpieler.NUMBER_CARDS_X) / (name.size + 1)
    val anzahlKartenDapp = karten.size - MitSpieler.NUMBER_CARDS_X - anzahlKartenProSpieler * (name.size + 1)
    spieler = MitSpieler(anzahlKartenProSpieler, anzahlKartenDapp)
    name.forEach { spieler.add(Spieler(it, anzahlKartenProSpieler)) }
    scorecard = Scorecard(karten, spieler)
  }

  /** Gibt alle Karten eines Typs zurück */
  fun getAlleKartenOfType(kartenTyp: Class<out Karte>) = karten.filterIsInstance(kartenTyp)

  /** Setzt die angegebene Karte für den entsprechenden Spieler und analysiert scorecard */
  fun setKarte(k: Karte, s: Spieler) {
    setKarte4Spieler(k, s)
    analyze()
  }

  /** Verarbeitung eines neuen Verdachts */
  fun addVerdacht(v: Verdacht) {
    _verdachtListe.add(v)
    // - alle, die Verdacht nicht widerlegt haben, haben keine der Karten
    var next = scorecard.mitSpieler.nextMitSpieler(v.erstelltVon)
    while (next != v.erstelltVon && next != v.widerlegtVon) {
      v.karten.forEach { scorecard.setStatus(it, next, Status.BESITZT_NICHT) }
      next = spieler.nextMitSpieler(next)
    }
    analyze()
  }

  /** Setzt die angegebene Karte für den entsprechenden Spieler auf BESITZT */
  private fun setKarte4Spieler(karte: Karte, currentSpieler: Spieler) {
    scorecard.setStatus(karte, currentSpieler, Status.BESITZT)
    // X besitzt dann keine andere Karte gleichen Typs
    if (currentSpieler == spieler.x) {
      getAlleKartenOfType(karte::class.java).filter { it != karte }.forEach { scorecard.setStatus(it, currentSpieler, Status.BESITZT_NICHT) }
    }
    // alle anderen Spieler haben Karte zwangsläufig nicht
    spieler.get().filter { it != currentSpieler }.forEach { scorecard.setStatus(karte, it, Status.BESITZT_NICHT) }
    // wenn fÜr den Spieler alle Karten bekannt sind, besitzt er die restlichen Karten nicht mehr
    if (scorecard.getKarten(currentSpieler, Status.BESITZT).size == currentSpieler.anzahlKarten) {
      karten.filter { scorecard.getStatus(it, currentSpieler) !== Status.BESITZT }
        .forEach { scorecard.setStatus(it, currentSpieler, Status.BESITZT_NICHT) }
    }
  }

  /** Analysiert Scorecard und Verdacht-Liste so lange, bis sich keine Veränderungen mehr ergeben */
  private fun analyze() {
    var after = scorecard.hashValue() // check if scorecard has changed
    var before: Int
    do {
      before = after
      analyzePlayers()
      analyzeKarten()
      analyzeVerdacht()
      after = scorecard.hashValue()
    } while (before != after)
  }

  private fun analyzeVerdacht() {
    // check widerlegte Verdacht
    _verdachtListe.filter { it.isWiderlegt } // widerlegt
      // map to [Widerleger, Map(Karte,Besitzer)]
      .map { v -> Pair(v.widerlegtVon!!, v.karten.associateBy({ it }, { scorecard.getBesitzer(it) })) } //
      // wenn genau eine Karte keinem Spieler zugeordnet ist und der Spieler, der widerlegt hat
      // keine der anderen zwei Karten besitzt, dann muss der 'Widerleger' diese Karte besitzen
      .filter { it.second.values.filterNotNull().size == 2 && it.first !in it.second.values }
      // map to Karte ohne Besitzer, Widerleger
      .map { Pair(it.second.keys.firstOrNull { karte -> it.second[karte] == null }, it.first) }
      // set Karte
      .forEach { setKarte4Spieler(it.first!!, it.second) }
  }

  private fun analyzeKarten() {
    karten.filter { scorecard.getBesitzer(it) == null } // // Alles über Karte bekannt
      .filter { scorecard.getSpieler(it, Status.BESITZT_NICHT).size == (spieler.get().size - 1) }
      // Falls Karte keinen Besitzer hat und alle außer einem Spieler die Karte Nicht besitzen,
      // dann besitzt dieser Spieler die Karte
      .forEach { setKarte4Spieler(it, scorecard.getSpieler(it, Status.UNBEKANNT)[0]) }
  }

  /** überprüft alle Karten der Spieler */
  private fun analyzePlayers() {
    spieler.get()
      // map Spieler,UNBEKANNT-Karten
      .map { Pair(it, scorecard.getKarten(it, Status.UNBEKANNT)) }
      // alles bekannt
      .filter { it.second.isNotEmpty() }
      // wenn Anzahl der 'UNBEKANNT'' Karten gleich der Anzahl der 'fehlenden' 'BESITZT' Karten ist
      // dann müssen diese Karten die richtigen sein
      .filter { it.second.size == it.first.anzahlKarten - scorecard.getKarten(it.first, Status.BESITZT).size } //
      .forEach {
        // setze alle NBEKANNT-Karten auf BESITZT für SPIELER
        it.second.forEach { karte -> setKarte4Spieler(karte, it.first) }
      }
    // Für 'X' gilt obige Aussage sogar für Karten gleichen Typs
    kartenTypen.map { scorecard.getKarten(spieler.x, it, Status.UNBEKANNT) }.filter { it.size == 1 }.forEach {
      setKarte4Spieler(it[0]!!, spieler.x)
    }
  }
}