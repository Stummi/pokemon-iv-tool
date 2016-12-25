package org.stummi.ivcalculator;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.Value;

@Value
public class PokemonType {
	private int pokeId, baseStaminia, baseAttack, baseDefense;
	private String name;

	public Pokemon create(PokemonLevel level, int ivStaminia, int ivAttack, int ivDefense) {
		return new Pokemon(this, level, ivStaminia, ivAttack, ivDefense);
	}

	public PokemonStatFinder createStatFinder() {
		return new PokemonStatFinder(this);
	}

	public Stream<Pokemon> allCombinations() {
		return PokemonLevel.stream().flatMap(lvl -> IntStream.range(0, 4096).mapToObj(i -> create(lvl, i)));
	}

	Pokemon create(PokemonLevel lvl, int i) {
		int stamina = i & 0x0f;
		int attack = (i & 0xf0) >> 4;
		int defense = (i & 0xf00) >> 8;
		return create(lvl, stamina, attack, defense);
	}

	@Override
	public String toString() {
		return name;
	}

}
