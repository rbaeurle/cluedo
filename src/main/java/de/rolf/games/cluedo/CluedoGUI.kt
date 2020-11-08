package de.rolf.games.cluedo

import de.rolf.games.cluedo.Karte.Taeter
import de.rolf.games.cluedo.Karte.Tatort
import de.rolf.games.cluedo.Karte.Tatwaffe
import org.springframework.stereotype.Component
import java.util.*

/**
 * @author brf
 */
@Component
class CluedoGUI {
  private val formatter: Formatter
  private var cluedo: Cluedo? = null
  private var scanner: Scanner
  private var scorecard: Scorecard? = null

  init {
    scanner = Scanner(System.`in`)
    formatter = Formatter(System.out)
  }

  private fun init(): Scorecard {
    val spielerNamen: MutableList<String> = ArrayList()
    val source =
      "Maike" + System.lineSeparator() +
          "Annso" + System.lineSeparator() + "Emilie" + System.lineSeparator() + "" + System.lineSeparator() + "0" + System.lineSeparator() + "4" + System.lineSeparator() + "6" + System.lineSeparator() + "15" + System.lineSeparator() + "21" + System.lineSeparator() + "" + System.lineSeparator() +  // DAPP
          //        "2" + System.lineSeparator() +
          //        "1" + System.lineSeparator() +
          // Verdacht
          "1" + System.lineSeparator() + "0" + System.lineSeparator() + "1" + System.lineSeparator() + "0" + System.lineSeparator() + "3" + System.lineSeparator() + "n" + System.lineSeparator() + "j" + "" + System.lineSeparator()
    scanner = Scanner(source)
    formatter.format("%nAndere Spieler in Reihenfolge eingeben:%n")
    var n = scanner.nextLine()
    while (n != null && !n.trim { it <= ' ' }.isEmpty()) {
      spielerNamen.add(n)
      n = scanner.nextLine()
    }
    cluedo = Cluedo(spielerNamen)
    return cluedo!!.scorecard
  }

  fun run() {
    scorecard = init()
    leseKarten(
      scorecard!!.mitSpieler.ich
    )
    printScorecard()
    var exit = false
    do {
      formatter.format("[0]: Ende - [1]: Verdacht - [2]: Dapp - [3]: Status%n")
      val input = readNextLine()
      if ("0".equals(input, ignoreCase = true)) {
        if (bestaetigung) {
          exit = true
          formatter.format("%nVerlasse Cluedo .... %n%n%n")
        }
      } else if ("1".equals(input, ignoreCase = true)) {
        leseVerdacht()
        printScorecard()
        scanner = Scanner(System.`in`)
      } else if ("2".equals(input, ignoreCase = true)) {
        leseKarten(
          scorecard!!.mitSpieler.dapp
        )
        printScorecard()
      } else if ("3".equals(input, ignoreCase = true)) {
        printStatus()
      } else {
        formatter.format("Falsche Eingabe !!%n%n")
      }
      scanner = Scanner(System.`in`)
    } while (!exit)
  }

  private val bestaetigung: Boolean
    get() {
      var b = false
      formatter.format("%nEingabe OK (j/n)? ")
      val eingabe = readNextLine()
      if ("j".equals(eingabe, ignoreCase = true)) {
        b = true
      }
      return b
    }

  private fun readNextLine(): String {
    var eingabe = scanner.nextLine()
    while (eingabe.trim { it <= ' ' }.isEmpty()) {
      eingabe = scanner.nextLine()
    }
    return eingabe
  }

  private fun leseVerdacht() {
    formatter.format("%n+++ Neuer Verdacht ++++%n")
    var v: Verdacht
    do {
      formatter.format("%s", "Wer hat Verdacht gestellt: ")
      printMitspieler()
      formatter.format("%n")
      var idx = scanner.nextInt()
      val von = scorecard!!.mitSpieler.getMitSpieler()[idx]
      var i = 0
      val kartenVerdacht = arrayOfNulls<Karte>(3)
      for (cz in cluedo!!.kartenTypen) {
        printKartenTyp(cz)
        idx = scanner.nextInt()
        kartenVerdacht[i++] = cluedo!!.getAlleKartenOfType(cz)[idx]
      }
      v = Verdacht(von, (kartenVerdacht[0] as Taeter?)!!, (kartenVerdacht[1] as Tatwaffe?)!!, (kartenVerdacht[2] as Tatort?)!!)
      formatter.format("%s", "Verdacht widerlegt (j/n)? ")
      val a = readNextLine()
      if ("j".equals(a, ignoreCase = true)) {
        formatter.format("%s", "Spieler: ")
        printMitspieler()
        idx = scanner.nextInt()
        val widerlegtVon = scorecard!!.mitSpieler.getMitSpieler()[idx]
        v.widerlegtVon = widerlegtVon
      }
      formatter.format("%s", "Folgender Verdacht wurde erstellt: ")
      printVerdacht(v)
    } while (!bestaetigung)
    cluedo!!.addVerdacht(v)

    // falls ich den Verdacht hervorgebracht habe, dann bekomme ich eine Karte zu sehen
    // kann unabhängig von Verdacht gesetzt werden ...
    if (v.isWiderlegt && "ich".equals(
        v.erstelltVon.name, ignoreCase = true
      )
    ) {
      leseKarte(v.widerlegtVon, v.taeter, v.waffe, v.tatort)
    }
  }

