package de.rolf.games.cluedo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ScorecardTest {

  lateinit var scorecard: Scorecard

  private val numberCardsPerPlayer = 5
  private val spielerA = Spieler("A", numberCardsPerPlayer)
  private val spielerB = Spieler("B", numberCardsPerPlayer)
  private val spielerC = Spieler("C", numberCardsPerPlayer)

  @BeforeEach
  fun init() {
    val mitSpieler = MitSpieler(5, 1)
    mitSpieler.add(spielerA)
    mitSpieler.add(spielerB)
    mitSpieler.add(spielerC)
    scorecard = Scorecard(
      listOf(
        Karte.Taeter.BLOOM, Karte.Taeter.MING, Karte.Tatwaffe.AXT, Karte.Tatwaffe.GIFT, Karte.Tatort.KUECHE, Karte.Tatort.HEIMKINO
      ), mitSpieler
    )
  }

  @Test
  fun `test scorecard set status`() {
    var actual = scorecard.setStatus(Karte.Tatwaffe.AXT, spielerA, Status.BESITZT)
    assertThat(actual).isEqualTo(true)
    actual = scorecard.setStatus(Karte.Tatwaffe.AXT, spielerA, Status.BESITZT_NICHT)
    assertThat(actual).isEqualTo(false)
    // should never be done
    actual = scorecard.setStatus(Karte.Tatwaffe.AXT, spielerA, Status.UNBEKANNT)
    assertThat(actual).isEqualTo(false)
  }

  @Test
  fun `test scorecard set status failed`() {
    val actual = scorecard.setStatus(Karte.Tatwaffe.AXT, spielerA, Status.BESITZT)
    assertThat(actual).isEqualTo(true)
    assertFailsWith<IllegalStateException> { scorecard.setStatus(Karte.Tatwaffe.AXT, spielerB, Status.BESITZT) }
  }

  @Test
  fun `test get karten for spieler in status`() {
    scorecard.setStatus(Karte.Tatwaffe.AXT, spielerA, Status.BESITZT)
    var actual = scorecard.getKarten(spielerA, Status.BESITZT)
    assertThat(actual).containsExactlyInAnyOrder(Karte.Tatwaffe.AXT)
    actual = scorecard.getKarten(spielerA, Status.UNBEKANNT)
    assertThat(actual).containsExactlyInAnyOrder(
      Karte.Taeter.BLOOM, Karte.Taeter.MING, Karte.Tatwaffe.GIFT, Karte.Tatort.KUECHE, Karte.Tatort.HEIMKINO
    )
  }

  @Test
  fun `test get karten for spieler from Typ in status`() {
    scorecard.setStatus(Karte.Tatwaffe.AXT, spielerA, Status.BESITZT)
    scorecard.setStatus(Karte.Taeter.MING, spielerC, Status.BESITZT)

    val actual = scorecard.getKarten(spielerA, Karte.Tatwaffe::class.java, Status.BESITZT)
    assertThat(actual).containsExactlyInAnyOrder(Karte.Tatwaffe.AXT)

  }

  @Test
  fun `test get Besitzer fuer Karte`() {
    scorecard.setStatus(Karte.Taeter.MING, spielerC, Status.BESITZT)
    var actual = scorecard.getBesitzer(Karte.Taeter.MING)
    assertThat(actual).isEqualTo(spielerC)
    actual = scorecard.getBesitzer(Karte.Tatort.HEIMKINO)
    assertThat(actual).isNull()
  }

  @Test
  fun `test get Spieler fuer Karte in status`() {
    scorecard.setStatus(Karte.Taeter.BLOOM, spielerC, Status.BESITZT)
    scorecard.setStatus(Karte.Taeter.BLOOM, spielerA, Status.BESITZT_NICHT)
    scorecard.setStatus(Karte.Taeter.BLOOM, spielerB, Status.BESITZT_NICHT)

    var actual = scorecard.getSpieler(Karte.Taeter.BLOOM, Status.BESITZT)
    assertThat(actual).containsExactlyInAnyOrder(spielerC)
    actual = scorecard.getSpieler(Karte.Taeter.BLOOM, Status.BESITZT_NICHT)
    assertThat(actual).containsExactlyInAnyOrder(spielerB, spielerA)
    actual = scorecard.getSpieler(Karte.Taeter.MING, Status.BESITZT)
    assertThat(actual).isEmpty()
    actual = scorecard.getSpieler(Karte.Taeter.MING, Status.UNBEKANNT)
    assertThat(actual).containsExactlyInAnyOrder(spielerB, spielerA, spielerC, scorecard.mitSpieler.dapp, scorecard.mitSpieler.x, scorecard
      .mitSpieler.ich)
  }

}