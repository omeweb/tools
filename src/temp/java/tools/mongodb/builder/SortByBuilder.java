package tools.mongodb.builder;

import com.mongodb.BasicDBObject;

public class SortByBuilder extends Builder {
	public SortByBuilder asc(String... keys) {
		BasicDBObject operates = super.getResult();
		for (String item : keys) {
			operates.put(item, 1);
		}
		return this;
	}

	public SortByBuilder desc(String... keys) {
		BasicDBObject operates = super.getResult();
		for (String item : keys) {
			operates.put(item, -1);
		}
		return this;
	}
}
