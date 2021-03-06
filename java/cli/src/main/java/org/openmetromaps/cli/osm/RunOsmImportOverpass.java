// Copyright 2018 Sebastian Kuerten
//
// This file is part of OpenMetroMaps.
//
// OpenMetroMaps is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// OpenMetroMaps is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with OpenMetroMaps. If not, see <http://www.gnu.org/licenses/>.

package org.openmetromaps.cli.osm;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openmetromaps.imports.config.ImportConfig;
import org.openmetromaps.imports.config.Processing;
import org.openmetromaps.imports.config.osm.OsmSource;
import org.openmetromaps.imports.config.reader.DesktopImportConfigReader;
import org.openmetromaps.maps.model.ModelData;
import org.openmetromaps.maps.xml.XmlModelWriter;
import org.openmetromaps.model.osm.Fix;
import org.openmetromaps.model.osm.filter.RouteFilter;
import org.openmetromaps.osm.OverpassApiImporter;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunOsmImportOverpass
{

	private static final String OPTION_CONFIG = "config";
	private static final String OPTION_OUTPUT = "output";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_CONFIG, true, true, "file", "an importer configuration file");
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "a target model text file");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		CommandLine line = arguments.getLine();

		String argConfig = line.getOptionValue(OPTION_CONFIG);
		String argOutput = line.getOptionValue(OPTION_OUTPUT);
		Path pathConfig = Paths.get(argConfig);
		Path pathOutput = Paths.get(argOutput);

		System.out.println("Config: " + pathConfig);
		System.out.println("Output: " + pathOutput);

		InputStream isConfig = Files.newInputStream(pathConfig);
		ImportConfig config = DesktopImportConfigReader.read(isConfig);
		isConfig.close();

		if (!(config.getSource() instanceof OsmSource)) {
			System.out.println("Config is not an OSM configuration");
			return;
		}

		final OsmSource source = (OsmSource) config.getSource();
		Processing processing = config.getProcessing();

		RouteFilter routeFilter = new OsmSourceRouteFilter(source);

		List<Fix> fixes = new ArrayList<>();

		String query = OverpassQueryBuilder.build(source);
		System.out.println("Overpass API query:");
		System.out.println(query);

		OverpassApiImporter overpassApiImporter = new OverpassApiImporter();
		ModelData data = overpassApiImporter.execute(query, routeFilter,
				processing.getPrefixes(), processing.getSuffixes(), fixes);

		OutputStream os = Files.newOutputStream(pathOutput);
		new XmlModelWriter().write(os, data, new ArrayList<>());
		os.close();
	}

}
