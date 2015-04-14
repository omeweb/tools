package tools.generator.ranged;

import java.util.ArrayList;
import java.util.List;

/**
 * （min,max】
 * 
 * @author liusan.dyf
 */
public class Range {
	private long min;
	private long max;

	public Range(long min, long max) {
		if (min > max)
			throw new IllegalArgumentException("min can not greater then max");
		setMin(min);
		setMax(max);
	}

	public long getMin() {
		return min;
	}

	@Override
	public String toString() {
		return "Range (min=" + min + ", max=" + max + "]";
	}

	public void setMin(long v) {
		this.min = v;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long v) {
		this.max = v;
	}

	public long length() {
		return (this.max - this.min + 1);
	}

	/**
	 * 划分区间
	 * 
	 * @param range
	 * @param size
	 * @return
	 */
	public static List<Range> div(Range range, int size) {
		if (range == null)
			return new ArrayList<Range>(0);
		if (size < 1)
			throw new IllegalArgumentException("size can not be less then 1");

		long min = range.getMin();
		long max = range.getMax();

		// 计算会有多少个区间
		long d = max - min + 1;
		long len = d % size;
		if (len == 0)
			len = d / size;
		else
			len = d / size + 1;

		// System.out.println(len);

		ArrayList<Range> list = new ArrayList<Range>((int) len);

		while (min <= max) {// 注意条件是<=
			long mx = min + size - 1;
			if (mx > max) {
				list.add(new Range(min, max));
				break;
			}

			list.add(new Range(min, mx));
			min += size;
		}
		return list;
	}
}
// Range [min=1, max=3]
// Range [min=4, max=6]
// Range [min=7, max=9]
// Range [min=10, max=10]
