package service;

import java.util.Calendar;

/**
 *  Design12306/service/TicketService.java
 *  2016年11月8日 下午3:42:06
 *  @Author cxworks
 *
 */

public interface TicketService {
	
	public AutoResult queryTrain(String src,String target, Calendar date) ;
	
	public AutoResult orderTicket(String src,String target,String tid,Calendar date,String seats_type, int account, int... customer) ;
}
