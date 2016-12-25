package org.stummi.ivcalculator;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PokemonStatFinder {
	private final PokemonType type;
	private int cp;
	private int hp;
	private int stardust;

	private StatRating ivSumRating;
	private StatRating bestIvRating;

	private boolean staminaIsBest;
	private boolean attackIsBest;
	private boolean defenseIsBest;

	public PokemonStatFinder hasCp(int newCp) {
		this.cp = newCp;
		return this;
	}

	public PokemonStatFinder hasHp(int newHp) {
		this.hp = newHp;
		return this;
	}

	public PokemonStatFinder hasStardustCost(int stardustCost) {
		this.stardust = stardustCost;
		return this;
	}

	public PokemonStatFinder staminaIsBest() {
		staminaIsBest = true;
		return this;
	}

	public PokemonStatFinder attackIsBest() {
		attackIsBest = true;
		return this;
	}

	public PokemonStatFinder defenseIsBest() {
		defenseIsBest = true;
		return this;
	}

	public PokemonStatFinder hasIvSumRating(StatRating rating) {
		this.ivSumRating = rating;
		return this;
	}

	public PokemonStatFinder hasBestIvRating(StatRating rating) {
		this.bestIvRating = rating;
		return this;
	}

	public Stream<Pokemon> findAllPossibleStats() {
		Stream<PokemonLevel> level = PokemonLevel.stream();

		// apply filter to levels based on stardust
		if (stardust != 0) {
			level = level.filter(l -> l.startdustCost() == stardust);
		}

		// create pokemon stream for level
		Stream<Pokemon> pokeStream = level.flatMap(this::createPokemonStreamForLevel);

		// apply cp filter
		if (cp > 0) {
			pokeStream = pokeStream.filter(p -> p.getCp() == cp);
		}

		if (attackIsBest || staminaIsBest || defenseIsBest) {
			pokeStream = pokeStream.filter(this::bestStatsFilter);
		}

		if (ivSumRating != null) {
			pokeStream = pokeStream.filter(p -> p.getIvSumRating() == ivSumRating);
		}

		if (bestIvRating != null) {
			pokeStream = pokeStream.filter(p -> p.getBestIvRating() == bestIvRating);
		}

		return pokeStream;
	}

	private boolean bestStatsFilter(Pokemon mon) {
		int max = mon.getBestIv();
		boolean att = mon.getIvAttack() == max;
		boolean def = mon.getIvDefense() == max;
		boolean stam = mon.getIvStamina() == max;

		return att == attackIsBest && def == defenseIsBest && stam == staminaIsBest;
	}

	private Stream<Pokemon> createPokemonStreamForLevel(PokemonLevel level) {
		IntStream staminaStream = ivRange();
		double cpm = level.cpMultiplier();
		int baseStamina = type.getBaseStaminia();

		if (hp > 0) {
			staminaStream = staminaStream.filter(s -> Math.max((int) (cpm * (baseStamina + s)), 10) == hp);
		}

		return staminaStream.flatMap(s -> IntStream.range(0, 256).map(i -> (i << 4) | s)).mapToObj(iv -> type.create(level, iv));
	}

	private static IntStream ivRange() {
		return IntStream.range(0, 16);
	}

}
