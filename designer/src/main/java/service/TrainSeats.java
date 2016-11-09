package service;

/*
  Design12306/service/TrainSeats.java
  @author cxworks
  2016-11-09
*/

public class TrainSeats {
	private String tid;
	private int[] seats;
	public TrainSeats(String tid,int[] seats) {
		this.seats=seats;
		this.tid=tid;
	}
	@Override
	public String toString(){
		return "车号: "+tid+" 商务座: "+seats[0]+" 一等座: "+seats[1]+" 二等座: "+seats[2]+" 无座: "+seats[3]+"\n";
	}
}
