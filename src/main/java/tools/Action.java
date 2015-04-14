package tools;

/**
 * 命名采用c#同名委托：http://msdn.microsoft.com/en-us/library/018hxwa8.aspx
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-12-20
 */
public interface Action<T> {
	void execute(T t);
}

// interface Action2<T1, T2> {
// void execute(T1 t1, T2 t2);
// }
