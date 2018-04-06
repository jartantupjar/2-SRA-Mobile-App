package db;

import Entity.CVS;
import Entity.Fertilizer;
import Entity.Field;
import Entity.Tiller;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MDODB {

    public ArrayList<Field> getFieldsByBarangay(String district, String municipality, String barangay) {
        ArrayList<Field> fields = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select *\n"
                    + "from fields\n"
                    + "where barangay = ? and municipality = ? and district = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, barangay);
            pstmt.setString(2, municipality);
            pstmt.setString(3, district);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                fields = new ArrayList();
                do {
                    Field field = new Field();
                    field.setId(rs.getInt("id"));
                    field.setCoordsString(rs.getString("boundary"));
                    fields.add(field);
                } while (rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MDODB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fields;
    }

    public Field getField(int fieldID, int year) {
        Field field = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select * from fields where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, fieldID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                field = new Field();
                field.setId(rs.getInt("id"));
                field.setFarmer(rs.getString("Farmers_name"));
                field.setBarangay(rs.getString("barangay"));
                field.setMunicipality(rs.getString("municipality"));
                field.setDistrict(rs.getString("district"));
                field.setArea(rs.getDouble("area"));
                field.setCoordsString(rs.getString("boundary"));
                query = "select * from cropvalidationsurveys where year = ? and fields_id = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, year);
                pstmt.setInt(2, fieldID);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    CVS cvs = new CVS();
                    cvs.setYear(rs.getInt(1));
                    cvs.setField(rs.getInt(2));
                    cvs.setVariety(rs.getString(3));
                    cvs.setCropClass(rs.getString(4));
                    cvs.setTexture(rs.getString(5));
                    cvs.setFarmingSystem(rs.getString(6));
                    cvs.setTopography(rs.getString(7));
                    cvs.setFurrowDistance(rs.getDouble(8));
                    cvs.setPlantingDensity(rs.getDouble(9));
                    cvs.setNumMillable(rs.getInt(10));
                    cvs.setAvgMillableStool(rs.getDouble(11));
                    cvs.setBrix(rs.getDouble(12));
                    cvs.setStalkLength(rs.getDouble(13));
                    cvs.setDiameter(rs.getDouble(14));
                    cvs.setWeight(rs.getDouble(15));
                    field.setCvs(cvs);
                }
                query = "select * from fertilizers where year = ? and Fields_id = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, year);
                pstmt.setInt(2, fieldID);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    ArrayList<Fertilizer> fertilizers = new ArrayList();
                    do {
                        Fertilizer fertilizer = new Fertilizer();
                        fertilizer.setYear(rs.getInt(1));
                        fertilizer.setFieldID(rs.getInt(2));
                        fertilizer.setFertilizer(rs.getString(3));
                        fertilizer.setFirstDose(rs.getDouble(4));
                        fertilizer.setSecondDose(rs.getDouble(5));
                        fertilizers.add(fertilizer);
                    } while (rs.next());
                    field.setFertilizers(fertilizers);
                }
                query = "select * from tillers where fields_id = ? and year = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, fieldID);
                pstmt.setInt(2, year);
                rs = pstmt.executeQuery();
                if (rs.next()){
                    ArrayList<Tiller> tillers = new ArrayList();
                    do{
                        Tiller tiller = new Tiller();
                        tiller.setYear(rs.getInt("year"));
                        tiller.setFieldID(rs.getInt("fields_id"));
                        tiller.setRep(rs.getInt("rep"));
                        tiller.setCount(rs.getInt("count"));
                        tillers.add(tiller);
                    }while(rs.next());
                    field.setTillers(tillers);
                }
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MDODB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return field;
    }

}
