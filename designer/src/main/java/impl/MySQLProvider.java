package impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import connector.MySQL;
import service.AutoResult;
import service.TicketService;

/**
 *  Design12306/impl/MySQLProvider.java
 *  2016年11月9日 上午8:20:29
 *  @Author cxworks
 *
 */

public class MySQLProvider implements TicketService {
	private MySQL mySql;
	public MySQLProvider() {
		mySql=new MySQL();
	}
	private Connection getConection() {
		try {
			Connection connection=mySql.getConnection();
			connection.setAutoCommit(true);
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
		try (PreparedStatement pStatement=getConection().prepareStatement(sql)){
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
			return ans;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ans;
		}
	}
	public static void main(String[] args) {
		System.out.println(MySQLProvider.generateMask(2, 5));
		
	}
	
	public static String generateMask(int src,int tar) {
		int origin=new Double(Math.pow(2, tar-src)-1).intValue();
		origin=origin<<(src-1);
		return Integer.toBinaryString(origin);
	}
	
	@Override
	public AutoResult queryTrain(String src, String target, Calendar date) {
		//query 
		Map<String, Integer[]> avaliable=queryTrain(src, target);
		if (avaliable.isEmpty()) {
			return null;
		}
		//
		
		for(Map.Entry<String, Integer[]> entry:avaliable.entrySet()){
			String tid=entry.getKey();
			Integer[] ordered=entry.getValue();
			int ttid=Integer.parseInt(tid.substring(1));
			if (ttid>140) {
				continue;
			}
			String mask=generateMask(ordered[0], ordered[1]);
			String sql="SELECT `stype`, COUNT(*) "
					+ "FROM `G?` "
					+ "WHERE `train_date` = ? AND `ticket` & b? = b? "
					+ "GROUP BY `stype`";
			int j=1;
			try (PreparedStatement pStatement=getConection().prepareStatement(sql)){
				pStatement.setInt(j++, ttid);
				pStatement.setDate(j++, new Date(date.getTimeInMillis()));
				pStatement.setString(j++, mask);
				pStatement.setString(j++, mask);
				ResultSet resultSet=pStatement.executeQuery();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}

	@Override
	public AutoResult orderTicket(String src, String target, Calendar date, int account, int... customer) {
		// TODO Auto-generated method stub
		return null;
	}

}
