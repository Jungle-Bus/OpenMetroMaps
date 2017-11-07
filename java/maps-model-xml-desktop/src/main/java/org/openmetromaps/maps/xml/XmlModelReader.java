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

package org.openmetromaps.maps.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openmetromaps.maps.model.Coordinate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.topobyte.lightgeom.lina.Point;

public class XmlModelReader
{

	public static XmlModel read(InputStream is)
			throws ParserConfigurationException, SAXException, IOException
	{
		XmlModelReader reader = new XmlModelReader();
		return reader.readModel(is);
	}

	private List<XmlStation> xmlStations = new ArrayList<>();
	private List<XmlLine> xmlLines = new ArrayList<>();
	private List<XmlView> xmlViews = new ArrayList<>();

	private Map<String, XmlStation> nameToStation = new HashMap<>();

	private XmlModelReader()
	{
		// private constructor
	}

	private XmlModel readModel(InputStream is)
			throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);

		parseStations(doc);
		parseLines(doc);
		parseViews(doc);

		return new XmlModel(xmlStations, xmlLines, xmlViews);
	}

	private void parseStations(Document doc)
	{
		NodeList allStations = doc.getElementsByTagName("stations");
		Element firstStations = (Element) allStations.item(0);
		NodeList stationList = firstStations.getElementsByTagName("station");

		for (int i = 0; i < stationList.getLength(); i++) {
			Element eStation = (Element) stationList.item(i);

			NamedNodeMap attributes = eStation.getAttributes();
			String stationName = attributes.getNamedItem("name").getNodeValue();
			String valLon = attributes.getNamedItem("lon").getNodeValue();
			String valLat = attributes.getNamedItem("lat").getNodeValue();
			double lon = Double.parseDouble(valLon);
			double lat = Double.parseDouble(valLat);

			XmlStation station = new XmlStation(stationName,
					new Coordinate(lon, lat));
			xmlStations.add(station);
		}

		for (XmlStation station : xmlStations) {
			nameToStation.put(station.getName(), station);
		}
	}

	private void parseLines(Document doc)
	{
		NodeList allLines = doc.getElementsByTagName("lines");
		Element firstLines = (Element) allLines.item(0);
		NodeList lineList = firstLines.getElementsByTagName("line");

		for (int i = 0; i < lineList.getLength(); i++) {
			Element eLine = (Element) lineList.item(i);

			NamedNodeMap attributes = eLine.getAttributes();
			String lineName = attributes.getNamedItem("name").getNodeValue();
			String color = attributes.getNamedItem("color").getNodeValue();
			String circular = attributes.getNamedItem("circular")
					.getNodeValue();

			boolean isCircular = circular.equals("true");

			List<XmlStation> stops = new ArrayList<>();

			NodeList stopList = eLine.getElementsByTagName("stop");
			for (int k = 0; k < stopList.getLength(); k++) {
				Node station = stopList.item(k);
				attributes = station.getAttributes();
				String stationName = attributes.getNamedItem("station")
						.getNodeValue();
				XmlStation xmlStation = nameToStation.get(stationName);
				stops.add(xmlStation);
			}

			xmlLines.add(new XmlLine(lineName, color, isCircular, stops));
		}
	}

	private void parseViews(Document doc)
	{
		NodeList allViews = doc.getElementsByTagName("view");
		for (int i = 0; i < allViews.getLength(); i++) {
			Element eView = (Element) allViews.item(i);
			xmlViews.add(parseView(eView));
		}
	}

	private XmlView parseView(Element eView)
	{
		NamedNodeMap attributes = eView.getAttributes();
		String viewName = attributes.getNamedItem("name").getNodeValue();

		String valSceneWidth = attributes.getNamedItem("scene-width")
				.getNodeValue();
		String valSceneHeight = attributes.getNamedItem("scene-height")
				.getNodeValue();

		double sceneWidth = Double.parseDouble(valSceneWidth);
		double sceneHeight = Double.parseDouble(valSceneHeight);

		String valStartX = attributes.getNamedItem("start-x").getNodeValue();
		String valStartY = attributes.getNamedItem("start-y").getNodeValue();

		double startX = Double.parseDouble(valStartX);
		double startY = Double.parseDouble(valStartY);

		XmlView view = new XmlView(viewName, sceneWidth, sceneHeight, startX,
				startY);

		parseViewStations(view, eView);

		parseViewEdges(view, eView);

		return view;
	}

	private void parseViewStations(XmlView view, Element eView)
	{
		NodeList stationList = eView.getElementsByTagName("station");

		for (int i = 0; i < stationList.getLength(); i++) {
			Element eStation = (Element) stationList.item(i);
			XmlViewStation station = parseViewStation(eStation);
			view.getStations().add(station);
		}
	}

	private XmlViewStation parseViewStation(Element eStation)
	{
		NamedNodeMap attributes = eStation.getAttributes();
		String stationName = attributes.getNamedItem("name").getNodeValue();
		String valx = attributes.getNamedItem("x").getNodeValue();
		String valY = attributes.getNamedItem("y").getNodeValue();
		double x = Double.parseDouble(valx);
		double y = Double.parseDouble(valY);

		return new XmlViewStation(stationName, new Point(x, y));
	}

	private void parseViewEdges(XmlView view, Element eView)
	{
		NodeList edgesList = eView.getElementsByTagName("edges");

		for (int i = 0; i < edgesList.getLength(); i++) {
			Element eEdges = (Element) edgesList.item(i);
			XmlEdges edges = parseEdges(eEdges);
			view.getEdges().add(edges);
		}
	}

	private XmlEdges parseEdges(Element eEdges)
	{
		NamedNodeMap attributes = eEdges.getAttributes();
		String line = attributes.getNamedItem("line").getNodeValue();

		XmlEdges edges = new XmlEdges(line);

		NodeList intervalList = eEdges.getElementsByTagName("interval");
		for (int i = 0; i < intervalList.getLength(); i++) {
			Element eInterval = (Element) intervalList.item(i);
			parseInterval(edges, eInterval);
		}

		return edges;
	}

	private void parseInterval(XmlEdges edges, Element eInterval)
	{
		NamedNodeMap attributes = eInterval.getAttributes();
		String from = attributes.getNamedItem("from").getNodeValue();
		String to = attributes.getNamedItem("to").getNodeValue();
		edges.addInterval(new XmlInterval(from, to));
	}

}