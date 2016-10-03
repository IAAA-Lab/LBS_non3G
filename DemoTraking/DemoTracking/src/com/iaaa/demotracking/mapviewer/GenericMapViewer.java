package com.iaaa.demotracking.mapviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.android.gsl_map_lib.ActionListener;
import com.android.gsl_map_lib.Event;
import com.android.gsl_map_lib.Extent;
import com.android.gsl_map_lib.Layer;
import com.android.gsl_map_lib.Map;
import com.android.gsl_map_lib.TileSet;
import com.android.gsl_map_lib.control.DragPan;
import com.android.gsl_map_lib.control.GestureControl;
import com.android.gsl_map_lib.control.LoadingMap;
import com.android.gsl_map_lib.control.ZoomIn;
import com.android.gsl_map_lib.control.ZoomOut;
import com.android.gsl_map_lib.layer.WMS;
import com.android.gsl_map_lib.layer.WMSC;
import com.android.gsl_map_lib.tool.Button;
import com.google.android.maps.MapActivity;
import com.iaaa.demotracking.R;

/**
 * Generic MapViewer Activity.
 * 
 * @author albertop
 */
public abstract class GenericMapViewer extends MapActivity {

	private Map map = null;
	private final ArrayList<View> mapFrontViews = new ArrayList<View>();

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		// Now we can ask for layout width and height
		if (map == null) {
			ViewGroup mapViewerContainer = (ViewGroup) findViewById(R.id.mapviewer_container_layout);
			init(mapViewerContainer.getWidth(), mapViewerContainer.getHeight());
		}
	}

	private Float parseExtentValue(Properties properties, String propertyName, String propertiesName) {
		String value = "";
		try {
			value = properties.getProperty(propertyName);
			return Float.parseFloat(value.trim());
		}
		catch (Exception e) {
			Log.e(this.getClass().getName(), propertiesName + ".properties - Error parsing " + propertyName + ": "
					+ value, e);
			return null;
		}
	}

	private Float parseInitExtentValue(Properties properties, int nameResourceId) {
		return parseExtentValue(properties, getString(nameResourceId), "map");
	}

	private Float parseTileSetExtentValue(Properties properties, int nameResourceId, int i, int j) {
		return parseExtentValue(properties, getString(nameResourceId) + "_" + i + "_" + j, "wms");
	}

	private Integer parseTileIntegerValue(Properties properties, int nameResourceId, int i, int j) {
		Float value = parseExtentValue(properties, getString(nameResourceId) + "_" + i + "_" + j, "wms");
		return value != null ? value.intValue() : null;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@SuppressWarnings("deprecation")
	private void init(int width, int height) {

		map = new com.android.gsl_map_lib.Map(this, width, height);

		ViewGroup mapViewerContainer = (ViewGroup) findViewById(R.id.mapviewer_container_layout);
		mapViewerContainer.addView(map.getMainView(), new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		AssetManager assetManager = getAssets();

		// Parse wms.properties
		Properties wmsProperties = new Properties();
		try {
			wmsProperties.load(assetManager.open(getString(R.string.constant_wmsclient_wmsPropertiesPath)));
			ArrayList<Layer> wmsList = new ArrayList<Layer>();
			int i = 0;
			do {
				String mapServerURL = wmsProperties
						.getProperty(getString(R.string.constant_wmsclient_propertyName_mapServerName) + "_" + i);
				if (mapServerURL == null) {
					if (i == 0)
						Log.e(this.getClass().getName(), "wms.properties - No map servers found");
					break;
				}

				String mapServerName = wmsProperties
						.getProperty(getString(R.string.constant_wmsclient_propertyName_mapServerTitle) + "_" + i);
				String mapServerVersion = wmsProperties
						.getProperty(getString(R.string.constant_wmsclient_propertyName_mapServerVersion) + "_" + i);
				String serviceType = wmsProperties
						.getProperty(getString(R.string.constant_wmsclient_propertyName_mapServerServiceType) + "_" + i);

				// Layers
				String layerNames = "";
				String layerStyles = "";
				int j = 0;
				do {
					String layerName = wmsProperties
							.getProperty(getString(R.string.constant_wmsclient_propertyName_layerName) + "_" + i + "_"
									+ j);
					String layerStyleName = wmsProperties
							.getProperty(getString(R.string.constant_wmsclient_propertyName_layerStyleName) + "_" + i
									+ "_" + j);
					if (layerName == null) {
						if (j == 0)
							Log.e(this.getClass().getName(), "wms.properties - Map Server " + i + " - No layers found");
						break;
					}
					if (layerName != null && layerName.length() > 0 && layerStyleName != null
							&& layerStyleName.length() > 0) {
						layerNames += (layerNames.length() > 0 ? "," : "") + layerName;
						layerStyles += (layerStyles.length() > 0 ? "," : "") + layerStyleName;
					}
					j++;
				} while (true);

				WMS wms = null;
				if (getString(R.string.constant_wmsclient_serviceType_WMSC).equals(serviceType)) {
					int tileAccesType = new Integer(
							wmsProperties.getProperty(getString(R.string.constant_wmsclient_tileAccessType) + "_" + i));
					wms = new WMSC(mapServerName, mapServerURL, false, tileAccesType);
					String path = wmsProperties.getProperty(getString(R.string.constant_wmsclient_tileLocalCachePath)
							+ "_" + i);
					((WMSC) wms).setPath(path);
					wms.setDefaultTileImage(BitmapFactory.decodeResource(getResources(), R.drawable.default_tile));
					// Tiles
					j = 0;
					do {
						if (wmsProperties.getProperty(getString(R.string.constant_wmsclient_propertyName_tileSetMinX)
								+ "_" + i + "_" + j) == null) {
							if (j == 0)
								Log.e(this.getClass().getName(), "wms.properties - No tiles found");
							break;
						}
						// Extent
						Float tileSetMinX = parseTileSetExtentValue(wmsProperties,
								R.string.constant_wmsclient_propertyName_tileSetMinX, i, j);
						Float tileSetMinY = parseTileSetExtentValue(wmsProperties,
								R.string.constant_wmsclient_propertyName_tileSetMinY, i, j);
						Float tileSetMaxX = parseTileSetExtentValue(wmsProperties,
								R.string.constant_wmsclient_propertyName_tileSetMaxX, i, j);
						Float tileSetMaxY = parseTileSetExtentValue(wmsProperties,
								R.string.constant_wmsclient_propertyName_tileSetMaxY, i, j);
						String tileSetSRS = wmsProperties
								.getProperty(getString(R.string.constant_wmsclient_propertyName_tileSetSRS) + "_" + i
										+ "_" + j);

						if (tileSetSRS == null || tileSetMinX == null || tileSetMinY == null || tileSetMaxX == null
								|| tileSetMaxY == null) {
							break;
						}

						// Resolution
						String tileSetResolutionsString = wmsProperties
								.getProperty(getString(R.string.constant_wmsclient_propertyName_tileSetResolutions)
										+ "_" + i + "_" + j);
						String[] resolutionStringList = {};
						if (tileSetResolutionsString != null && tileSetResolutionsString.trim().length() > 0) {
							resolutionStringList = tileSetResolutionsString.split(" ");
						}
						else {
							Log.e(this.getClass().getName(), "wms.properties - Map Server " + i + " - TileSet " + j
									+ " - No resolutions found");
							break;
						}
						ArrayList<Double> resolutionList = new ArrayList<Double>();
						int k = 0;
						try {
							for (k = 0; k < resolutionStringList.length; k++)
								resolutionList.add(Double.valueOf(resolutionStringList[k]));
						}
						catch (NumberFormatException e) {
							Log.e(this.getClass().getName(), "wms.properties - Map Server " + i + " - Tileset " + j
									+ " - Error parsing resolution " + k + ": " + resolutionStringList[k], e);
							break;
						}

						// Width and Height
						Integer tileWidth = parseTileIntegerValue(wmsProperties,
								R.string.constant_wmsclient_propertyName_tileSetTileWidth, i, j);
						Integer tileHeight = parseTileIntegerValue(wmsProperties,
								R.string.constant_wmsclient_propertyName_tileSetTileHeight, i, j);

						if (tileWidth == null || tileHeight == null) {
							break;
						}

						String tileSetFormat = wmsProperties
								.getProperty(getString(R.string.constant_wmsclient_propertyName_tileSetFormat) + "_"
										+ i + "_" + j);
						if (tileSetFormat == null || tileSetFormat.trim().length() == 0) {
							Log.e(this.getClass().getName(), "wms.properties - Map Server " + i + " - TileSet " + j
									+ " - No tileSet format found");
							break;
						}

						TileSet tileset = new TileSet(tileSetSRS, new Extent(tileSetMinX, tileSetMinY, tileSetMaxX,
								tileSetMaxY, tileSetSRS), resolutionList, tileSetFormat, tileWidth, tileHeight);
						((WMSC) wms).addTileSet(tileset);
						// Add SRS projection to map
						map.addProjection(tileSetSRS);
						map.setProjectionFromList(tileSetSRS);

						j++;
					} while (true);
				}
				else {
					wms = new WMS(mapServerName, mapServerURL);
				}
				if (wms != null) {
					wms.setVersion(mapServerVersion);
					wms.setBuffer(0);
					wms.setLayers(layerNames, layerStyles);
					wmsList.add(wms);
				}

				i++;
			} while (true);

			if (!wmsList.isEmpty())
				map.addLayers(wmsList);
		}
		catch (IOException e) {
			Log.e(this.getClass().getName(), "Error loading wms.properties", e);
		}

		// Parse map.properties
		Properties mapProperties = new Properties();
		Float maxMinX = null;
		Float maxMinY = null;
		Float maxMaxX = null;
		Float maxMaxY = null;
		Float initMinX = null;
		Float initMinY = null;
		Float initMaxX = null;
		Float initMaxY = null;
		String initSRS = null;
		Float initZoom = null;
		try {
			mapProperties.load(assetManager.open(getString(R.string.constant_wmsclient_mapPropertiesPath)));

			maxMinX = parseInitExtentValue(mapProperties, R.string.constant_wmsclient_propertyName_maxMinX);
			maxMinY = parseInitExtentValue(mapProperties, R.string.constant_wmsclient_propertyName_maxMinY);
			maxMaxX = parseInitExtentValue(mapProperties, R.string.constant_wmsclient_propertyName_maxMaxX);
			maxMaxY = parseInitExtentValue(mapProperties, R.string.constant_wmsclient_propertyName_maxMaxY);
			initMinX = parseInitExtentValue(mapProperties, R.string.constant_wmsclient_propertyName_initMinX);
			initMinY = parseInitExtentValue(mapProperties, R.string.constant_wmsclient_propertyName_initMinY);
			initMaxX = parseInitExtentValue(mapProperties, R.string.constant_wmsclient_propertyName_initMaxX);
			initMaxY = parseInitExtentValue(mapProperties, R.string.constant_wmsclient_propertyName_initMaxY);
			initSRS = mapProperties.getProperty(getString(R.string.constant_wmsclient_propertyName_initSRS));
			initZoom = parseInitExtentValue(mapProperties, R.string.constant_wmsclient_propertyName_initZoom);
		}
		catch (IOException e) {
			Log.e(this.getClass().getName(), "Error loading map.properties", e);
		}

		// Set max extent
		if (initSRS != null && maxMinX != null && maxMinY != null && maxMaxX != null && maxMaxY != null) {
			map.setMaxExtent(new Extent(maxMinX, maxMinY, maxMaxX, maxMaxY, initSRS));
		}
		// Set extent
		if (initSRS != null && initMinX != null && initMinY != null && initMaxX != null && initMaxY != null) {
			map.setExtent(new Extent(initMinX, initMinY, initMaxX, initMaxY, initSRS));
			if (initZoom != null) {
				map.zoomTo(initZoom.intValue());
			}
		}

		boolean gestureControlAdded = false;

		try {
			GestureControl gestureControl;
			PackageManager pm = this.map.getContext().getPackageManager();
			if (pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
				gestureControl = new GestureControl(true);
				// Minimun displacement to be considered a panning action
				gestureControl.setMinDisplacement(10);
				gestureControl.setDisplacementPrecision(5);
				gestureControl.setMinDiferenceBetweenPinchMoves(20f);
				this.map.addControl(gestureControl);
				gestureControlAdded = true;
			}
			else {
				// DragPan initially selected and hide vector layer before do the panning
				DragPan panning = new DragPan(true);
				// Minimun displacement to be considered a panning action
				panning.setMinDisplacement(10);

				map.addControl(panning);
			}
		}
		catch (Error e) {
			// Si ha habido algun error crea el interfaz normal

			// DragPan initially selected and hide vector layer before do the panning
			DragPan panning = new DragPan(true);
			// Minimun displacement to be considered a panning action
			panning.setMinDisplacement(10);
			map.addControl(panning);

		}

		if (!gestureControlAdded || (getString(R.string.config_wmsclient_addzoombuttons).compareTo("1") == 0)) {
			addZoomButtons();
		}

		onInit(map);
	}

	private void addZoomButtons() {
		if (map != null) {
			ZoomIn zoomIn = new ZoomIn();
			ZoomOut zoomOut = new ZoomOut();

			map.addControl(zoomIn);
			map.addControl(zoomOut);

			// Create control tools

			// Zoom buttons

			Bitmap zoomOutBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_zoom_down_normal);
			int gapMarginZoomButtons = (int) getResources().getDimension(R.dimen.zoomButtonsGapMargin);
			int bottomMarginZoomButtons = (int) getResources().getDimension(R.dimen.zoomButtonsBottomMargin);
			int rightMarginZoomButtons = (int) getResources().getDimension(R.dimen.zoomButtonsRightMargin);

			// Zoom In button
			RelativeLayout.LayoutParams layoutParamsZoomInBtn = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			layoutParamsZoomInBtn.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParamsZoomInBtn.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			layoutParamsZoomInBtn.rightMargin = rightMarginZoomButtons;
			layoutParamsZoomInBtn.bottomMargin = bottomMarginZoomButtons + zoomOutBitmap.getHeight()
					+ gapMarginZoomButtons;
			final Button zoomInBtn = new Button(R.drawable.btn_zoom_up_normal, R.drawable.btn_zoom_up_pressed, zoomIn,
					layoutParamsZoomInBtn);
			map.addTool(zoomInBtn);

			// Zoom Out button
			RelativeLayout.LayoutParams layoutParamsZoomOutBtn = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			layoutParamsZoomOutBtn.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParamsZoomOutBtn.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			layoutParamsZoomOutBtn.rightMargin = rightMarginZoomButtons;
			layoutParamsZoomOutBtn.bottomMargin = bottomMarginZoomButtons;
			final Button zoomOutBtn = new Button(R.drawable.btn_zoom_down_normal, R.drawable.btn_zoom_down_pressed,
					zoomOut, layoutParamsZoomOutBtn);
			map.addTool(zoomOutBtn);

			// Ensure images are always drawn on top
			mapFrontViews.add(zoomOutBtn.getImageView());
			mapFrontViews.add(zoomInBtn.getImageView());
			final String addOverlayedElementEvent = getString(R.string.constant_wmsclient_eventAddOverlayedElement);
			final String changeViewEvent = getString(R.string.constant_wmsclient_eventChangeView);
			ActionListener listener = new ActionListener() {
				public boolean actionPerformed(Event e) {
					if ((e.getType().equals(addOverlayedElementEvent)) || (e.getType().equals(changeViewEvent))) {
						for (View view : mapFrontViews) {
							view.bringToFront();
						}
					}
					return false;
				}
			};
			map.getEvents().register(listener, addOverlayedElementEvent);
			map.getEvents().register(listener, changeViewEvent);
		}
	}

	protected void addFrontView(View view) {
		mapFrontViews.add(view);
	}

	/**
	 * Executed after generic initialization
	 * 
	 * @param map
	 */
	protected void onInit(Map map) {
	}
	
	/**
	 * Destroy the map object and free the memory allocated 
	 */
	  
	protected void onDestroy() {
		if (map != null) {
			map.destroy();
		}
		
		map = null;
				
		super.onDestroy();
	}
}