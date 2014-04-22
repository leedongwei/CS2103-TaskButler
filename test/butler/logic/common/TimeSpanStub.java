// @author A0097836L
package butler.logic.common;

import java.util.Date;

import butler.common.TimeSpan;

public class TimeSpanStub extends TimeSpan {

	private Date nowStub;

	public TimeSpanStub(Date nowStub) {
		this(null, null);
	}

	public TimeSpanStub(Date startTime, Date endTime) {
		super(startTime, endTime);
		this.nowStub = DateStub.getNowTime();
	}

	@Override
	protected Date getNowTime() {
		assert nowStub != null;
		return nowStub;
	}

}
