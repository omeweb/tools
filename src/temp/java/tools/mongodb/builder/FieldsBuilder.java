package tools.mongodb.builder;

import com.mongodb.BasicDBObject;

public class FieldsBuilder extends Builder {
	public FieldsBuilder exclude(String... keys) {
		BasicDBObject operates = super.getResult();
		for (String item : keys) {
			operates.put(item, 0);
		}
		return this;
	}

	public FieldsBuilder include(String... keys) {
		BasicDBObject operates = super.getResult();
		for (String item : keys) {
			operates.put(item, 1);
		}
		return this;
	}
}
