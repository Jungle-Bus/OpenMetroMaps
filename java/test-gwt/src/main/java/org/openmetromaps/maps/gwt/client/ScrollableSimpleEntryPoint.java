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

package org.openmetromaps.maps.gwt.client;

import java.util.Map;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.gwt.BaseMapWindowPanel;
import org.openmetromaps.maps.gwt.ScrollableSimplePlanPanel;
import org.openmetromaps.maps.gwt.Util;

public class ScrollableSimpleEntryPoint extends ScrollableEntryPoint
{

	private ScrollableSimplePlanPanel panel;

	@Override
	protected BaseMapWindowPanel createPanel()
	{
		panel = new ScrollableSimplePlanPanel();
		return panel;
	}

	@Override
	protected void setParameters(Map<String, String> params)
	{
		panel.setDebugSize(Util.getBoolean(params, "debug-size", false));
	}

	@Override
	protected void setModel(MapModel mapModel)
	{
		panel.setModel(mapModel);
	}

}
