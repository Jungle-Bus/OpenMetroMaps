// Copyright 2017 Sebastian Kuerten
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

package org.openmetromaps.misc;

import java.io.IOException;
import java.nio.file.Path;

import org.openmetromaps.maps.xml.XmlLine;
import org.openmetromaps.maps.xml.XmlStation;

public class CircularLineWriter
{

	private Context context;
	private Path file;
	private XmlLine line;

	public CircularLineWriter(Context context, Path file, XmlLine line)
	{
		this.context = context;
		this.file = file;
		this.line = line;
	}

	public void write() throws IOException
	{
		MarkdownWriter output = new MarkdownWriter(file);

		for (XmlStation station : line.getStops()) {
			output.unordered(station.getName());
		}

		output.close();
	}

}
