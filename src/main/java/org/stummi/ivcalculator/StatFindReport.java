package org.stummi.ivcalculator;

import java.io.PrintStream;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class StatFindReport {
	@Value
	static class VisibleStats {
		final int cp;
		final int hp;
		final int stardust;

		static VisibleStats fromPokemon(Pokemon mon) {
			return new VisibleStats(mon.getCp(), mon.getHp(), mon.getLevel().startdustCost());
		}

		@Override
		public String toString() {
			return "[cp=" + cp + "; hp=" + hp + "; stardust=" + stardust + "]";
		}
	}

	private final List<Pokemon> pokemon;
	private final PrintStream out;

	public void print() {
		if (pokemon.isEmpty()) {
			out.println(" --- Empty :( ---");
			return;
		}
		
		PokemonType type = pokemon.get(0).getType();
		System.out.println("Results for your " + type + ": ");
		System.out.println();

		IntSummaryStatistics perf = pokemon.stream().mapToDouble(Pokemon::perfection).mapToInt(d -> (int) (d * 100)).summaryStatistics();
		IntSummaryStatistics att = pokemon.stream().mapToInt(Pokemon::getIvAttack).summaryStatistics();
		IntSummaryStatistics def = pokemon.stream().mapToInt(Pokemon::getIvDefense).summaryStatistics();
		IntSummaryStatistics sta = pokemon.stream().mapToInt(Pokemon::getIvStamina).summaryStatistics();

		out.println("== Stats ranges ==");
		printRange("attack", att);
		printRange("defense", def);
		printRange("stamina", sta);
		printRange("perfection", perf);

		out.println();
		out.println("== " + pokemon.size() + " possible combinations ==");
		out.printf("%8s|%8s|%8s|%8s|%8s%n", "level", "attack", "defense", "stamina", "perfect");
		out.println("--------------------------------------------");
		int row = 0;
		for (Pokemon p : pokemon) {
			out.printf("%8.1f|%8d|%8d|%8d|%8.0f%n", p.getLevel().asReallevel(), p.getIvAttack(), p.getIvDefense(), p.getIvStamina(),
					p.perfection() * 100);
			if (row++ == 15) {
				out.println("      (... and more - please filter further)");
				break;
			}
		}

		System.out.println();
		System.out.println("== Upgrade path ==");
		int worstCase = determineUpgradePath(pokemon, 0);
		out.println();
		System.out.println("worst case upgrade count: " + worstCase);

	}

	private void printRange(String string, IntSummaryStatistics perf) {
		int max = perf.getMax();
		int min = perf.getMin();
		if (min == max) {
			out.printf("%12s: %3d%n", string, max);
		} else {
			out.printf("%12s: %3d - %3d%n", string, min, max);
		}
	}

	private int determineUpgradePath(List<Pokemon> mons, int indentation) {
		StringBuilder prefixBuilder = new StringBuilder();
		for (int idx = 0; idx < indentation; ++idx) {
			prefixBuilder.append("  ");
		}
		String prefix = prefixBuilder.toString();

		Map<VisibleStats, List<Pokemon>> map = mons.stream().collect(Collectors.groupingBy(VisibleStats::fromPokemon, Collectors.toList()));

		if(indentation == 0 && map.size() > 1) {
			out.println("(Got ambigous visible stats at top level. Please provide at least CP, HP and Stardust)");
		}
		int maxIndentation = indentation;

		for (Entry<VisibleStats, List<Pokemon>> entry : map.entrySet()) {
			VisibleStats stats = entry.getKey();
			List<Pokemon> list = entry.getValue();
			out.print(prefix + list.size() + " x " + stats);
			if (list.size() > 1) {
				out.println();
				List<Pokemon> newList = list.stream().filter(p -> !p.getLevel().isMax()).map(Pokemon::powerUp).collect(Collectors.toList());
				int maxed = list.size() - newList.size();
				if (maxed > 0) {
					out.println(prefix + "(maxed out: " + maxed + ")");
				}
				maxIndentation = Math.max(determineUpgradePath(newList, indentation + 1), maxIndentation);
			} else {
				Pokemon singleResult = list.get(0);
				out.printf(" => %d/%d/%d - %.0f%%%n", singleResult.getIvAttack(), singleResult.getIvDefense(), singleResult.getIvStamina(),
						singleResult.perfection() * 100);

			}
		}

		return maxIndentation;
	}
}
