package de.rolf.games.cluedo

import de.rolf.games.cluedo.Karte.Taeter
import de.rolf.games.cluedo.Karte.Tatort
import de.rolf.games.cluedo.Karte.Tatwaffe

data class Verdacht(val erstelltVon: Spieler, val taeter: Taeter, val waffe: Tatwaffe, val tatort: Tatort, var widerlegtVon: Spieler? = null) {

  val isWiderlegt: Boolean
    get() = widerlegtVon != null

  val karten: List<Karte>
    get() = listOf(taeter, waffe, tatort)
}