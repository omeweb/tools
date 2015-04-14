package tools.mongodb.builder;

public class CollectionOptionsBuilder extends Builder {
	public CollectionOptionsBuilder autoIndexId(boolean value) {
		if (value) {
			super.getResult().put("autoIndexId", value);
		} else {
			super.getResult().remove("autoIndexId");
		}
		return this;
	}

	public CollectionOptionsBuilder capped(boolean value) {
		if (value) {
			super.getResult().put("capped", value);
		} else {
			super.getResult().remove("capped");
		}
		return this;
	}

	public CollectionOptionsBuilder maxDocuments(long value) {
		super.getResult().put("max", value);
		return this;
	}

	/**
	 * 单位是byte
	 * 
	 * @param value
	 * @return
	 */
	public CollectionOptionsBuilder maxSize(long value) {
		super.getResult().put("size", value);
		return this;
	}
}
