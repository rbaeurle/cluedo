package de.rolf.games.cluedo

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class MitSpielerTest {

  lateinit var mitSpieler: MitSpieler

  @BeforeEach
  fun init() {
    val numberCardsPerPlayer = 5
    mitSpieler = MitSpieler(numberCardsPerPlayer,1)
    mitSpieler.add(Spieler("M", numberCardsPerPlayer))
    mitSpieler.add(Spieler("A", numberCardsPerPlayer))
    mitSpieler.add(Spieler("E", numberCardsPerPlayer))
  }

  @Test
  fun `test get all Spieler`() {
    val actual = mitSpieler.get()
    assertThat(actual.size).isEqualTo(6)
    assertThat(actual.map { it.name }).containsExactlyInAnyOrder("ich", "M", "A", "E", "X", "Dapp")
  }

  @Test
  fun `test get all MitSpieler`() {
    val actual = mitSpieler.getMitSpieler()
    assertThat(actual.size).isEqualTo(4)
    assertThat(actual.map { it.name }).containsExactlyInAnyOrder("ich", "M", "A", "E")
  }

  @Test
  fun `test next MitSpieler`() {
    var actual = mitSpieler.ich
    assertThat(actual).isEqualTo(mitSpieler.getMitSpieler()[0])

    actual = mitSpieler.nextMitSpieler(actual)
    assertThat(actual).isEqualTo(mitSpieler.getMitSpieler()[1])

    actual = mitSpieler.nextMitSpieler(actual)
    assertThat(actual).isEqualTo(mitSpieler.getMitSpieler()[2])

    actual = mitSpieler.nextMitSpieler(actual)
    assertThat(actual).isEqualTo(mitSpieler.getMitSpieler()[3])

    actual = mitSpieler.nextMitSpieler(actual)
    assertThat(actual).isEqualTo(mitSpieler.getMitSpieler()[0])
  }
}