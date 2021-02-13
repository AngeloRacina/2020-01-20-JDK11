package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Exhibition;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void loadAllArtist(Map<Integer, Artist> idMap){
		String sql = "Select * from artists";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Artist a = new Artist(res.getInt("artist_id"), res.getString("name"));
				if(!idMap.containsKey(a.getId())){
					idMap.put(a.getId(), a);
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public List<Exhibition> listExhibitions() {
		
		String sql = "SELECT * from exhibitions";
		List<Exhibition> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Exhibition exObj = new Exhibition(res.getInt("exhibition_id"), res.getString("exhibition_department"), res.getString("exhibition_title"), 
						res.getInt("begin"), res.getInt("end"));
				
				result.add(exObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getRoles(){
		
		List<String> roles = new ArrayList<String>();
		String sql = "SELECT DISTINCT role FROM authorship";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {				
				roles.add(res.getString("role"));
			}
			conn.close();
			return roles;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Artist> getArtists(String role){
		List<Artist> result = new ArrayList<Artist>();
		String sql = "SELECT distinct a.* FROM artists AS a, authorship AS aa " + 
				"WHERE a.artist_id = aa.artist_id AND aa.role = ? ";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			ResultSet res = st.executeQuery();
			while (res.next()) {	
				Artist a =new Artist(res.getInt("artist_id"), res.getString("name"));
				result.add(a);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getAdiacenze(String role, Map<Integer, Artist> idMap){
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		String sql ="SELECT a1.artist_id AS id1, a2.artist_id AS id2, COUNT(DISTINCT eo1.exhibition_id) " + 
				" AS peso " + 
				"FROM authorship AS a1, authorship AS a2," + 
				" exhibition_objects AS eo1, exhibition_objects AS eo2" + 
				" WHERE a1.artist_id > a2.artist_id AND " + 
				" a1.object_id = eo1.object_id AND " + 
				" a2.object_id = eo2.object_id AND " + 
				" eo1.exhibition_id = eo2.exhibition_id AND " + 
				" a1.role = ? AND a1.role = a2.role " + 
				" GROUP BY id1, id2";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			ResultSet res = st.executeQuery();
			while (res.next()) {	
				if(idMap.containsKey(res.getInt("id1")) && idMap.containsKey(res.getInt("id1"))){
					result.add(new Adiacenza(idMap.get(res.getInt("id1")), idMap.get(res.getInt("id1")), res.getInt("peso") ));
				}
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
