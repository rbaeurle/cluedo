setlocal
set PATH=d:\tools\kotlin-compiler-1.4.20\kotlinc\bin;%PATH%

kotlinc src/main/kotlin/de/rolf/games/cluedo/Karte.kt src/main/kotlin/de/rolf/games/cluedo/Spieler.kt src/main/kotlin/de/rolf/games/cluedo/Verdacht.kt src/main/kotlin/de/rolf/games/cluedo/CluedoApp.kt src/main/kotlin/de/rolf/games/cluedo/CluedoGUI.kt src/main/kotlin/de/rolf/games/cluedo/Cluedo.kt src/main/kotlin/de/rolf/games/cluedo/Scorecard.kt src/main/kotlin/de/rolf/games/cluedo/MitSpieler.kt -include-runtime -d target/classes
