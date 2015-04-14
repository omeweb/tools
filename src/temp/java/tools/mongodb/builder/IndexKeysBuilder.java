package tools.mongodb.builder;

import com.mongodb.BasicDBObject;

public class IndexKeysBuilder extends Builder {
	public IndexKeysBuilder asc(String... keys) {
		BasicDBObject operates = super.getResult();
		for (String item : keys) {
			operates.put(item, 1);
		}
		return this;
	}

	public IndexKeysBuilder desc(String... keys) {
		BasicDBObject operates = super.getResult();
		for (String item : keys) {
			operates.put(item, -1);
		}
		return this;
	}

	public IndexKeysBuilder GeoSpatial(String name) {
		super.getResult().put(name, "2d");
		return this;
	}

	public IndexKeysBuilder geoSpatialHaystack(String name) {
		return geoSpatialHaystack(name, null);
	}

	/**
	 * Sets the key name and additional field name to create a geospatial haystack index on.
	 * 
	 * @param name
	 * @param additionalName
	 * @return
	 */
	public IndexKeysBuilder geoSpatialHaystack(String name, String additionalName) {
		super.getResult().put(name, "geoHaystack");

		if (additionalName != null)
			super.getResult().put(additionalName, 1);
		return this;
	}
}
