package tools.test;

import java.util.Date;
import java.util.Map;

public class BeanTest {
	public static void main(String[] args) {
		A a = new A();
		a.setId(10086);

		B b = new B();

		// copy
		tools.BeanUtil.copy(a, b);

		System.out.println(tools.Json.toString(b));

		// fill
		Map<String, Object> map = tools.MapUtil.create();
		map.put("name", "六三");
		map.put("date", new Date());
		tools.BeanUtil.fill(b, map);

		System.out.println(tools.Json.toString(b));
	}
}

class A {
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}

class B {
	private long id;
	private String name;
	private Date date;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
