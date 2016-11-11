package impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mongodb.event.ConnectionCheckedInEvent;

import connector.MySQL;
import service.AutoResult;
import service.CustomerSeats;
import service.OrderResult;
import service.TicketQuery;
import service.TicketService;
import service.TrainSeats;

/**
 *  Design12306/impl/MySQLProvider.java
 *  2016年11月9日 上午8:20:29
 *  @Author cxworks
 *
 */

public class MySQLProvider implements TicketService {
	private MySQL mySql;
	private Interner<String> pool;
	public MySQLProvider() {
		mySql=new MySQL();
		pool=Interners.newWeakInterner();
	}
	private synchronized Connection getConection() {
		try {
			Connection connection=mySql.getConnection();
			return connection;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	private Map<String, Integer[]> queryTrain(String src,String target){
		Map<String, Integer[]> ans=new HashMap<>();
		String sql="SELECT t1.`tid`,t1.`orderedNum`, t2.`orderedNum` "
				+ "FROM `station_train` t1, `station_train` t2 "
				+ "WHERE t1.station = ? AND t2.station = ? "
				+ "AND t1.`tid` = t2.`tid` AND t1.`orderedNum` < t2.`orderedNum`";
		synchronized (pool.intern(src)) {
			try (Connection connection=getConection()){
				PreparedStatement pStatement=connection.prepareStatement(sql);
				int j=1;
				pStatement.setString(j++, src);
				pStatement.setString(j++, target);
				ResultSet resultSet=pStatement.executeQuery();
				while (resultSet.next()) {
					Integer[] a=new Integer[2];
					String tid=resultSet.getString(1);
					a[0] = resultSet.getInt(2);
					a[1] = resultSet.getInt(3);
					ans.put(tid, a);
				}	
				resultSet.close();
				connection.close();
				return ans;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return ans;
			}
		
		}
			
		
	}
	private int[] queryStationNum(String src,String target,String tid){
		int[] ans=new int[2];
		String sql="SELECT `orderedNum` FROM `station_train` WHERE `tid`= ? AND (`station`= ? OR `station`= ?) ORDER BY `orderedNum`";
		try (Connection connection=getConection()){
			PreparedStatement pStatement=connection.prepareStatement(sql);
			int j=1;
			pStatement.setString(j++, tid);
			pStatement.setString(j++, src);
			pStatement.setString(j++, target);
			ResultSet resultSet=pStatement.executeQuery();
			
			for(int i=0;i<2&&resultSet.next();i++){
				ans[i]=resultSet.getInt(1);
			}
			resultSet.close();
			return ans;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ans;
		}
	}
	public static String generateMask(int src,int tar) {
		int origin=new Double(Math.pow(2, tar-src)-1).intValue();
		origin=origin<<(src-1);
		return Integer.toBinaryString(origin);
	}
	public static String generateUpadate(int src,int tar){
		int origin=new Double(Math.pow(2, tar-src)-1).intValue();
		origin=origin<<(src-1);
		int max=Integer.MAX_VALUE;
		int ans=max^origin;
		return Integer.toBinaryString(ans);
	}
	@Override
	public AutoResult queryTrain(String src, String target, Calendar date) {
		//query 
		Map<String, Integer[]> avaliable=queryTrain(src, target);
		if (avaliable.isEmpty()) {
			return null;
		}
		//
		String sql="SELECT `stype`, COUNT(*) "
				+ "FROM `G?` "
				+ "WHERE `train_date` = ? AND `ticket` & b? = b? "
				+ "GROUP BY `stype`";
		List<TrainSeats> trainSeats=new ArrayList<>(30);
		synchronized (pool.intern(src)) {
			try (Connection connection=getConection()){
				PreparedStatement pStatement=connection.prepareStatement(sql);
			for(Map.Entry<String, Integer[]> entry:avaliable.entrySet()){
				String tid=entry.getKey();
				Integer[] ordered=entry.getValue();
				int ttid=Integer.parseInt(tid.substring(1));
				String mask=generateMask(ordered[0], ordered[1]);
				int j=1;
					pStatement.setInt(j++, ttid);
					pStatement.setDate(j++, new Date(date.getTimeInMillis()));
					pStatement.setString(j++, mask);
					pStatement.setString(j++, mask);
					ResultSet resultSet=pStatement.executeQuery();
					int[] seats=new int[5];
					while(resultSet.next()){
						seats[resultSet.getInt(1)]=resultSet.getInt(2);
					}
					resultSet.close();
					trainSeats.add(new TrainSeats(tid, seats));
				
			}
			connection.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		AutoResult autoResult=new TicketQuery(date, src, target, trainSeats);
		return autoResult;
	}
	
	public static void main(String[] args) {
		MySQLProvider mySQLProvider=new MySQLProvider();
		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 2);
		System.out.println(mySQLProvider.queryTrain("济南西", "上海虹桥", calendar));
//		System.out.println(mySQLProvider.orderTicket("济南西", "上海虹桥", "G41", calendar, "二等座", 1,1, 2));
//		System.out.println(mySQLProvider.queryStationNum("济南西", "上海虹桥", "G41")[1]);
//		System.out.println(generateUpadate(2, 5));
	}
	private int seats2stype(String seats_type) {
		if (seats_type.contains("商")) {
			return 0;
		}else if (seats_type.contains("一")) {
			return 1;
		}else if(seats_type.contains("二")){
			return 2;
		}else {
			return 3;
		}
	}
	private int[] getRange(int count,int stype,int len) {
		switch (stype) {
		case 0:
			int[] a={3-count,4-count+len};
			return a;
		case 1:
		case 2:
			int[] b={4-count,5-count+len};
			return b;
		case 3:
			int[] c={4,6};
			return c;
		default:
			return null;
		}
	}
	private List<CustomerSeats> lockSeats(String tid,Calendar date,int stype,String mask,String update,int len) {
		List<CustomerSeats> ans=new ArrayList<>(10);
		String sql1="SELECT `t_c_id`,`row`,COUNT(*) FROM `G?` WHERE `train_date` = ? AND `stype` = ? AND `ticket` & b? = b? GROUP BY `t_c_id`,`row` HAVING COUNT(*) >= ? LIMIT 1";
		synchronized (pool.intern(tid)) {
			try (Connection connection=getConection()){
				int ttid=Integer.parseInt(tid.substring(1));
				//first
				PreparedStatement pStatement=connection.prepareStatement(sql1);
				int j=1;
				pStatement.setInt(j++, ttid);
				pStatement.setDate(j++, new Date(date.getTimeInMillis()));
				pStatement.setInt(j++, stype);
				pStatement.setString(j++, mask);
				pStatement.setString(j++, mask);
				pStatement.setInt(j++, len);
				ResultSet resultSet=pStatement.executeQuery();
				if (resultSet.next()) {
					String sql2="UPDATE `G?` SET `ticket` = `ticket` & b? WHERE `train_date` = ? AND `t_c_id` = ? AND `row` = ? AND (`location` > ? AND `location`< ?)";
					int t_c_id=resultSet.getInt(1);
					int row=resultSet.getInt(2);
					int count=resultSet.getInt(3);
					resultSet.close();
					//lock
					int[] r=getRange(count, stype, len);
					
					PreparedStatement pStatement2=connection.prepareStatement(sql2);
					j=1;
					pStatement2.setInt(j++, ttid);
					pStatement2.setString(j++, update);
					pStatement2.setDate(j++, new Date(date.getTimeInMillis()));
					pStatement2.setInt(j++, t_c_id);
					pStatement2.setInt(j++, row);
					pStatement2.setInt(j++, r[0]);
					pStatement2.setInt(j++, r[1]);
					try {
						pStatement2.executeUpdate();
						connection.commit();
						connection.close();
						for (int i = r[0]+1; i < r[1]; i++) {
							CustomerSeats customerSeats= new CustomerSeats(0, stype, t_c_id, row, i);
							ans.add(customerSeats);
						}
						
						return ans;
					} catch (Exception e) {
						e.printStackTrace();
						connection.rollback();
						System.out.println("rollback");
						connection.close();
						return ans;
					}
					
				}else {
					String sql3="SELECT `t_c_id`, `row`, `location` FROM `G?` WHERE `train_date` = ? AND stype = ? AND `ticket` & b? = b? LIMIT ?";
					PreparedStatement pStatement2=connection.prepareStatement(sql3);
					j=1;
					pStatement2.setInt(j++, ttid);
					pStatement2.setDate(j++, new Date(date.getTimeInMillis()));
					pStatement2.setInt(j++, stype);
					pStatement2.setString(j++, mask);
					pStatement2.setString(j++, mask);
					pStatement2.setInt(j++, len);
					ResultSet resultSet2=pStatement2.executeQuery();
					if (resultSet2.next()) {
						String sql4="UPDATE `G?` SET `ticket` = `ticket` & b? WHERE `train_date` = ? AND `t_c_id` = ? AND `row` = ? AND `location` = ?";
						PreparedStatement pStatement3=connection.prepareStatement(sql4);
						do {
							j=1;
							int t_c_id=resultSet2.getInt(1);
							int row=resultSet2.getInt(2);
							int location=resultSet2.getInt(3);
							pStatement3.setInt(j++, ttid);
							pStatement3.setString(j++, update);
							pStatement3.setDate(j++, new Date(date.getTimeInMillis()));
							pStatement3.setInt(j++, t_c_id);
							pStatement3.setInt(j++, row);
							pStatement3.setInt(j++, location);
							pStatement3.addBatch();
							CustomerSeats customerSeats=new CustomerSeats(0, stype, t_c_id, row, location);
							ans.add(customerSeats);
						} while (resultSet2.next());
						resultSet2.close();
						try {
							pStatement3.executeBatch();
							connection.commit();
							connection.close();
							return ans;
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("rollback");
							e.printStackTrace();
							connection.rollback();
							connection.close();
							return ans;
						}
						
					}else {
						return ans;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return ans;
	}
	private void insertOrder(String src, String tar,String tid, Calendar date,int account,List<CustomerSeats> customerSeats) {
		synchronized (pool.intern(src)) {
			try (Connection connection=getConection()){
				
				String sql1="INSERT INTO `order`(`oid`, `aid`, `orderAt`, `tid`, `start`, `end`, `startAt`) VALUES (?,?,?,?,?,?,?)";
				PreparedStatement pStatement1=connection.prepareStatement(sql1);
				int j=1;
				String oid=src+tar+account+Calendar.getInstance().getTimeInMillis();
				pStatement1.setString(j++, oid);
				pStatement1.setInt(j++,account);
				pStatement1.setDate(j++, new Date(Calendar.getInstance().getTimeInMillis()));
				pStatement1.setString(j++, tid);
				pStatement1.setString(j++, src);
				pStatement1.setString(j++, tar);
				pStatement1.setDate(j++, new Date(date.getTimeInMillis()));
				pStatement1.execute();
				String sql2="INSERT INTO `order_ticket`(`oid`, `cid`, `aid`, `stype`, `ctype`, `t_c_id`, `row`, `location`) VALUES (?,?,?,?,?,?,?,?)";
				PreparedStatement pStatement2=connection.prepareStatement(sql2);
				for (CustomerSeats customerSeats2 : customerSeats) {
					j=1;
					pStatement2.setString(j++, oid);
					pStatement2.setInt(j++, customerSeats2.cid);
					pStatement2.setInt(j++, account);
					pStatement2.setInt(j++, customerSeats2.stype);
					pStatement2.setInt(j++, 0);
					pStatement2.setInt(j++, customerSeats2.t_c_id);
					pStatement2.setInt(j++, customerSeats2.row);;
					pStatement2.setInt(j++, customerSeats2.location);
					pStatement2.addBatch();
				}
				pStatement2.executeBatch();
				connection.commit();
				connection.close();
				System.out.println("order inserted");
			} catch (Exception e) {
				e.printStackTrace();
				return ;
				
			}
			return ;
		}		
		
				
}
	@Override
	public AutoResult orderTicket(String src, String target,String tid, Calendar date, String seats_type, int account, int... customer) {
		int stype=seats2stype(seats_type);
		//query
		int[] sid=queryStationNum(src, target, tid);
		//
		int len=customer.length;
		
		String mask=generateMask(sid[0], sid[1]);
		String update=generateUpadate(sid[0], sid[1]);
		//
		List<CustomerSeats> customerSeats=lockSeats(tid, date, stype, mask, update, len);
		if (customerSeats.isEmpty()) {
			return new AutoResult();
		}
		for(int i=0;i<customerSeats.size();i++){
			customerSeats.get(i).cid=customer[i];
		}
		OrderResult orderResult=new OrderResult(date, src, target, customerSeats);
		insertOrder(src, target, tid, date, account, customerSeats);
		return orderResult;
	}

}
