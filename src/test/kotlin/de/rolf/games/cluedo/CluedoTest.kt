package de.rolf.games.cluedo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CluedoTest {

  lateinit var cluedo: Cluedo
  lateinit var scorecard: Scorecard

  @BeforeEach
  fun init() {
    cluedo = Cluedo(listOf("A", "B", "C"))
    scorecard = cluedo.scorecard
  }

  @Test
  fun `get all karten of one type`() {
    val actual = cluedo.getAlleKartenOfType(Karte.Taeter::class.java)
    assertThat(actual).containsExactlyInAnyOrder(
      Karte.Taeter.MING, Karte.Taeter.BLOOM, Karte.Taeter.GATOW, Karte.Taeter.GRUEN, Karte.Taeter.PORZ, Karte.Taeter.WEISS
    )
  }

  @Test
  fun `add Verdacht`() {
    var verdacht = Verdacht(scorecard.mitSpieler.ich, Karte.Taeter.MING, Karte.Tatwaffe.SEIL, Karte.Tatort.WELLNESSRAUM)
    verdacht.widerlegtVon = scorecard.mitSpieler.getMitSpieler()[3]
    cluedo.addVerdacht(verdacht)
    var actual = cluedo.verdachtListe
    assertThat(actual.size).isEqualTo(1)
    assertThat(actual[0].erstelltVon).isEqualTo(scorecard.mitSpieler.ich)
    verdacht = Verdacht(scorecard.mitSpieler.getMitSpieler()[0], Karte.Taeter.WEISS, Karte.Tatwaffe.TROPHAEE, Karte.Tatort.SPEISEZIMMER)
    cluedo.addVerdacht(verdacht)
    actual = cluedo.verdachtListe
    assertThat(actual.size).isEqualTo(2)
  }

  @Test
  fun `analyze Spieler - Part 1`() {
    val player = scorecard.mitSpieler.get()[0]
    assertThat(player.anzahlKarten).isEqualTo(5)
    assertThat(scorecard.getKarten(player, Status.UNBEKANNT).size).isEqualTo(24)
    assertThat(scorecard.getKarten(player, Status.BESITZT).size).isEqualTo(0)
    assertThat(scorecard.getKarten(player, Status.BESITZT_NICHT).size).isEqualTo(0)

    // set 19 cards to BESITZT_NICHT -> others must be set to BESITZT then
    scorecard.karten.subList(0, 19).forEach { scorecard.setStatus(it, player, Status.BESITZT_NICHT) }
    // trigger analyze ...
    cluedo.setKarte(Karte.Taeter.WEISS, scorecard.mitSpieler.dapp)
    assertThat(scorecard.getKarten(player, Status.BESITZT).size).isEqualTo(5)
  }

  @Test
  fun `analyze Spieler - Part 2 for X`() {
    val x = scorecard.mitSpieler.x
    assertThat(x.anzahlKarten).isEqualTo(3)
    assertThat(scorecard.getKarten(x, Status.UNBEKANNT).size).isEqualTo(24)
    assertThat(scorecard.getKarten(x, Status.BESITZT).size).isEqualTo(0)
    assertThat(scorecard.getKarten(x, Status.BESITZT_NICHT).size).isEqualTo(0)

    // set 5 Taeter to BESITZT_NICHT -> others must be set to BESITZT then
    cluedo.getAlleKartenOfType(Karte.Taeter::class.java).subList(0, 5).forEach { scorecard.setStatus(it, x, Status.BESITZT_NICHT) }
    // trigger analyze ...
    cluedo.setKarte(Karte.Tatort.WELLNESSRAUM, scorecard.mitSpieler.dapp)
    assertThat(scorecard.getKarten(x, Status.BESITZT).size).isEqualTo(1)
  }

  @Test
  fun `analyze Karten`() {
    val player = scorecard.mitSpieler.get().random()
    assertThat(scorecard.getKarten(player, Status.UNBEKANNT).size).isEqualTo(24)

    // set Karte to status BESITZT_NICHT for all other players
    val karte = Karte.Taeter.MING
    scorecard.mitSpieler.get().filterNot { it == player }.forEach { scorecard.setStatus(karte, it, Status.BESITZT_NICHT) }
    // trigger analyze ...
    cluedo.setKarte(Karte.Taeter.WEISS, scorecard.mitSpieler.dapp)
    // check that player BESITZT Karte MING now
    assertThat(scorecard.getBesitzer(karte)).isEqualTo(player)
  }

  @Test
  fun `analyze Verdacht - widerlegt and identified`() {
    // prepare scorecard
    scorecard.setStatus(Karte.Taeter.MING, scorecard.mitSpieler.getMitSpieler()[2], Status.BESITZT)
    scorecard.setStatus(Karte.Tatwaffe.SEIL, scorecard.mitSpieler.getMitSpieler()[3], Status.BESITZT)
    assertThat(scorecard.getBesitzer(Karte.Tatort.WELLNESSRAUM)).isNull()
    // prepare Verdacht
    val verdacht = Verdacht(scorecard.mitSpieler.ich, Karte.Taeter.MING, Karte.Tatwaffe.SEIL, Karte.Tatort.WELLNESSRAUM)
    verdacht.widerlegtVon = scorecard.mitSpieler.getMitSpieler()[1]
    cluedo.addVerdacht(verdacht)


  }

}
