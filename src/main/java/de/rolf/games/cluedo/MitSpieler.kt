package de.rolf.games.cluedo

class MitSpieler(numberCardsIch: Int, numberCardsDapp: Int) {

  companion object {
    const val NUMBER_CARDS_X = 3
  }

  val ich = Spieler("ich", numberCardsIch)

  // 'Mitspieler' - no Dapp and X
  private val mitSpieler = mutableListOf(ich)

  val dapp: Spieler = Spieler("Dapp", numberCardsDapp)

  // holds the solution
  val x = Spieler("X", NUMBER_CARDS_X)

  fun add(other: Spieler) = mitSpieler.add(other)

  /** returns all Mit-[Spieler] without dapp and X */
  fun getMitSpieler(): List<Spieler> {
    return mitSpieler
  }

  /** returns all [Spieler] incl. dapp and X */
  fun get(): List<Spieler> {
    return mitSpieler + listOf(dapp, x)
  }

  fun nextMitSpieler(current: Spieler) = mitSpieler[(mitSpieler.indexOf(current) + 1) % mitSpieler.size]

}