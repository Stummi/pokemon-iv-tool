package org.stummi.ivcalculator;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CliTool {
	final PrintStream out;
	private PokemonDb db;
	private PokemonStatFinder finder;

	private static final Map<String, BiConsumer<CliTool, String>> ARGUMENT_HANDLERS = new HashMap<>();

	static {
		ARGUMENT_HANDLERS.put("cp", CliTool::cp);
		ARGUMENT_HANDLERS.put("hp", CliTool::hp);
		ARGUMENT_HANDLERS.put("stardust", CliTool::stardust);
		ARGUMENT_HANDLERS.put("ivsumrating", CliTool::ivsumrating);
		ARGUMENT_HANDLERS.put("bestivrating", CliTool::bestivrating);
		ARGUMENT_HANDLERS.put("beststat", CliTool::beststat);
	}

	public static void main(String[] args) throws IOException {
		try {
			new CliTool(System.out).run(args);
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());

			e.printStackTrace();
		}
	}

	public void run(String[] args) throws IOException {
		if (args.length == 0) {
			usage();
			return;
		}

		this.db = PokemonDb.loadDefault();
		PokemonType type = selectPokemon(args[0]);
		this.finder = type.createStatFinder();

		Stream.of(args).skip(1).forEach(this::handleArgument);
		List<Pokemon> pokelist = finder.findAllPossibleStats().collect(Collectors.toList());
		new StatFindReport(pokelist, out).print();
	}

	private PokemonType selectPokemon(String string) {
		PokemonType type;
		try {
			Integer id = Integer.parseInt(string);
			type = db.typeById(id);
			if (type == null) {
				throw new IllegalArgumentException("not a known pokemon id: " + string);
			} else {
				return type;
			}
		} catch (NumberFormatException nfe) {
			// argument was not a number, continue with looking up by name
		}

		type = db.typeByExactName(string);
		if (type != null) {
			return type;
		}
		List<PokemonType> types = db.typesByPartName(string);
		if (types.size() == 0) {
			throw new IllegalArgumentException("not a known pokemon name: " + string);
		} else if (types.size() > 1) {
			throw new IllegalArgumentException("ambigous pokemon name: " + string + ", candidates are: "
					+ types.stream().map(PokemonType::getName).collect(Collectors.joining(", ")));
		} else {
			return types.get(0);
		}
	}

	public void handleArgument(String argument) {
		String[] spl = argument.split("=");
		if (spl.length < 2) {
			throw new IllegalArgumentException("argument expected: " + argument);
		}

		BiConsumer<CliTool, String> func = ARGUMENT_HANDLERS.get(spl[0].toLowerCase());
		if (func == null) {
			throw new IllegalArgumentException("unknown argument: " + spl[0]);
		}

		func.accept(this, spl[1]);
	}

	public void cp(String cp) {
		finder.hasCp(Integer.parseInt(cp));
	}

	public void hp(String hp) {
		finder.hasHp(Integer.parseInt(hp));
	}

	public void stardust(String stardust) {
		finder.hasStardustCost(Integer.parseInt(stardust));
	}

	public void ivsumrating(String ivsumrating) {
		finder.hasIvSumRating(parseRating(ivsumrating));
	}

	public void bestivrating(String bestivrating) {
		finder.hasBestIvRating(parseRating(bestivrating));
	}

	private StatRating parseRating(String rating) {
		try {
			return StatRating.values()[Integer.parseInt(rating)];
		} catch (NumberFormatException nfe) {
			return StatRating.valueOf(rating.toUpperCase());
		}
	}

	public void beststat(String beststat) {
		for (String s : beststat.split(",")) {
			switch (Character.toUpperCase(s.charAt(0))) {
			case 'A':
				finder.attackIsBest();
				break;
			case 'D':
				finder.defenseIsBest();
				break;
			case 'S':
				finder.staminaIsBest();
				break;
			default:
				throw new IllegalArgumentException("not a valid stat: " + s);
			}
		}
	}

	public void usage() {
		out.println("Usage: iv-tool <pokemon-Id> [cp=<val>] [hp=<val>] "
				+ "[stardust=<val>] [ivsumrating=<0..3|WORST|BAD|GOOD|BEST>] " + "[beststat=<A,D,S>]"
				+ "[bestivrating=<0..3|WORST|BAD|GOOD|BEST>]");
	}
}
