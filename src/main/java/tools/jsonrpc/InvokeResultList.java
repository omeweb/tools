package tools.jsonrpc;

import java.util.ArrayList;
import java.util.List;

/**
 * 一组调用结果的封装 2011-10-27 by 63
 * 
 * @author liusan.dyf
 */
public class InvokeResultList {
	private boolean isBatch;
	private List<SingleInvokeResult> results = new ArrayList<SingleInvokeResult>();

	public boolean isBatch() {
		return isBatch;
	}

	public void setBatch(boolean isBatch) {
		this.isBatch = isBatch;
	}

	public List<SingleInvokeResult> getResults() {
		return results;
	}

	public void setResults(List<SingleInvokeResult> results) {
		this.results = results;
	}
}
