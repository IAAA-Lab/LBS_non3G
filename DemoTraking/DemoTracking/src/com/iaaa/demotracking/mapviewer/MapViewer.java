package com.iaaa.demotracking.mapviewer;

import java.util.ArrayList;
import java.util.Date;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.android.gsl_map_lib.FeatureTypeGraphicConf;
import com.android.gsl_map_lib.Map;
import com.android.gsl_map_lib.layer.Vector;
import com.iaaa.demotracking.Point;
import com.iaaa.demotracking.R;
import com.iaaa.location.LastLocation;

/**
 * Specific Application MapViewer
 * 
 * @author albertop
 */
public abstract class MapViewer extends GenericMapViewer {

	public static final String LAYER_NAME_POINTS_ALL = "pointsAll";

	private com.android.gsl_map_lib.Facade facade;
	public static final String RES_POS_TOP = "TOP";
	private Facade facadeApp;
	private ArrayList<String> mVectorLayerNames = new ArrayList<String>();

	@Override
	protected void onInit(Map map) {

		facade = new com.android.gsl_map_lib.Facade(map);

		facadeApp = new Facade(facade);

		facade.setOverrideFeatureStyles(false);
		facade.setFeatureSelectablePropertyName(getString(R.string.constant_wmsclient_geoJSONStringSelectable));

		// Get the screen's density scale
		// pixel = dpi * scale + 0.5f
		final float scale = getResources().getDisplayMetrics().density;

		// Set feature touch tolerance
		int touchingTolerance = (int) (new Integer(
				getString(R.string.config_wmsclient_featureSelectionTouchTolerance))
		* scale + 0.5f);
		facade.setTouchingTolerance(touchingTolerance);

		// Feature type property name
		facade.setFeatureTypePropertyName(getString(R.string.constant_wmsclient_geoJSONStringType));

		// Get style values
		// See res/values/attrs.xml for the <declare-styleable>
		TypedArray a = obtainStyledAttributes(R.styleable.Map);
		int geometryStrokeWidth = (int) (a.getInteger(
				R.styleable.Map_mapRouteGeometryStrokeWidth, 0) * scale + 0.5f);
		int geometryStrokeColor = a.getColor(
				R.styleable.Map_mapRouteGeometryStrokeColor, Color.BLACK);
		int geometryStrokeTransparency = a.getInteger(
				R.styleable.Map_mapRouteGeometryStrokeTransparency, 0);
		a.recycle();

		// Geometry style
		Paint geometryStyle = new Paint();
		geometryStyle.setStrokeWidth(geometryStrokeWidth);
		geometryStyle.setColor(geometryStrokeColor);
		geometryStyle.setAntiAlias(true);
		geometryStyle.setStrokeCap(Paint.Cap.BUTT);
		geometryStyle.setStyle(Paint.Style.STROKE);// Tipo línea
		geometryStyle.setAlpha(geometryStrokeTransparency); // Transparency

		// Route geometry type
		String typeGeometry = getString(R.string.constant_wmsclient_geoJSONStringTypeGeometry);
		FeatureTypeGraphicConf featureTypeGeometry = new FeatureTypeGraphicConf(
				typeGeometry);
		featureTypeGeometry.setStyle(geometryStyle);
		// If long lines are expected then setDrawGeometrySegments should be set
		// to true to avoid
		// the possibility of not being able to render them with drawPath and as
		// a result not being
		// able to see them on the map
		featureTypeGeometry.setDrawGeometrySegments(false);
		facade.addFeatureType(featureTypeGeometry);

		// Simple point feature type
		String typePoint = getString(R.string.constant_wmsclient_geoJSONStringTypePoint);
		FeatureTypeGraphicConf featureTypePoint = new FeatureTypeGraphicConf(
				typePoint, R.drawable.ic_point, R.drawable.ic_point_selected,
				RES_POS_TOP, RES_POS_TOP);
		// featureTypePoint.setMapTipContent(mapTipLayout, true);
		// featureTypePoint.setMapTipOffset(mapTipOffsetTop, 0, 0, 0);
		facade.addFeatureType(featureTypePoint);

		Vector vLayer;
		// All points layer
		vLayer = new Vector(LAYER_NAME_POINTS_ALL);
		map.addLayer(vLayer, false);

		// Route layer
		vLayer = new Vector("Layer_Route");
		map.addLayer(vLayer, false);

		mVectorLayerNames.add(LAYER_NAME_POINTS_ALL);
		mVectorLayerNames.add("Layer_Route");

		onInit();

		map.setMapLoading(false);
	}

	/**
	 * Return facade
	 * 
	 * @return
	 */
	public Facade getFacade() {
		return facadeApp;
	}

	/**
	 * Executed after generic initialization
	 */
	protected void onInit() {
		ArrayList<Point> points = new ArrayList<Point>();

		// Obtener las últimas localizaciones obetindas y trasnformalas a puntos

		ArrayList<Location> locations =
				LastLocation.getInstance().getLocations(); for(int i = 0; i <
						locations.size();i++){ points.add(new Point(i, new
								Date(locations.get(i).getTime()), locations.get(i).getLatitude(),
								locations.get(i).getLongitude()));

				}

				/*points.add(new Point(0, new Date(), 41.655517578125,
				-0.9054049849510193));
		points.add(new Point(0, new Date(), 41.655517578125,
				-0.8854049849510193));*/

				try {
					facadeApp.drawPointsAll(points, "EPSG:4326", this);

					facadeApp.drawRoute(this, points, "1", true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

}
