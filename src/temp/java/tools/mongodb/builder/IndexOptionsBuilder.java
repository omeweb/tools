package tools.mongodb.builder;

public class IndexOptionsBuilder extends Builder {
	public IndexOptionsBuilder background(boolean value) {
		super.getResult().put("background", value);
		return this;
	}

	public IndexOptionsBuilder bucketSize(double value) {
		super.getResult().put("bucketSize", value);
		return this;
	}

	public IndexOptionsBuilder dropDups(boolean value) {
		super.getResult().put("dropDups", value);
		return this;
	}

	public IndexOptionsBuilder geoSpatialRange(double min, double max) {
		super.getResult().put("min", min);
		super.getResult().put("max", max);
		return this;
	}

	public IndexOptionsBuilder name(String value) {
		super.getResult().put("name", value);
		return this;
	}

	public IndexOptionsBuilder sparse(boolean value) {
		super.getResult().put("sparse", value);
		return this;
	}

	public IndexOptionsBuilder unique(boolean value) {
		super.getResult().put("unique", value);
		return this;
	}
}
