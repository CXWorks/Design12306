package service;

import java.util.Calendar;
import java.util.Map;

/**
 *  Design12306/service/TicketQuery.java
 *  2016年11月8日 下午3:44:25
 *  @Author cxworks
 *
 */

public class TicketQuery extends AutoResult{
	private Calendar date;
	private String tid;
	private Map<Integer, Integer> seats;
	public TicketQuery(Calendar date,String tid) {
		// TODO Auto-generated constructor stub
	}
}
