package tools.test.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yong Huang VM args: -Xms20M -Xmx20M
 * 
 *         <pre>
 * Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
 * 	at java.util.Arrays.copyOf(Arrays.java:2760)
 * 	at java.util.Arrays.copyOf(Arrays.java:2734)
 * 	at java.util.ArrayList.ensureCapacity(ArrayList.java:167)
 * 	at java.util.ArrayList.add(ArrayList.java:351)
 * 	at tools.test.oom.HeapOOM.main(HeapOOM.java:22)
 * </pre>
 */
public class HeapOOM {

	public HeapOOM() {
		// TODO Auto-generated constructor stub
	}

	static class OOMObject {

	}

	public static void main(String[] args) {
		List<OOMObject> list = new ArrayList<OOMObject>();
		while (true) {
			list.add(new OOMObject());
		}
	}

}
