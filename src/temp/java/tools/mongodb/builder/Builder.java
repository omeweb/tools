package tools.mongodb.builder;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class Builder {
	private BasicDBObject operates;

	/**
	 * 2011-11-14
	 * 
	 * @return
	 */
	public BasicDBObject getResult() {
		return operates;
	}

	@Override
	public String toString() {
		return JSON.serialize(operates);
	}

	/**
	 * Initializes a new instance of the QueryConditionList class.
	 */
	public Builder() {
		operates = new BasicDBObject();
	}

	/**
	 * 2012-02-21
	 * 
	 * @param ops
	 */
	public Builder(BasicDBObject ops) {
		operates = ops;
	}
}
