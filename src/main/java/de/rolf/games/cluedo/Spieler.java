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
   private final String name;
   private final int anzahlKarten;

}

