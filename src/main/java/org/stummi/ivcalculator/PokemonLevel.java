package org.stummi.ivcalculator;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.Value;

@Value
public class PokemonLevel {
	static final double[] CP_MULTIPLIERS = { 0.094, 0.135137432, 0.16639787, 0.192650919, 0.21573247, 0.236572661, 0.25572005, 0.273530381,
			0.29024988, 0.306057377, 0.3210876, 0.335445036, 0.34921268, 0.362457751, 0.37523559, 0.387592406, 0.39956728, 0.411193551, 0.42250001,
			0.432926419, 0.44310755, 0.4530599578, 0.46279839, 0.472336083, 0.48168495, 0.4908558, 0.49985844, 0.508701765, 0.51739395, 0.525942511,
			0.53435433, 0.542635767, 0.55079269, 0.558830576, 0.56675452, 0.574569153, 0.58227891, 0.589887917, 0.59740001, 0.604818814, 0.61215729,
			0.619399365, 0.62656713, 0.633644533, 0.64065295, 0.647576426, 0.65443563, 0.661214806, 0.667934, 0.674577537, 0.68116492, 0.687680648,
			0.69414365, 0.700538673, 0.70688421, 0.713164996, 0.71939909, 0.725571552, 0.7317, 0.734741009, 0.73776948, 0.740785574, 0.74378943,
			0.746781211, 0.74976104, 0.752729087, 0.75568551, 0.758630378, 0.76156384, 0.764486065, 0.76739717, 0.770297266, 0.7731865, 0.776064962,
			0.77893275, 0.781790055, 0.78463697, 0.787473578, 0.79030001 };

	static final int[] STARDUST_COST = { 200, 400, 600, 800, 1000, 1300, 1600, 1900, 2200, 2500, 3000, 3500, 4000, 4500, 5000, 6000, 7000, 8000, 9000,
			10000 };

	final int halfLevels;

	private PokemonLevel(int halfLevels) {
		if (halfLevels < 0 || halfLevels >= CP_MULTIPLIERS.length) {
			throw new IllegalArgumentException("invalid half levels: " + halfLevels + "( = " + (1 + halfLevels / 2D) + ")");
		}
		this.halfLevels = halfLevels;
	}

	/**
	 * Creates a PokemonLevel instance from the "real" level which starts at 1
	 * and increases by .5 for every powerup
	 * 
	 * @param d
	 *            the level to use. Allowed are all from 1 to 40, which are
	 *            either full (decimal fraction *.0), or half (decimal fraction
	 *            *.5)
	 * 
	 * @throws IllegalArgumentException
	 *             if the supplied level is neither a full (.0) or half (.5)
	 *             level
	 */
	public static PokemonLevel fromRealLevel(double d) {
		if (d % .5 != 0) {
			throw new IllegalArgumentException("only full or half levels allowed");
		}

		return new PokemonLevel((int) ((d * 2) - 2));
	}

	/**
	 * Creates a PokemonLevel instance from the amount of half levels (or
	 * powerups) which is used internally. This means:
	 * 
	 * <table border=1>
	 * <tr>
	 * <th>"Half levels"</th>
	 * <th>represents "real level"</th>
	 * </tr>
	 * <tr>
	 * <td>0</td>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>1.5</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>2</td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td>2.5</td>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td>3</td>
	 * </tr>
	 * <tr>
	 * <td colspan="2">... and so on</td>
	 * </tr>
	 * </table>
	 * 
	 * @param hl
	 *            amount of half-levels to use
	 */
	public static PokemonLevel fromHalfLevels(int hl) {
		return new PokemonLevel(hl);
	}

	public int asHalfLevels() {
		return halfLevels;
	}

	public double asReallevel() {
		return 1 + halfLevels / 2D;
	}

	public double cpMultiplier() {
		return CP_MULTIPLIERS[halfLevels];
	}

	/**
	 * @return The amount of stardust cost needed to upgrade to the next level
	 *         from this one
	 */
	public int startdustCost() {
		return STARDUST_COST[halfLevels / 4];
	}

	public PokemonLevel next() {
		return new PokemonLevel(halfLevels + 1);
	}

	@Override
	public String toString() {
		if (halfLevels % 2 == 0) {
			return Integer.toString((int) asReallevel());
		} else {
			return Double.toString(asReallevel());
		}
	}

	public boolean isMax() {
		return halfLevels == CP_MULTIPLIERS.length - 1;
	}

	public static Stream<PokemonLevel> stream() {
		return IntStream.range(0, CP_MULTIPLIERS.length).mapToObj(PokemonLevel::new);
	}

}
