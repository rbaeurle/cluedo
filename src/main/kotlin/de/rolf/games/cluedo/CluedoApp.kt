package de.rolf.games.cluedo

import java.io.File

fun main(args: Array<String>) {
  var inputFile : File? = null
  if (args.size == 1) {
    inputFile = File(args[0])
    require(inputFile.exists()) {"Eingabedatei ${args[0]} nicht gefunden"}
  }
  CluedoGUI().run(inputFile)
}