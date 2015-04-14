package tools.oauth;

public enum Display {
	DEFAULT("default"), MOBILE("mobile"), JS("js"), WAP12("wap1.2"), WAP20("wap2.0");

	private final String value;

	private Display(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static void main(String args[]) {
		for (Display item : Display.values()) {
			System.out.println(item + "====>" + item.getValue());
		}
	}
}
