package service;

/**
 *  Design12306/service/TicketService.java
 *  2016年11月8日 下午3:42:06
 *  @Author cxworks
 *
 */

public interface TicketService {
	
	public AutoResult queryTrain(String src,String target) ;
	
	public AutoResult orderTicket(String src,String target,int account,int... customer) ;
}
