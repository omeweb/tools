package tools.test;

public class NanoTimeTest {
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		while (true) {
			long start = System.nanoTime();
			for (int i = 0; i < 1; i++)
				Thread.currentThread().sleep(200);

			long end = System.nanoTime();
			long cost = end - start;
			if (cost < 0) {
				System.out.println("start: " + start + ", end: " + end + ", cost: " + cost);
			} else
				System.out.println(cost / 1000 / 1000); // 换算为毫秒
		}
	}
}
