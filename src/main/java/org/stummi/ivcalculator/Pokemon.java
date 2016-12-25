package org.stummi.ivcalculator;

import static java.lang.Math.sqrt;

import lombok.Value;

@Value
public class Pokemon {
	PokemonType type;
	PokemonLevel level;
	int ivStamina, ivAttack, ivDefense;

	public double getInternalCp() {
		double cpM = level.cpMultiplier();
		return (type.getBaseAttack() + ivAttack) * sqrt(type.getBaseDefense() + ivDefense) * sqrt(type.getBaseStaminia() + ivStamina) * (cpM * cpM)
				/ 10D;
	}

	public double getInternalHp() {
		return level.cpMultiplier() * (type.getBaseStaminia() + ivStamina);
	}

	public double perfection() {
		return getIvSum() / 45D;
	}

	public int getCp() {
		int cp = (int) getInternalCp();
		return cp < 10 ? 10 : cp;
	}

	public int getHp() {
		int hp = (int) getInternalHp();
		return hp < 10 ? 10 : hp;
	}

	public StatRating getIvSumRating() {
		return StatRating.forIvSum(getIvSum());
	}

	public StatRating getBestIvRating() {
		return StatRating.forBestIv(getBestIv());
	}

	public Pokemon powerUp() {
		return new Pokemon(type, level.next(), ivStamina, ivAttack, ivDefense);
	}

	@Override
	public String toString() {
		return "{" + type + " level " + level + "; ivs: [" + ivAttack + "/" + ivDefense + "/" + ivStamina + "]; cp=" + getCp() + "; hp=" + getHp()
				+ "}";
	}

	public int getIvSum() {
		return ivStamina + ivAttack + ivDefense;
	}

	public int getBestIv() {
		return Math.max(ivStamina, Math.max(ivDefense, ivAttack));
	}

}
