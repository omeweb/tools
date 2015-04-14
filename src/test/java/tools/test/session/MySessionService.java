package tools.test.session;

import tools.ObjectConverter;
import tools.session.SessionServiceBase;
import tools.test.domain.User;

public class MySessionService extends SessionServiceBase<User> {
	public MySessionService() {
		super.setConverter(new MyConverter());
		super.setStorer(new tools.Context<String>());
	}
}

class MyConverter implements ObjectConverter<User, String> {

	@Override
	public User from(String v) {
		User entry = new User();
		entry.setUserName(v);
		return entry;
	}

	@Override
	public String to(User t) {
		return t.getUserName() + ".";
	}
}
