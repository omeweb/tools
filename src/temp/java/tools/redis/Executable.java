package tools.redis;

/**
 * 表示是可执行的，用来做回调
 * 
 * @author liusan.dyf
 */
public interface Executable {
	void execute(Object value);
}
