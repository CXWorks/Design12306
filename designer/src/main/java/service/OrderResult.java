package service;

import java.util.Calendar;
import java.util.List;

/**
 *  Design12306/service/OrderResult.java
 *  2016年11月8日 下午3:44:39
 *  @Author cxworks
 *
 */

public class OrderResult extends AutoResult{
	private Calendar date;
	private String src;
	private String tar;
	private List<CustomerSeats> customerSeats;
	public OrderResult(Calendar date, String src, String tar, List<CustomerSeats> customerSeats) {
		
		this.date = Calendar.getInstance();
		this.date.setTimeInMillis(date.getTimeInMillis());
		this.src = src;
		this.tar = tar;
		this.customerSeats = customerSeats;
	}
	@Override
	public String toString() {
		String ans="日期: "+this.date.get(Calendar.YEAR)+"年"+(this.date.get(Calendar.MONTH)+1)+"月"+this.date.get(Calendar.DAY_OF_MONTH)+"日\n";
		ans+="出发地: "+src+"\n目的地: "+tar+"\n";
		for (CustomerSeats customerSeats2 : customerSeats) {
			ans+=customerSeats2.toString();
		}
		return ans;
	}
	
}
