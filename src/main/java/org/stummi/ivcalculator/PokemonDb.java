package org.stummi.ivcalculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PokemonDb {
	private static final String DEFAULT_RESOURCE_NAME = "pokemon/base-data.tsv";

	private final List<PokemonType> list;

	public PokemonDb(List<PokemonType> mons) {
		this.list = mons;
	}

	public PokemonType typeById(int id) {
		return list.stream().filter(m -> m.getPokeId() == id).findFirst().orElse(null);
	}

	public static PokemonDb loadDefault() throws IOException {
		return loadFromTsvResource(DEFAULT_RESOURCE_NAME);
	}

	public static PokemonDb loadFromTsvFile(String file) throws IOException {
		try (Stream<String> mons = Files.lines(Paths.get(file)).skip(1)) {
			return fromTsvLines(mons);
		}
	}

	public static PokemonDb loadFromTsvResource(String resourceName) throws IOException {
		try (InputStream res = PokemonDb.class.getClassLoader().getResourceAsStream(resourceName)) {
			if (res == null) {
				throw new IOException("resource not found: " + resourceName);
			}
			return fromTsvLines(new BufferedReader(new InputStreamReader(res, StandardCharsets.UTF_8)).lines().skip(1));
		}
	}

	public static PokemonDb fromTsvLines(Stream<String> lines) {
		return new PokemonDb(lines.map(str -> {
			String[] spl = str.split("\t", 5);
			int pokeId = Integer.parseInt(spl[0]);
			int stam = Integer.parseInt(spl[1]);
			int att = Integer.parseInt(spl[2]);
			int def = Integer.parseInt(spl[3]);
			String name = spl[4];
			return new PokemonType(pokeId, stam, att, def, name);
		}).collect(Collectors.toList()));
	}

	public PokemonType typeByExactName(String name) {
		return list.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findAny().orElse(null);
	}
	
	public List<PokemonType> typesByPartName(String name) {
		String lcName =name.toLowerCase();
		
		return list.stream().filter(p -> p.getName().toLowerCase().indexOf(lcName) >= 0).collect(Collectors.toList());
		
	}

}
