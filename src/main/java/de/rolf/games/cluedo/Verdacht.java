/**
 * 
 */
package de.rolf.games.cluedo;

import java.util.stream.Stream;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author brf
 *
 */
@Data
@RequiredArgsConstructor
class Verdacht
{
   private final Spieler        erstelltVon;
   private Spieler              widerlegtVon;

   private final Karte.Taeter   taeter;
   private final Karte.Tatwaffe waffe;
   private final Karte.Tatort   tatort;

   boolean isWiderlegt()
   {
      return widerlegtVon != null;
   }

   Stream<Karte> getKarten() {
      return Stream.of(taeter, waffe, tatort);
   }

}
