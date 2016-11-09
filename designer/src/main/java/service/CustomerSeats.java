package service;

/*
  Design12306/service/CustomerSeats.java
  @author cxworks
  2016-11-09
*/

public class CustomerSeats {
	public int cid;
	private int stype;
	private int t_c_id;
	private int row;
	private int location;
	public CustomerSeats(int cid, int stype, int t_c_id, int row, int location) {
		this.cid = cid;
		this.stype = stype;
		this.t_c_id = t_c_id;
		this.row = row;
		this.location = location;
	}
	private String switchStype(int stype){
		switch (stype) {
		case 0:
			return "商务座";
		case 1:
			return "一等座";
		case 2:
			return "二等座";
		case 3:
		default:
			return "无座";
		}
	}
	private String switchLocation(int location) {
		switch (location) {
		case 0:
			return "A";
		case 1:
			return "B";
		case 2:
			return "C";
		case 3:
			return "D";
		case 4:
			return "E";
		case 5:
			
		default:
			return "无座";
		}
	}
	@Override
	public String toString() {
		return "顾客座位  [顾客编号=" + cid + ", 座位类型=" + switchStype(stype) + ", 车厢号=" + t_c_id + ", 排号=" + row + ", 座位号="
				+ switchLocation(location) + "]\n";
	}
	
}
