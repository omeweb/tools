package tools;

/**
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2013-12-9
 */
public interface ObjectConverter<TFrom, TTo> {
	TFrom from(TTo v);

	TTo to(TFrom t);
}
