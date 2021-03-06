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

package org.openmetromaps.maps;

import org.openmetromaps.maps.model.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.BBox;
import de.topobyte.formatting.Formatting;
import de.topobyte.geomath.WGS84;
import de.topobyte.lightgeom.lina.Point;

public class CoordinateConverter
{

	final static Logger logger = LoggerFactory
			.getLogger(CoordinateConverter.class);

	private double worldsize = 1;
	private double factor;
	private double minX;
	private double minY;

	private double width;
	private double height;

	private double margin;

	public CoordinateConverter(BBox bbox, double size, double margin)
	{
		this.margin = margin;

		double x1 = WGS84.lon2merc(bbox.getLon1(), worldsize);
		double x2 = WGS84.lon2merc(bbox.getLon2(), worldsize);

		double y1 = WGS84.lat2merc(bbox.getLat1(), worldsize);
		double y2 = WGS84.lat2merc(bbox.getLat2(), worldsize);

		double spanX = Math.abs(x1 - x2);
		double spanY = Math.abs(y1 - y2);

		double usedSize = size - margin * 2;

		double biggerSpan = Math.max(spanX, spanY);
		factor = usedSize / biggerSpan;

		logger.debug(Formatting.format("Size: %.2f", size));
		logger.debug(Formatting.format("Margin: %.2f", margin));
		logger.debug(Formatting.format("Used size: %.2f", usedSize));
		logger.debug(Formatting.format("Factor: %.2f", factor));

		minX = Math.min(x1, x2);
		minY = Math.min(y1, y2);

		logger.debug(
				Formatting.format("coordinates: %f,%f:%f,%f", x1, x2, y1, y2));
		logger.debug(Formatting.format("spanX: %f, spanY: %f", spanX, spanY));

		width = spanX * factor + margin * 2;
		height = spanY * factor + margin * 2;

		logger.debug(
				Formatting.format("width: %.2f, height: %.2f", width, height));
	}

	public double getWidth()
	{
		return width;
	}

	public double getHeight()
	{
		return height;
	}

	public Point convert(Coordinate coordinate)
	{
		double x = WGS84.lon2merc(coordinate.getLongitude(), worldsize);
		double y = WGS84.lat2merc(coordinate.getLatitude(), worldsize);
		double dx = x - minX;
		double dy = y - minY;
		double sx = dx * factor;
		double sy = dy * factor;
		return new Point(sx + margin, sy + margin);
	}

	public Point convert(Point point)
	{
		double x = WGS84.lon2merc(point.getX(), worldsize);
		double y = WGS84.lat2merc(point.getY(), worldsize);
		double dx = x - minX;
		double dy = y - minY;
		double sx = dx * factor;
		double sy = dy * factor;
		return new Point(sx + margin, sy + margin);
	}

}
