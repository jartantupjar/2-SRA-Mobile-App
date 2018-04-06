package db;

import Entity.Barangay;
import Entity.Phase;
import Entity.User;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsersDB {

    public String authenticate(String username, String password) {
        String result = "null";
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();

            String query = "select name, district from users where username = ? and password = password(?) and role = 'MDO';";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {

                User user = new User();
                user.setName(rs.getString(1));
                user.setDistrict(rs.getString(2));
                query = "select * from configuration";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()){
                    user.setDate(rs.getDate("current_date").getTime());
                    user.setYear(rs.getInt("crop_year"));
                }
                ArrayList<String> temp;
                temp = new ArrayList<>();
                query = "select * from `ref-fertilizers`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                user.setFertilizers(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-varieties`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                user.setVarieties(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-cropclass`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                user.setCropClass(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-textures`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                user.setTextures(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-topography`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                user.setTopography(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-farmingsystems`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                user.setFarmingSystems(temp);
                query = "select phase, year\n"
                        + "from crop_calendar\n"
                        + "where date_starting <= ? and date_ending >= ?;";

                pstmt = conn.prepareStatement(query);
                pstmt.setDate(1, new Date(user.getDate()));
                pstmt.setDate(2, new Date(user.getDate()));
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    ArrayList<Phase> phases = new ArrayList();
                    do {
                        Phase phase = new Phase();
                        phase.setPhase(rs.getString("phase"));
                        phase.setYear(rs.getInt("year"));
                        phases.add(phase);
                    } while (rs.next());
                    user.setPhases(phases);
                }
                result = new Gson().toJson(user);
            }

            rs.close();
            pstmt.close();
            conn.close();
          

        } catch (SQLException ex) {
            Logger.getLogger(UsersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String getBarangays(String district) {
        String result = "null";
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();

            String query = "select municipality, barangay from `Ref-Barangays` where district = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, district);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ArrayList<Barangay> barangays = new ArrayList();
                do {

                    barangays.add(new Barangay(rs.getString(1), rs.getString(2)));

                } while (rs.next());
                
                result = new Gson().toJson(barangays);
            }
            
            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(UsersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
