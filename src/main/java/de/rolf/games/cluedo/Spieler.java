/**
 * 
 */
package de.rolf.games.cluedo;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author brf
 *
 */
@Data
@RequiredArgsConstructor
public class Spieler
{

   @NonNull
   final private String name;
   final private int anzahlKarten;

}

