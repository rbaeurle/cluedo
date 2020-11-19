package de.rolf.games.cluedo

import de.rolf.games.cluedo.Karte.*
import java.io.File

private const val nl: String = "\n"

class CluedoGUI {

    private var scanner = java.util.Scanner(System.`in`)
    private lateinit var cluedo: Cluedo
    private lateinit var scorecard: Scorecard

    private fun readNextLine(): String {
        var eingabe = scanner.nextLine().trim()
        while (eingabe.trim().isEmpty()) {
            eingabe = scanner.nextLine().trim()
        }
        return eingabe
    }

    private fun init(inputFile: File?): Scorecard {

        val spielerNamen: MutableList<String> = ArrayList()
        if (inputFile != null) {
            scanner = java.util.Scanner(inputFile)
        }
        println()
        println("Andere Spieler in Reihenfolge eingeben:")
        var n = scanner.nextLine()

        while (n.trim().isNotEmpty()) {
            spielerNamen.add(n.trim())
            n = scanner.nextLine()
        }
        cluedo = Cluedo(spielerNamen)
        return cluedo.scorecard
    }

    fun run(inputFile: File?) {
        scorecard = init(inputFile)
        leseKarten(
            scorecard.mitSpieler.ich
        )
        printScorecard()
        var exit = false
        do {
            println("[0]: Ende - [1]: Verdacht - [2]: Dapp - [3]: Status")
            when (readNextLine()) {
                "0" -> {
                    if (bestaetigung) {
                        exit = true
                        println()
                        println("Verlasse Cluedo ....")
                    }
                }
                "1" -> {
                    leseVerdacht()
                    printScorecard()
                }
                "2" -> {
                    leseKarten(scorecard.mitSpieler.dapp)
                    printScorecard()
                }
                "3" -> {
                    scanner = java.util.Scanner(System.`in`)
                    printStatus()
                }
                else -> println("Falsche Eingabe !!$nl")
            }
        } while (!exit)
    }

    private val bestaetigung: Boolean
        get() {
            println("Eingabe OK (j/n)?")
            if ("j".equals(readNextLine(), ignoreCase = true)) {
                return true
            }
            return false
        }

    private fun leseVerdacht() {
        println()
        println("+++ Neuer Verdacht ++++")
        var v: Verdacht
        do {
            print("Wer hat Verdacht gestellt: ")
            printMitspieler()
            println()
            val von = scorecard.mitSpieler.getMitSpieler()[scanner.nextInt()]
            val kartenVerdacht = mutableListOf<Karte>()
            for (cz in cluedo.kartenTypen) {
                printKartenTyp(cz)
                kartenVerdacht.add(cluedo.getAlleKartenOfType(cz)[scanner.nextInt()])
            }
            v = Verdacht(von, kartenVerdacht[0] as Taeter, kartenVerdacht[1] as Tatwaffe, kartenVerdacht[2] as Tatort)
            println("Verdacht widerlegt (j/n)?")
            if ("j".equals(readNextLine(), ignoreCase = true)) {
                println("Spieler: ")
                printMitspieler()
                v.widerlegtVon = scorecard.mitSpieler.getMitSpieler()[scanner.nextInt()]
            }
            print("Folgender Verdacht wurde erstellt: ")
            printVerdacht(v)
        } while (!bestaetigung)
        cluedo.addVerdacht(v)

        // falls ich den Verdacht hervorgebracht habe, dann bekomme ich eine Karte zu sehen
        // kann unabhängig von Verdacht gesetzt werden ...
        if (v.isWiderlegt && "ich".equals(v.erstelltVon.name, ignoreCase = true)) {
            leseKarte(v.widerlegtVon, v.taeter, v.waffe, v.tatort)
        }

    }

    /**
     *
     */
    private fun printVerdacht(v: Verdacht) {
        val widerLeger = if (v.widerlegtVon != null) v.widerlegtVon!!.name else "(niemand)"
        println(
            " %-7s [%-7s, %-12s, %-12s] --> %-7s".format(
                v.erstelltVon.name,
                v.taeter,
                v.waffe,
                v.tatort,
                widerLeger
            )
        )
    }

    /** Lese Karten für den angegeben Spieler */
    private fun leseKarten(s: Spieler) {
        println("${nl}Gib alle ${s.anzahlKarten} Karten von Spieler '${s.name}' ein:")
        printKartenAuswahl()
        println()
        for (i in 0 until s.anzahlKarten) {
            print("Nummer: ")
            cluedo.setKarte(scorecard.karten[scanner.nextInt()], s)
        }
    }

    private fun leseKarte(s: Spieler?, vararg k: Karte) {
        print("${nl}Gib gezeigte Karte ein:")
        for (i in k.indices) {
            print("[$i]: ${String.format("%-17s", k[i])}")
        }
        println("${nl}Nummer: ")
        cluedo.setKarte(k[scanner.nextInt()], s!!)
    }

    private fun printMitspieler() {
        val ms = scorecard.mitSpieler.getMitSpieler()
        for (i in ms.indices) {
            print("[$i]: ${String.format("%-12s", ms[i].name)}")
        }
    }

    private fun printKartenTyp(kartenTyp: Class<out Karte?>) {
        val k = cluedo.getAlleKartenOfType(kartenTyp)
        print("${nl}${String.format("%-10s", kartenTyp.simpleName)} : ")
        for (i in k.indices) {
            print("[$i]: ${String.format("%-17s", k[i])}")
        }
        println()
    }

    private fun printKartenAuswahl() {
        val k = scorecard.karten
        var kartenTyp: String? = null
        for (i in k.indices) {
            if (k[i].javaClass.simpleName != kartenTyp) {
                print("${nl}${String.format("%-10s", k[i].javaClass.simpleName)} : ")
                kartenTyp = k[i].javaClass.simpleName
            }
            print("[${String.format("%2d", i)}]: ${String.format("%-17s", k[i])}")
        }
    }

    private fun printScorecard() {
        val spieler = scorecard.mitSpieler
        println()
        println("**** SCORECARD")
        print("${nl}${String.format("%15s", "")}")
        spieler.get().map {
            String.format(
                "%15s%-8s",
                it.name,
                "(${it.anzahlKarten}/${scorecard.getKarten(it, Status.BESITZT).size}/${
                    scorecard.getKarten(
                        it,
                        Status.UNBEKANNT
                    ).size
                })"
            )
        }.forEach { print(it) }
        println()
        for (karte in scorecard.karten) {
            print(String.format("%15s", karte))
            for (sp in spieler.get()) {
                print(
                    "${String.format("%15s", getStatusSign(karte, sp))}${
                        String.format(
                            "%8s",
                            ""
                        )
                    }"
                )
            }
            println()
        }
        println()
    }

    private fun getStatusSign(karte: Karte, spieler: Spieler): Char {
        val status = scorecard.getStatus(karte, spieler)
        val sign = when (status) {
            Status.BESITZT -> 'x'
            Status.BESITZT_NICHT -> '-'
            else -> {
                if (cluedo.maybe(karte,spieler)) {
                    'o'
                } else {
                    ' '
                }
            }
        }
        return sign
    }

    private fun printStatus() {
        println(" **** S T A T U S *****")
        println(" **** Verdaechtigungen ")
        for (v in cluedo.verdachtListe) {
            println()
            printVerdacht(v)
        }
        println()
        printScorecard()
    }
}