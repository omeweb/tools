package tools.test;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsonTest {
	public static void main(String[] args) {
		// User entry = new User();
		// entry.setUserName("omeweb");
		//
		// System.out.println(tools.Json.toString(entry));

		Entity entry = new Entity();
		entry.setS("string");
		entry.setBody("中国".getBytes());

		String json = tools.Json.toString(entry);
		System.out.println(json);

		Entity another = tools.Json.toObject(json, Entity.class);

		System.out.println(new String(another.getBody()));
	}
}

class Entity {
	private String s;
	private byte[] body;
	
	private int age = 10;

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	@JsonIgnore
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
