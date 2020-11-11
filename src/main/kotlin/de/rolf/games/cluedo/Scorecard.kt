package de.rolf.games.cluedo

class Scorecard(val karten: List<Karte>, val mitSpieler: MitSpieler) {

  private val spieler = mitSpieler.get()
  private val scorecard: Array<Array<Status>> = Array(karten.size) { Array(spieler.size) { Status.UNBEKANNT } }

  /** returns a hash value for the scorecard */
  fun hashValue(): Int {
    var hash = 7
    var index = 1
    scorecard.flatten().forEach { hash = 31 * hash + it.ordinal + index++ }
    return hash
  }

  /** set status for karte/spieler */
  fun setStatus(k: Karte, s: Spieler, status: Status): Boolean {
    val actStatus = scorecard[karten.indexOf(k)][spieler.indexOf(s)]
    if (actStatus == status) {
      return true
    }
    // check if other player has card already
    val other = scorecard[karten.indexOf(k)].mapIndexed { index, st ->
      if (index != spieler.indexOf(s) && st == Status.BESITZT && status == Status.BESITZT) spieler[index] else null
    }.filterNotNull()
    if (other.isNotEmpty()) {
      System.err.println("Spieler $other besitzt bereits Karte $k")
      throw IllegalStateException("Spieler $other besitzt bereits Karte $k")
    }

    var statusChangeOk = true
    // check (in)consistency
    if (actStatus != Status.UNBEKANNT && actStatus != status) {
      System.err.println("Status für ${s.name}/$k wurde von $actStatus nach $status geändert !!!")
      statusChangeOk = false
    }
    println("${s.name} $status $k")
    scorecard[karten.indexOf(k)][spieler.indexOf(s)] = status
    return statusChangeOk
  }

  /** Gets the status  */
  fun getStatus(k: Karte, s: Spieler) = scorecard[karten.indexOf(k)][spieler.indexOf(s)]

  /** Gibt den Besitzer der entsprechenden Karten zurück, oder null, wenn Besitzer noch unbekannt ist */
  fun getBesitzer(k: Karte): Spieler? {
    for (i in spieler.indices) {
      if (scorecard[karten.indexOf(k)][i] == Status.BESITZT) {
        return spieler[i]
      }
    }
    return null
  }

  /** return Alle Spieler, die die angegebene Karte im entsprechendem Zustand besitzen */
  fun getSpieler(k: Karte, status: Status) = scorecard[karten.indexOf(k)].mapIndexed { index, st ->
    if (st == status) index else null
  }.filterNotNull().map { spieler[it] }

  /** return alle Karten, die der Spieler im entsprechendem Zustand besitzt  */
  fun getKarten(s: Spieler, status: Status) = karten.filter { scorecard[karten.indexOf(it)][spieler.indexOf(s)] == status }.toList()

  /** return alle Karten vom Kartentyp, die der Spieler im entsprechendem Zustand besitzt  */
  fun getKarten(s: Spieler, kartenType: Class<out Karte?>, status: Status) = getKarten(s, status).filterIsInstance(kartenType)

}

enum class Status {
  UNBEKANNT, // bisher unbekannt
  BESITZT_NICHT, // Spieler hat Karte nicht
  BESITZT // Spieler hat Karte
}
