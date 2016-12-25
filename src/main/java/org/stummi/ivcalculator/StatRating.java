package org.stummi.ivcalculator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StatRating {
	BEST(37, 15),
	GOOD(30, 13),
	BAD(23, 8),
	WORST(0, 0);

	final int minIvSum;
	final int minBestIv;

	public static StatRating forBestIv(int bestIv) {
		for (StatRating r : values()) {
			if (bestIv >= r.minBestIv) {
				return r;
			}
		}

		throw new IllegalArgumentException("bestIv: " + bestIv);
	}

	public static StatRating forIvSum(int ivSum) {
		for (StatRating r : values()) {
			if (ivSum >= r.minIvSum) {
				return r;
			}
		}

		throw new IllegalArgumentException("ivSum: " + ivSum);
	}
}
