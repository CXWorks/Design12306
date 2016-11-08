package init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connector.MySQL;

/**
 *  Design12306/init/CreateTrainTable.java
 *  2016年11月8日 上午8:29:46
 *  @Author cxworks
 *
 */

public class CreateTrainTable {
	private MySQL mySQL;
	public CreateTrainTable() {
		mySQL=new MySQL();
	}
	public static void main(String[] args) {
		CreateTrainTable a=new CreateTrainTable();
		a.initG_Train();
	}
	private Connection getConnection(){
		try {
			Connection connection=mySQL.getConnection();
			connection.setAutoCommit(true);
			return connection;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	private Map<String, Integer> getCarriage_seats() {
		String sql="SELECT `tid`,COUNT(*) FROM `train_carriage` WHERE 1 GROUP BY `tid`";
		//now just hard coding
		try (PreparedStatement pStatement=getConnection().prepareStatement(sql)){
			ResultSet resultSet=pStatement.executeQuery();
			Map<String, Integer> ans=new HashMap<>(200);
			while (resultSet.next()) {
				ans.put(resultSet.getString(1), resultSet.getInt(2));
				
			}
			return ans;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	private List<int[]> getTrain_Carriage(String tid) {
		String select="SELECT `carriage_id`, `stype`, `t_c_id` FROM `train_carriage` WHERE `tid` = ?";
		try (PreparedStatement pStatement=getConnection().prepareStatement(select)){
			pStatement.setString(1, tid);
			ResultSet resultSet=pStatement.executeQuery();
			List<int[]> ans=new ArrayList<>(20);
			for (int i = 0; resultSet.next(); i++) {
				int[] a=new int[3];
				a[0]=resultSet.getInt(1);
				a[1]=resultSet.getInt(2);
				a[2]=resultSet.getInt(3);
				ans.add(a);
			}
			return ans;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	public void  initG_Train() {
		String sql="INSERT INTO `G?`(`tid`, `t_c_id`, `stype`, `row`, `location`) VALUES (?,?,?,?,?)";
		Map<String, Integer> t_c=getCarriage_seats();
		Reader reader=new Reader();
		List<String[]> train=reader.getRoutes();
		try (PreparedStatement pStatement=getConnection().prepareStatement(sql)){
			for (String[] strings : train) {
				String tid=strings[0];
				int ttid=Integer.parseInt(tid.substring(1));
				if (t_c.get(tid)==8) {
					//1
					for(int i=0;i<1;i++){
						for(int j=0;j<10;j++){
							for(int k=0;k<4;k++){
								int pq=1;
								pStatement.setInt(pq++, ttid);
								pStatement.setString(pq++,tid);
								pStatement.setInt(pq++, i+1);
								pStatement.setInt(pq++, 0);
								pStatement.setInt(pq++, j+1);
								pStatement.setInt(pq++, k);
								pStatement.addBatch();
							}
						}
					}
					//2
					for(int i=1;i<3;i++){
						for(int j=0;j<16;j++){
							for(int k=0;k<5;k++){
								int pq=1;
								pStatement.setInt(pq++, ttid);
								pStatement.setString(pq++,tid);
								pStatement.setInt(pq++, i+1);
								pStatement.setInt(pq++, 1);
								pStatement.setInt(pq++, j+1);
								pStatement.setInt(pq++, k);
								pStatement.addBatch();
							}
						}
					}
					//5
					for(int i=3;i<8;i++){
						for(int j=0;j<20;j++){
							for(int k=0;k<6;k++){
								int pq=1;
								pStatement.setInt(pq++, ttid);
								pStatement.setString(pq++,tid);
								pStatement.setInt(pq++, i+1);
								pStatement.setInt(pq++, 2);
								pStatement.setInt(pq++, j+1);
								pStatement.setInt(pq++, k);
								pStatement.addBatch();
							}
						}
					}
					pStatement.executeBatch();
				}else {
					//3
					for(int i=0;i<3;i++){
						for(int j=0;j<10;j++){
							for(int k=0;k<4;k++){
								int pq=1;
								pStatement.setInt(pq++, ttid);
								pStatement.setString(pq++,tid);
								pStatement.setInt(pq++, i+1);
								pStatement.setInt(pq++, 0);
								pStatement.setInt(pq++, j+1);
								pStatement.setInt(pq++, k);
								pStatement.addBatch();
							}
						}
					}
					//5
					for(int i=3;i<8;i++){
						for(int j=0;j<16;j++){
							for(int k=0;k<5;k++){
								int pq=1;
								pStatement.setInt(pq++, ttid);
								pStatement.setString(pq++,tid);
								pStatement.setInt(pq++, i+1);
								pStatement.setInt(pq++, 1);
								pStatement.setInt(pq++, j+1);
								pStatement.setInt(pq++, k);
								pStatement.addBatch();
							}
						}
					}
					//8
					for(int i=8;i<16;i++){
						for(int j=0;j<20;j++){
							for(int k=0;k<6;k++){
								int pq=1;
								pStatement.setInt(pq++, ttid);
								pStatement.setString(pq++,tid);
								pStatement.setInt(pq++, i+1);
								pStatement.setInt(pq++, 2);
								pStatement.setInt(pq++, j+1);
								pStatement.setInt(pq++, k);
								pStatement.addBatch();
							}
						}
					}
					pStatement.executeBatch();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void createTable() {
		String sql = "CREATE TABLE IF NOT EXISTS `G?` (tid VARCHAR(20) NOT NULL ," + "t_c_id TINYINT NOT NULL ,"
				+ "stype TINYINT NOT NULL DEFAULT 2," + "row INTEGER NOT NULL ,"
				+ "location TINYINT DEFAULT 0 NOT NULL ," + "ticket BIT(?) DEFAULT b?,"
				+ "PRIMARY KEY (tid,t_c_id,row,location)" + ");";
		Reader reader=new Reader();
		List<String[]> train=reader.getRoutes();
		try(PreparedStatement pStatement=getConnection().prepareStatement(sql)) {
			for (String[] strings : train) {
				String tid=strings[0];
				int ttid=Integer.parseInt(tid.substring(1));
				int len=strings.length-2;
				Double bb=Math.pow(2, len)-1;
				String hex=Integer.toBinaryString(bb.intValue());
				int j=1;
				pStatement.setInt(j++, ttid);
				pStatement.setInt(j++, len);
				pStatement.setString(j++, hex);
				pStatement.addBatch();
			}
			pStatement.executeBatch();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		}
}
