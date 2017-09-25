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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.openmetromaps.maps.PlanRenderer.SegmentMode;
import org.openmetromaps.maps.PlanRenderer.StationMode;
import org.openmetromaps.maps.actions.AboutAction;
import org.openmetromaps.maps.actions.DebugRanksAction;
import org.openmetromaps.maps.actions.DebugTangentsAction;
import org.openmetromaps.maps.actions.ExitAction;
import org.openmetromaps.maps.actions.LicenseAction;
import org.openmetromaps.maps.actions.ShowLabelsAction;
import org.openmetromaps.maps.model.ModelData;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.swing.util.EmptyIcon;
import de.topobyte.swing.util.action.enums.DefaultAppearance;
import de.topobyte.swing.util.action.enums.EnumActions;

public class MapViewer
{

	private ModelData model;

	private ViewConfig viewConfig;

	private JFrame frame;

	private ScrollableAdvancedPanel map;
	private StatusBar statusBar;

	public MapViewer(ModelData model)
	{
		this.model = model;

		viewConfig = ModelUtil.viewConfig(model);
	}

	public ModelData getModel()
	{
		return model;
	}

	public Window getFrame()
	{
		return frame;
	}

	public ScrollableAdvancedPanel getMap()
	{
		return map;
	}

	public StatusBar getStatusBar()
	{
		return statusBar;
	}

	public void show()
	{
		frame = new JFrame("Map Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);

		build();

		frame.setVisible(true);
	}

	private void build()
	{
		setupContent();
		setupMenu();

		map.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e)
			{
				statusBar.setText(
						String.format("Location: %d,%d", e.getX(), e.getY()));
			}

		});
	}

	private void setupMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);

		JMenu menuView = new JMenu("View");
		menuBar.add(menuView);

		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		menuFile.add(new ExitAction());

		addCheckbox(menuView, new ShowLabelsAction(this));
		JMenu stationMode = submenu("Station mode");
		JMenu segmentMode = submenu("Segment mode");
		menuView.add(stationMode);
		menuView.add(segmentMode);
		addCheckbox(menuView, new DebugTangentsAction(this));
		addCheckbox(menuView, new DebugRanksAction(this));

		PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

		EnumActions.add(stationMode, StationMode.class, changeSupport,
				"station-mode", StationMode.CONVEX, x -> setStationMode(x),
				new DefaultAppearance<>());
		EnumActions.add(segmentMode, SegmentMode.class, changeSupport,
				"segment-mode", SegmentMode.CURVE, x -> setSegmentMode(x),
				new DefaultAppearance<>());

		menuHelp.add(new AboutAction(frame));
		menuHelp.add(new LicenseAction(frame));
	}

	private void setStationMode(StationMode mode)
	{
		map.getPlanRenderer().setStationMode(mode);
		map.repaint();
	}

	private void setSegmentMode(SegmentMode mode)
	{
		map.getPlanRenderer().setSegmentMode(mode);
		map.repaint();
	}

	private JMenu submenu(String string)
	{
		JMenu menu = new JMenu(string);
		menu.setIcon(new EmptyIcon(24));
		return menu;
	}

	private void addCheckbox(JMenu menu, Action action)
	{
		menu.add(new JCheckBoxMenuItem(action));
	}

	private void setupContent()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		frame.setContentPane(panel);

		map = new ScrollableAdvancedPanel(model,
				PlanRenderer.StationMode.CONVEX, PlanRenderer.SegmentMode.CURVE,
				viewConfig.getStartPosition(), 10, 15, viewConfig.getBbox());

		statusBar = new StatusBar();

		GridBagConstraintsEditor c = new GridBagConstraintsEditor();
		c.weight(1, 1).fill(GridBagConstraints.BOTH);
		panel.add(map, c.getConstraints());
		c.weight(1, 0).fill(GridBagConstraints.HORIZONTAL);
		c.gridPos(0, 1);
		panel.add(statusBar, c.getConstraints());
	}

}
