package de.rolf.games.cluedo;

/**
 * @author brf
 *
 */
public interface Karte
{

   enum Taeter implements Karte
   {
      GATOW, // Oberst
      PORZ, // Baronin von
      BLOOM, // Professor
      GRUEN, // Direktor
      MING, // Fräulein
      WEISS // Frau
   }
   
   enum Tatwaffe implements Karte
   {
      PISTOLE,
      KERZENLEUCHTER,
      SEIL,
      MESSER,
      KEULE,
      AXT,
      HANTEL,
      TROPHAEE,
      GIFT
   }
   
   enum Tatort implements Karte
   {
      KUECHE,
      SPEISEZIMMER,
      GAESTEHAUS,
      EINGANGSHALLE,
      OBSERVATORIUM,
      WOHNZIMMER,
      HEIMKINO,
      WELLNESSRAUM,
      TERASSE
   }
}
