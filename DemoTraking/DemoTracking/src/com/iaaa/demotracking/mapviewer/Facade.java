package com.iaaa.demotracking.mapviewer;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.geo.GeoJSONFeature;
import org.json.geo.GeoJSONFeatureCollection;

import android.content.Context;

import com.android.gsl_map_lib.Coordinates;
import com.iaaa.demotracking.Point;
import com.iaaa.demotracking.R;

/**
 * MapViewer app instance specific facade
 * 
 * @author albertop
 */
/**
 * @author albertop
 */
public class Facade {

	private com.android.gsl_map_lib.Facade facade;

	/**
	 * @param facade
	 */
	public Facade(com.android.gsl_map_lib.Facade facade) {
		this.facade = facade;
	}

	/**
	 * Center map in position
	 * 
	 * @param x
	 * @param y
	 */
	public void centerMap(double x, double y, String projection) {
		centerMap(x, y, projection, -1);
	}

	/**
	 * Center map in position
	 * 
	 * @param location
	 */
	public void centerMap(android.location.Location location) {
		centerMap(location, -1);
	}

	/**
	 * Center map in position
	 * 
	 * @param x
	 * @param y
	 * @param zoomLevel
	 */
	public void centerMap(double x, double y, String projection, int zoomLevel) {
		if (zoomLevel > -1) {
			facade.getMap().moveTo(new Coordinates(x, y, projection), zoomLevel);
		}
		else {
			facade.getMap().moveTo(new Coordinates(x, y, projection));
		}
	}

	/**
	 * Center map in position
	 * 
	 * @param location
	 * @param zoomLevel
	 */
	public void centerMap(android.location.Location location, int zoomLevel) {
		centerMap(location.getLongitude(), location.getLatitude(), "EPSG:4326", zoomLevel);
	}

	/**
	 * Returns true if location is inside map's maximum extent, false otherwise
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isLocationInMaxExtent(double x, double y) {
		if((facade != null) && (facade.getMap() != null)) {
			return facade.isLocationInExtent(new Coordinates(x, y), facade.getMap().getMaxExtent());
		} else {
			return false;
		}
	}

	/**
	 * Returns true if location is inside map's maximum extent, false otherwise
	 * 
	 * @param location
	 * @return
	 */
	public boolean isLocationInMaxExtent(android.location.Location location) {
		return isLocationInMaxExtent(location.getLongitude(), location.getLatitude());
	}

	/**
	 * Calculate distance in meters from p1 to p2
	 * 
	 * @param p1
	 * @param p2
	 * @return distance in meters from p1 to p2
	 */
	public double getCoordDistanceInMeters(Coordinates p1, Coordinates p2) {
		return facade.getMap().getCoordDistanceInMeters(p1, p2);
	}

	/**
	 * @return map center coordinates
	 */
	public Coordinates getMapCenter() {
		return facade.getMap().getCenter();
	}

	/**
	 * @return map zoom level
	 */
	public int getMapZoomLevel() {
		if (facade != null) {
			return facade.getMap().getZoomLvl();
		}
		return -1;
	}

	/**
	 * 
	 * @param points
	 * @param crs
	 * @param context
	 * @param layer
	 * @throws Exception
	 */
	public void drawPoints(Collection<Point> points, String crs, Context context, String layer)
			throws Exception {
		GeoJSONFeatureCollection geoJSONFeatureCollection = new GeoJSONFeatureCollection();

		for (Point point : points) {
			GeoJSONFeature geoJSONFeature = new GeoJSONFeature();
			JSONArray coords = new JSONArray();
			coords.put(point.getLongtiud());
			coords.put(point.getLatitud());
			JSONObject geometry = new JSONObject();
			geometry.put("type", "Point");
			geometry.put("coordinates", coords);
			geoJSONFeature.setGeometry(geometry);
			geoJSONFeature.setFeatureId("" + point.getId());
			geoJSONFeature.putProperty(context.getString(R.string.constant_wmsclient_geoJSONStringSelectable), true);
			geoJSONFeature.putProperty(context.getString(R.string.constant_wmsclient_geoJSONStringType),
					context.getString(R.string.constant_wmsclient_geoJSONStringTypePoint));
			geoJSONFeature.putProperty("date", point.getFecha());
			geoJSONFeatureCollection.add(geoJSONFeature);
		}
		if (crs != null) {
			geoJSONFeatureCollection.setCRS(crs);
		}
		facade.addGeoJSON(geoJSONFeatureCollection.getJSONObject(), layer, false);
	}

	/**
	 * 
	 * @param points
	 * @param crs
	 * @param context
	 * @throws Exception
	 */
	public void drawPointsAll(Collection<Point> points, String crs, Context context) throws Exception {
		drawPoints(points, crs, context, MapViewer.LAYER_NAME_POINTS_ALL);
	}

	private GeoJSONFeature getRouteGeoJSONFeature(Context context, String type, String geomType, JSONArray coords,
			String id) throws Exception {
		GeoJSONFeature geoJSONFeature = new GeoJSONFeature();
		geoJSONFeature.putProperty(context.getString(R.string.constant_wmsclient_geoJSONStringSelectable), id != null);
		if (id != null) {
			geoJSONFeature.setFeatureId(id);
		}
		geoJSONFeature.putProperty(context.getString(R.string.constant_wmsclient_geoJSONStringType), type);
		JSONObject geometry = new JSONObject();
		geometry.put("type", geomType);
		geometry.put("coordinates", coords);
		geoJSONFeature.setGeometry(geometry);
		return geoJSONFeature;
	}
	
	public void drawRoute(Context context, ArrayList<Point> line, String id,
			boolean centerInData) throws Exception {
		GeoJSONFeatureCollection geoJSONFeatureCollection = new GeoJSONFeatureCollection();

		// Geometry
		JSONArray coords = new JSONArray();

		for (Point point : line) {
			JSONArray coord = new JSONArray();
			coord.put(point.getLongtiud());
			coord.put(point.getLatitud());
			coords.put(coord);

		}

		geoJSONFeatureCollection.add(getRouteGeoJSONFeature(context,
				context.getString(R.string.constant_wmsclient_geoJSONStringTypeGeometry), "LineString", coords, null));

		geoJSONFeatureCollection.setCRS("EPSG:4326");

		facade.addGeoJSON(geoJSONFeatureCollection.getJSONObject(), "Layer_Route", centerInData);
	}



}