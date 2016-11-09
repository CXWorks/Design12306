package service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *  Design12306/service/TicketQuery.java
 *  2016年11月8日 下午3:44:25
 *  @Author cxworks
 *
 */

public class TicketQuery extends AutoResult{
	private Calendar date;
	private String src;
	private String tar;
	private List<TrainSeats> trainSeats;
	public TicketQuery(Calendar date,String src,String tar,List<TrainSeats> trainSeats) {
		
		this.date=Calendar.getInstance();
		this.date.setTimeInMillis(date.getTimeInMillis());
		this.src=src;
		this.tar=tar;
		this.trainSeats=trainSeats;
	}
	@Override
	public String toString(){
		String ans="日期: "+this.date.get(Calendar.YEAR)+"年"+(this.date.get(Calendar.MONTH)+1)+"月"+this.date.get(Calendar.DAY_OF_MONTH)+"日\n";
		ans+="出发地: "+src+"\n目的地: "+tar+"\n";
		for (TrainSeats trainSeats2 : trainSeats) {
			ans+=trainSeats2;
		}
		return ans;
	}
}
