package de.rolf.games.cluedo

/**
 * @author brf
 */
interface Karte {

  enum class Taeter : Karte {
    GATOW,  // Oberst
    PORZ,  // Baronin von
    BLOOM,  // Professor
    GRUEN,  // Direktor
    MING,  // Fräulein
    WEISS // Frau
  }

  enum class Tatwaffe : Karte {
    PISTOLE, KERZENLEUCHTER, SEIL, MESSER, KEULE, AXT, HANTEL, TROPHAEE, GIFT
  }

  enum class Tatort : Karte {
    KUECHE, SPEISEZIMMER, GAESTEHAUS, EINGANGSHALLE, OBSERVATORIUM, WOHNZIMMER, HEIMKINO, WELLNESSRAUM, TERASSE
  }
}