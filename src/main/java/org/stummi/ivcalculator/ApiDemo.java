package org.stummi.ivcalculator;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// EXample how to use the API
public class ApiDemo {
	public static void main(String[] args) throws IOException {
		PokemonDb db = PokemonDb.loadDefault();
		PokemonDbHelper hlp = new PokemonDbHelper(db);

		PokemonStatFinder finder = hlp.magnemite().createStatFinder();
		List<Pokemon> list = finder //
				.hasCp(270).hasHp(22) //
				.hasStardustCost(1000) //
				.hasIvSumRating(StatRating.BAD) //
				.attackIsBest().hasBestIvRating(StatRating.GOOD).findAllPossibleStats().collect(Collectors.toList());

		new StatFindReport(list, System.out).print();
	}
}