  /**
   *
   */
  private fun printVerdacht(v: Verdacht) {
    val widerLeger = if (v.widerlegtVon != null) v.widerlegtVon!!.name else "(niemand)"
    formatter.format(
      " %-7s [%-7s, %-12s, %-12s] --> %-7s", v.erstelltVon.name, v.taeter, v.waffe, v.tatort, widerLeger
    )
  }

  /**
   * Lese Karten für den angegeben Spieler
   *
   * @param s Spieler
   */
  private fun leseKarten(s: Spieler) {
    formatter.format("%nGib alle %s Karten von Spieler '%s' ein:%n", s.anzahlKarten, s.name)
    printKartenAuswahl()
    formatter.format("%n")
    for (i in 0 until s.anzahlKarten) {
      formatter.format("%s", "Nummer: ")
      val idx = scanner.nextInt()
      cluedo!!.setKarte(
        scorecard!!.karten[idx], s
      )
    }
  }

  private fun leseKarte(s: Spieler?, vararg k: Karte) {
    formatter.format("%nGib gezeigte Karte ein:")
    for (i in 0 until k.size) {
      formatter.format("[%d]: %-17s", i, k[i])
    }
    formatter.format("%n")
    formatter.format("%s", "Nummer: ")
    val idx = scanner.nextInt()
    cluedo!!.setKarte(k[idx], s!!)
  }

  private fun printMitspieler() {
    val ms = scorecard!!.mitSpieler.getMitSpieler()
    for (i in ms.indices) {
      formatter.format(
        "[%d]: %-12s", i, ms[i].name
      )
    }
  }

  private fun printKartenTyp(kartenTyp: Class<out Karte?>) {
    val k = cluedo!!.getAlleKartenOfType(kartenTyp)
    formatter.format("%n%-10s : ", kartenTyp.simpleName)
    for (i in k.indices) {
      formatter.format("[%2d]: %-17s", i, k[i])
    }
    formatter.format("%n")
  }

  private fun printKartenAuswahl() {
    val k = scorecard!!.karten
    var kartenTyp: String? = null
    for (i in k.indices) {
      if (k[i].javaClass.simpleName != kartenTyp) {
        formatter.format(
          "%n%-10s : ", k[i].javaClass.simpleName
        )
        kartenTyp = k[i].javaClass.simpleName
      }
      formatter.format("[%2d]: %-17s", i, k[i])
    }
  }

  private fun printScorecard() {
    val spieler = scorecard!!.mitSpieler
    formatter.format("%n%n%15s", "")
    spieler.get().stream().map { s: Spieler ->
      String.format(
        "%15s%-8s",
        s.name,
        "(" + s.anzahlKarten + '/' + scorecard!!.getKarten(s, Status.BESITZT).size + '/' + scorecard!!.getKarten(s, Status.UNBEKANNT).size + ')'
      )
    }.forEach { s -> formatter.format(s) }
    formatter.format("%n")
    for (karte in scorecard!!.karten) {
      formatter.format("%15s", karte)
      for (sp in spieler.get()) {
        formatter.format("%15s%8s", getStatusSign(scorecard!!.getStatus(karte, sp)), "")
      }
      formatter.format("%n%n")
    }
  }

  private fun getStatusSign(status: Status): Char {
    var c = ' '
    if (status === Status.BESITZT) {
      c = 'x'
    } else if (status === Status.BESITZT_NICHT) {
      c = '-'
    }
    return c
  }

  private fun printStatus() {
    formatter.format("%s", " **** S T A T U S *****")
    formatter.format("%s", " **** Verdaechtigungen ")
    for (v in cluedo!!.verdachtListe) {
      formatter.format("%n")
      printVerdacht(v)
    }
    formatter.format("%n")
    printScorecard()
  }

}