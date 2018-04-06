package db;

import Entity.CVS;
import Entity.Farmer;
import Entity.Fertilizer;
import Entity.Field;
import Entity.LoginResult;
import Entity.Notifications;
import Entity.Phase;
import Entity.Post;
import Entity.Problem;
import Entity.Production;
import Entity.Recommendation;
import Entity.Tiller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FarmersDB {

    public boolean checkFarmer(String name, String username) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();

            String query = "select * from farmers where name = ?;";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                pstmt.close();
                rs.close();
                conn.close();
                return false;
            }
            query = "select * from farmers where username = ?;";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                pstmt.close();
                rs.close();
                conn.close();
                return false;
            }
            pstmt.close();
            rs.close();
            conn.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean createNewFarmer(String name, String username, String password,
            String district, JsonArray fields) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "SELECT `AUTO_INCREMENT`\n"
                    + "FROM  INFORMATION_SCHEMA.TABLES\n"
                    + "WHERE TABLE_SCHEMA = 'SRA'\n"
                    + "AND   TABLE_NAME   = 'Fields';";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            rs.next();

            int id = rs.getInt("AUTO_INCREMENT");
            conn.setAutoCommit(false);
            query = "insert into farmers values (?,?,password(?), ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(4, district);
            pstmt.setString(3, password);
            if (pstmt.executeUpdate() != 1) {
                pstmt.close();
                conn.close();
                return false;
            }

            Gson gson = new Gson();
            Field field;

            query = "insert into fields (Farmers_name, barangay, municipality, district, area, boundary) values (?,?,?,?,?,?);";
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < fields.size(); i++) {
                field = gson.fromJson(fields.get(i), Field.class);
                pstmt.setString(1, name);
                pstmt.setString(2, field.getBarangay());
                pstmt.setString(3, field.getMunicipality());
                pstmt.setString(4, district);
                pstmt.setDouble(5, Math.round(field.getArea() * 100d) / 100d);
                pstmt.setString(6, field.getCoordsString());
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            query = "insert into soilanalysis values (?,?,?,?,?)";
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < fields.size(); i++) {
                field = gson.fromJson(fields.get(i), Field.class);
                pstmt.setInt(1, id);
                pstmt.setDouble(2, field.getPhLevel());
                pstmt.setDouble(3, field.getOrganicMatter());
                pstmt.setDouble(4, field.getPhosphorus());
                pstmt.setDouble(5, field.getPotassium());
                pstmt.addBatch();
                id++;
            }

            pstmt.executeBatch();

            conn.commit();
            pstmt.close();
            conn.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String authenticateFarmer(String username, String password) {
        String result = null;
        LoginResult loginResult = null;
        Farmer farmer = null;
        ArrayList<String> temp;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            conn.setAutoCommit(false);
            String query = "select * from farmers where username = ? and password = password(?);";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loginResult = new LoginResult();
                farmer = new Farmer();
                farmer.setName(rs.getString(1));
                farmer.setUsername(rs.getString(2));
                farmer.setDistrict(rs.getString(4));

                query = "select * from fields where Farmers_name = ?;";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, farmer.getName());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    ArrayList<Field> fields = new ArrayList<>();
                    do {
                        Field field = new Field();
                        field.setBarangay(rs.getString("barangay"));
                        field.setMunicipality(rs.getString("municipality"));
                        field.setDistrict(rs.getString("district"));
                        field.setCoordsString(rs.getString("boundary"));
                        field.setArea(rs.getDouble("area"));
                        field.setId(rs.getInt("id"));
                        fields.add(field);
                    } while (rs.next());
                    farmer.setFields(fields);
                }
                temp = new ArrayList<>();
                query = "select * from `ref-fertilizers`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                loginResult.setFertilizers(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-varieties`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                loginResult.setVarieties(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-cropclass`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                loginResult.setCropClass(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-textures`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                loginResult.setTextures(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-topography`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                loginResult.setTopography(temp);
                temp = new ArrayList<>();
                query = "select * from `ref-farmingsystems`";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    do {
                        temp.add(rs.getString(1));
                    } while (rs.next());
                }
                query = "select * from configuration";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    loginResult.setDate(rs.getDate("current_date"));
                    loginResult.setYear(rs.getInt("crop_year"));
                }
                loginResult.setFarmingSystems(temp);
                loginResult.setFarmer(farmer);

                query = "select phase, year\n"
                        + "from crop_calendar\n"
                        + "where date_starting <= ? and date_ending >= ?;";

                pstmt = conn.prepareStatement(query);
                pstmt.setDate(1, (Date) loginResult.getDate());
                pstmt.setDate(2, (Date) loginResult.getDate());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    ArrayList<Phase> phases = new ArrayList();
                    do {
                        Phase phase = new Phase();
                        phase.setPhase(rs.getString("phase"));
                        phase.setYear(rs.getInt("year"));
                        phases.add(phase);
                    } while (rs.next());
                    loginResult.setPhases(phases);
                }
                if (loginResult.getFarmer().getFields() != null) {
                    query = "select coalesce(sum(damage), 0) as 'damage'\n"
                            + "from `problems-fields`\n"
                            + "where Fields_id = ? and\n"
                            + "	date <= ? and\n"
                            + "	date >= (select date_starting from crop_calendar where year = ? and district = ? and phase = 'Planting')\n"
                            + "    and date <=(select date_ending from crop_calendar where year = ? and district = ? and phase = 'Milling');";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setDate(2, (Date) loginResult.getDate());
                    pstmt.setInt(3, loginResult.getYear());
                    pstmt.setInt(5, loginResult.getYear());
                    for (int i = 0; i < loginResult.getFarmer().getFields().size(); i++) {
                        pstmt.setInt(1, loginResult.getFarmer().getFields().get(i).getId());
                        pstmt.setString(4, loginResult.getFarmer().getFields().get(i).getDistrict());
                        pstmt.setString(6, loginResult.getFarmer().getFields().get(i).getDistrict());
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            loginResult.getFarmer().getFields().get(i).setDamage(rs.getDouble("damage"));
                        } else {
                            loginResult.getFarmer().getFields().get(i).setDamage(0);
                        }
                    }
                }
                ArrayList<Production> productions = new ArrayList();
                if (loginResult.getFarmer().getFields() != null) {
                    query = "select * from production where year = ? and Fields_id  = ? and date <= ?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, loginResult.getYear());
                    pstmt.setDate(3, (Date) loginResult.getDate());
                    for (int i = 0; i < loginResult.getFarmer().getFields().size(); i++) {
                        pstmt.setInt(2, loginResult.getFarmer().getFields().get(i).getId());
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            do {
                                Production tempProduction = new Production();
                                tempProduction.setId(rs.getInt(1));
                                tempProduction.setYear(rs.getInt(2));
                                tempProduction.setFieldsID(rs.getInt(3));
                                tempProduction.setAreaHarvested(rs.getDouble(4));
                                tempProduction.setTonsCane(rs.getDouble(5));
                                tempProduction.setLkg(rs.getDouble(6));
                                tempProduction.setDate(rs.getDate(7));
                                productions.add(tempProduction);
                            } while (rs.next());
                        }
                    }
                }
                if (!productions.isEmpty()) {
                    loginResult.setProductions(productions);
                }
                ArrayList<CVS> cvs = new ArrayList<>();
                if (loginResult.getFarmer().getFields() != null) {
                    query = "select * from cropvalidationsurveys where year = ? and Fields_id = ?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, loginResult.getYear());
                    for (int i = 0; i < loginResult.getFarmer().getFields().size(); i++) {
                        pstmt.setInt(2, loginResult.getFarmer().getFields().get(i).getId());
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            do {
                                CVS tempCVS = new CVS();
                                tempCVS.setYear(rs.getInt(1));
                                tempCVS.setField(rs.getInt(2));
                                tempCVS.setVariety(rs.getString(3));
                                tempCVS.setCropClass(rs.getString(4));
                                tempCVS.setTexture(rs.getString(5));
                                tempCVS.setFarmingSystem(rs.getString(6));
                                tempCVS.setTopography(rs.getString(7));
                                tempCVS.setFurrowDistance(rs.getDouble(8));
                                tempCVS.setPlantingDensity(rs.getDouble(9));
                                tempCVS.setNumMillable(rs.getInt(10));
                                tempCVS.setAvgMillableStool(rs.getDouble(11));
                                tempCVS.setBrix(rs.getDouble(12));
                                tempCVS.setStalkLength(rs.getDouble(13));
                                tempCVS.setDiameter(rs.getDouble(14));
                                tempCVS.setWeight(rs.getDouble(15));
                                cvs.add(tempCVS);
                            } while (rs.next());
                        }
                    }
                }
                if (!cvs.isEmpty()) {
                    loginResult.setCvs(cvs);
                }
                ArrayList<Fertilizer> fertilizersSubmitted = new ArrayList<>();
                if (loginResult.getFarmer().getFields() != null) {
                    query = "select * from fertilizers where year = ? and Fields_id = ?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, loginResult.getYear());
                    for (int i = 0; i < loginResult.getFarmer().getFields().size(); i++) {
                        pstmt.setInt(2, loginResult.getFarmer().getFields().get(i).getId());
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            do {
                                Fertilizer fertilizer = new Fertilizer();
                                fertilizer.setYear(rs.getInt(1));
                                fertilizer.setFieldID(rs.getInt(2));
                                fertilizer.setFertilizer(rs.getString(3));
                                fertilizer.setFirstDose(rs.getDouble(4));
                                fertilizer.setSecondDose(rs.getDouble(5));
                                fertilizersSubmitted.add(fertilizer);
                            } while (rs.next());
                        }
                    }
                }
                loginResult.setFertilizersSubmitted(fertilizersSubmitted);
                ArrayList<Tiller> tillers = new ArrayList<>();
                if (loginResult.getFarmer().getFields() != null) {
                    query = "select * from tillers where year = ? and Fields_id = ?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, loginResult.getYear());
                    for (int i = 0; i < loginResult.getFarmer().getFields().size(); i++) {
                        pstmt.setInt(2, loginResult.getFarmer().getFields().get(i).getId());
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            do {
                                Tiller tiller = new Tiller();
                                tiller.setYear(rs.getInt(1));
                                tiller.setFieldID(rs.getInt(2));
                                tiller.setRep(rs.getInt(3));
                                tiller.setCount(rs.getInt(4));
                                tillers.add(tiller);
                            } while (rs.next());
                        }
                    }
                }
                if (!tillers.isEmpty()) {
                    loginResult.setTillers(tillers);
                }
                ArrayList<Problem> problems = new ArrayList<>();
                if (loginResult.getFarmer().getFields() != null) {
                    query = "select pf.id, pf.problems_id, pf.fields_id, pf.date, pf.status, pf.damage, p.name, p.description, p.type, pf.status\n"
                            + "					from `problems-fields` pf join problems p on pf.problems_id = p.id\n"
                            + "                    where pf.Fields_id = ? and pf.date <= ? and (pf.status = 'active' or pf.status = 'pending')";
                    pstmt = conn.prepareStatement(query);
                    for (int i = 0; i < loginResult.getFarmer().getFields().size(); i++) {
                        pstmt.setInt(1, loginResult.getFarmer().getFields().get(i).getId());
                        pstmt.setDate(2, (Date) loginResult.getDate());
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            do {
                                Problem problem = new Problem();
                                problem.setName(rs.getString("name"));
                                problem.setId(rs.getInt("id"));
                                problem.setProblemsID(rs.getInt("problems_id"));
                                problem.setFieldsID(rs.getInt("fields_id"));
                                problem.setDate(rs.getDate("date"));
                                problem.setStatus(rs.getString("status"));
                                problem.setDamage(rs.getDouble("damage"));
                                problem.setName(rs.getString("name"));
                                problem.setDescription(rs.getString("description"));
                                problem.setType(rs.getString("type"));
                                problem.setStatus(rs.getString("status"));
                                problems.add(problem);
                            } while (rs.next());
                        }
                    }
                }
                if (!problems.isEmpty()) {
                    loginResult.setProblems(problems);
                }
                ArrayList<Recommendation> recommendations = new ArrayList<>();
                if (loginResult.getFarmer().getFields() != null) {
                    query = "select r.id, rf.Fields_id, rf.status, rf.date, r.recommendation, r.type, r.description, r.improvement, r.phase, rf.duration_days\n"
                            + "from `recommendations-fields` rf join recommendations r on rf.Recommendations_id = r.id\n"
                            + "where rf.Fields_id = ? and rf.date <= ? and (rf.status = 'active' or rf.status = 'verifying')";
                    pstmt = conn.prepareStatement(query);
                    for (int i = 0; i < loginResult.getFarmer().getFields().size(); i++) {
                        pstmt.setInt(1, loginResult.getFarmer().getFields().get(i).getId());
                        pstmt.setDate(2, (Date) loginResult.getDate());
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            do {
                                Recommendation recommendation = new Recommendation();
                                recommendation.setId(rs.getInt("id"));
                                recommendation.setFieldID(rs.getInt("Fields_id"));
                                recommendation.setStatus(rs.getString("status"));
                                recommendation.setDate(rs.getDate("date"));
                                recommendation.setType(rs.getString("type"));
                                recommendation.setDescription(rs.getString("description"));
                                recommendation.setImprovement(rs.getString("improvement").charAt(0));
                                recommendation.setPhase(rs.getString("phase"));
                                recommendation.setDurationDays(rs.getInt("duration_days"));
                                recommendation.setRecommendation(rs.getString("recommendation"));
                                recommendations.add(recommendation);
                            } while (rs.next());
                        }
                    }
                }
                if (!recommendations.isEmpty()) {
                    loginResult.setRecommendations(recommendations);
                }
            }
            result = new Gson().toJson(loginResult);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;

    }

    public void updateCVS(CVS cvs) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "insert into cropvalidationsurveys(year, Fields_id, variety, crop_class, texture, farming_system, topography, furrow_distance, planting_density) "
                    + "values (?,?,?,?,?,?,?,?,?)"
                    + "on duplicate key update "
                    + " variety = ?, "
                    + "crop_class = ?, "
                    + "texture = ?, "
                    + "farming_system = ?, "
                    + "topography = ?, "
                    + "furrow_distance = ?, "
                    + "planting_density = ?;";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, cvs.getYear());
            pstmt.setInt(2, cvs.getField());
            pstmt.setString(3, cvs.getVariety());
            pstmt.setString(4, cvs.getCropClass());
            pstmt.setString(5, cvs.getTexture());
            pstmt.setString(6, cvs.getFarmingSystem());
            pstmt.setString(7, cvs.getTopography());
            pstmt.setDouble(8, cvs.getFurrowDistance());
            pstmt.setDouble(9, cvs.getPlantingDensity());

            pstmt.setString(10, cvs.getVariety());
            pstmt.setString(11, cvs.getCropClass());
            pstmt.setString(12, cvs.getTexture());
            pstmt.setString(13, cvs.getFarmingSystem());
            pstmt.setString(14, cvs.getTopography());
            pstmt.setDouble(15, cvs.getFurrowDistance());
            pstmt.setDouble(16, cvs.getPlantingDensity());

            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateFertilizer(Fertilizer fertilizer) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "insert into fertilizers values (?,?,?,?,?)"
                    + "on duplicate key update "
                    + "first_dose = ?, "
                    + "second_dose = ?;";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, fertilizer.getYear());
            pstmt.setInt(2, fertilizer.getFieldID());
            pstmt.setString(3, fertilizer.getFertilizer());
            pstmt.setDouble(4, fertilizer.getFirstDose());
            pstmt.setDouble(5, fertilizer.getSecondDose());

            pstmt.setDouble(6, fertilizer.getFirstDose());
            pstmt.setDouble(7, fertilizer.getSecondDose());
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTillers(ArrayList<Tiller> tillers) {

        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            conn.setAutoCommit(false);
            String query = "insert into tillers values (?,?,?,?) "
                    + "on duplicate key update "
                    + "year = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 0; i < tillers.size(); i++) {
                pstmt.setInt(1, tillers.get(i).getYear());
                pstmt.setInt(2, tillers.get(i).getFieldID());
                pstmt.setInt(3, tillers.get(i).getRep());
                pstmt.setInt(4, tillers.get(i).getCount());
                pstmt.setInt(5, tillers.get(i).getYear());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addUpload(CVS cvs) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "update cropvalidationsurveys set "
                    + "num_millable = ?, avg_millable_stool = ?, brix = ?, stalk_length = ?, diameter = ?, weight = ?"
                    + " where year = ? and Fields_id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, cvs.getNumMillable());
            pstmt.setDouble(2, cvs.getAvgMillableStool());
            pstmt.setDouble(3, cvs.getBrix());
            pstmt.setDouble(4, cvs.getStalkLength());
            pstmt.setDouble(5, cvs.getDiameter());
            pstmt.setDouble(6, cvs.getWeight());
            pstmt.setInt(7, cvs.getYear());
            pstmt.setInt(8, cvs.getField());
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String sendProduction(Production production) {
        String result = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "insert into production(year, Fields_id, area_harvested, tons_cane, lkg, date) values (?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, production.getYear());
            pstmt.setInt(2, production.getFieldsID());
            pstmt.setDouble(3, production.getAreaHarvested());
            pstmt.setDouble(4, production.getTonsCane());
            pstmt.setDouble(5, production.getLkg());
            pstmt.setDate(6, new Date(production.getDate().getTime()));
            int rows = pstmt.executeUpdate();
            if (rows == 1) {
                query = "select * from production where year = ? and Fields_id = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, production.getYear());
                pstmt.setInt(2, production.getFieldsID());
                ResultSet rs = pstmt.executeQuery();
                ArrayList<Production> productions = null;
                if (rs.next()) {
                    productions = new ArrayList();
                    do {
                        Production tempProduction = new Production();
                        tempProduction.setId(rs.getInt(1));
                        tempProduction.setYear(rs.getInt(2));
                        tempProduction.setFieldsID(rs.getInt(3));
                        tempProduction.setAreaHarvested(rs.getDouble(4));
                        tempProduction.setTonsCane(rs.getDouble(5));
                        tempProduction.setLkg(rs.getDouble(6));
                        tempProduction.setDate(rs.getDate(7));
                        productions.add(tempProduction);
                    } while (rs.next());
                }
                result = new Gson().toJson(productions);
                rs.close();
            }
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public ArrayList<Recommendation> getRecommendationsByProblem(int problemID) {
        ArrayList<Recommendation> recommendations = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select * from recommendations where id in "
                    + "(select Recommendations_id from `Recommendations-Problems` where Problems_id = ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, problemID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                recommendations = new ArrayList<>();
                do {
                    Recommendation recommendation = new Recommendation();
                    recommendation.setId(rs.getInt("id"));
                    recommendation.setRecommendation(rs.getString("recommendation"));
                    recommendation.setType(rs.getString("type"));
                    recommendation.setDescription(rs.getString("description"));
                    recommendation.setImprovement(rs.getString("type").charAt(0));
                    recommendations.add(recommendation);
                } while (rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);

        }
        return recommendations;
    }

    public String addRecommendations(ArrayList<Recommendation> recommendations, int problemID, int fieldID) {
        ArrayList<Recommendation> newRecommendations = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            conn.setAutoCommit(false);
            String query = "insert into `recommendations-fields`(Recommendations_id, Fields_id, status, date)"
                    + " values (?, ?, 'Active', ?)\n"
                    + "                    on duplicate key update status = 'Active', date = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 0; i < recommendations.size(); i++) {
                pstmt.setInt(1, recommendations.get(i).getId());
                pstmt.setInt(2, fieldID);
                pstmt.setDate(3, new Date(recommendations.get(i).getDate().getTime()));
                pstmt.setDate(4, new Date(recommendations.get(i).getDate().getTime()));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            query = "update `Problems-Fields` set status = 'Solved' where id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, problemID);
            pstmt.executeUpdate();
            conn.commit();
            query = "select r.id, rf.Fields_id, rf.status, rf.date, r.recommendation, r.type, r.description, r.improvement, r.phase, rf.duration_days\n"
                    + "from `recommendations-fields` rf join recommendations r on rf.Recommendations_id = r.id\n"
                    + "where rf.Fields_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, fieldID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                newRecommendations = new ArrayList<>();
                do {
                    Recommendation recommendation = new Recommendation();
                    recommendation.setId(rs.getInt("id"));
                    recommendation.setFieldID(rs.getInt("Fields_id"));
                    recommendation.setStatus(rs.getString("status"));
                    recommendation.setDate(rs.getDate("date"));
                    recommendation.setType(rs.getString("type"));
                    recommendation.setDescription(rs.getString("description"));
                    recommendation.setImprovement(rs.getString("improvement").charAt(0));
                    recommendation.setPhase(rs.getString("phase"));
                    recommendation.setDurationDays(rs.getInt("duration_days"));
                    recommendation.setRecommendation(rs.getString("recommendation"));
                    newRecommendations.add(recommendation);
                } while (rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);

        }
        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
        return gson.toJson(newRecommendations);
    }

    public String checkForUpdates(String farmer) {
        Notifications notifications = new Notifications();
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select * from configuration";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                notifications.setDate(new Date(rs.getDate("current_date").getTime()));
                notifications.setYear(rs.getInt("crop_year"));
            }

            query = "select phase, year\n"
                    + "from crop_calendar\n"
                    + "where date_starting <= ? and date_ending >= ?;";

            pstmt = conn.prepareStatement(query);
            pstmt.setDate(1, new Date(notifications.getDate().getTime()));
            pstmt.setDate(2, new Date(notifications.getDate().getTime()));
            rs = pstmt.executeQuery();
            ArrayList<Phase> phases = new ArrayList();
            if (rs.next()) {
                do {
                    Phase phase = new Phase();
                    phase.setPhase(rs.getString("phase"));
                    phase.setYear(rs.getInt("year"));
                    phases.add(phase);
                } while (rs.next());
                notifications.setPhases(phases);
            }
            query = "select id from fields where farmers_name = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, farmer);
            rs = pstmt.executeQuery();
            ArrayList<Integer> fieldID = new ArrayList();
            if (rs.next()) {
                do {
                    fieldID.add(rs.getInt("id"));
                } while (rs.next());
            }
            ArrayList<Recommendation> newRecommendations = new ArrayList();
            query = "select * \n"
                    + "from `recommendations-fields`\n"
                    + "where (status = 'inactive') \n"
                    + "and recommendations_id in (select id from recommendations where phase = ?) \n"
                    + "and fields_id = ?\n"
                    + "and date >= (select date_starting from crop_calendar where year = ? and phase = ?) \n"
                    + "and date <= (select date_ending from crop_calendar where year = ? and phase = ?)\n"
                    + "and date <= ?;";
            for (int i = 0; i < phases.size(); i++) {
                for (int j = 0; j < fieldID.size(); j++) {
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, phases.get(i).getPhase());
                    pstmt.setInt(2, fieldID.get(j));
                    pstmt.setInt(3, notifications.getYear());
                    pstmt.setString(4, phases.get(i).getPhase());
                    pstmt.setInt(5, notifications.getYear());
                    pstmt.setString(6, phases.get(i).getPhase());
                    pstmt.setDate(7, notifications.getDate());
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        do {
                            Recommendation recommendation = new Recommendation();
                            recommendation.setId(rs.getInt("recommendations_id"));
                            recommendation.setFieldID(rs.getInt("Fields_id"));
                            recommendation.setDate(new Date(Calendar.getInstance().getTimeInMillis()));
                            newRecommendations.add(recommendation);
                        } while (rs.next());
                    }
                }
            }
            query = "select *\n"
                    + "from recommendations where\n"
                    + "phase = ?\n"
                    + "and id \n"
                    + "not in (select recommendations_id from `recommendations-fields` where fields_id = ?);";
            for (int i = 0; i < phases.size(); i++) {
                for (int j = 0; j < fieldID.size(); j++) {
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, phases.get(i).getPhase());
                    pstmt.setInt(2, fieldID.get(j));
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        do {
                            Recommendation recommendation = new Recommendation();
                            recommendation.setId(rs.getInt("id"));
                            recommendation.setFieldID(fieldID.get(j));
                            recommendation.setDate(new Date(Calendar.getInstance().getTimeInMillis()));
                            newRecommendations.add(recommendation);
                        } while (rs.next());
                    }
                }
            }
            //conn.setAutoCommit(false);
            query = "select n.id, n.message, n.Fields_id, n.`Problems-Fields_id`, pf.Problems_id, p.type, p.name, p.description, pf.status, p.phase, pf.date\n"
                    + "from notifications n join `Problems-Fields` pf on n.`Problems-Fields_id` = pf.id join Problems p on pf.Problems_id = p.id\n"
                    + "where n.received ='N' and n.disaster = 'N' and n.`Problems-Fields_id` is not null and n.Fields_id in (select id from fields where farmers_name = ?);";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, farmer);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ArrayList<Problem> problems = new ArrayList<>();
                do {
                    Problem problem = new Problem();
                    problem.setNotificationID(rs.getInt("id"));
                    problem.setMessage(rs.getString("message"));
                    problem.setFieldsID(rs.getInt("Fields_id"));
                    problem.setId(rs.getInt("Problems-Fields_id"));
                    problem.setProblemsID(rs.getInt("Problems_id"));
                    problem.setType(rs.getString("type"));
                    problem.setName(rs.getString("name"));
                    problem.setDescription(rs.getString("description"));
                    problem.setStatus(rs.getString("status"));
                    problem.setPhase(rs.getString("phase"));
                    problem.setDate(rs.getDate("date"));
                    problems.add(problem);
                } while (rs.next());
                notifications.setProblems(problems);
                query = "update notifications set received = 'Y' where id = ?";
                pstmt = conn.prepareStatement(query);
                for (int i = 0; i < problems.size(); i++) {
                    pstmt.setInt(1, problems.get(i).getNotificationID());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            query = "select n.id, n.message, n.Fields_id, n.Recommendations_id, rf.status, rf.date, rf.duration_days, r.recommendation, r.type, r.description, r.phase\n"
                    + "from notifications n join `Recommendations-Fields` rf on n.Recommendations_id = rf.Recommendations_id and n.Recommendations_Fields_id = rf.Fields_id \n"
                    + "		join Recommendations r on rf.Recommendations_id = r.id\n"
                    + "where n.received ='N' and n.disaster = 'N' and n.Recommendations_id is not null and n.Fields_id in (select id from fields where farmers_name = ?);";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, farmer);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ArrayList<Recommendation> recommendations = new ArrayList<>();
                do {
                    Recommendation recommendation = new Recommendation();
                    recommendation.setNotificationID(rs.getInt("id"));
                    recommendation.setFieldID(rs.getInt("Fields_id"));
                    recommendation.setId(rs.getInt("Recommendations_id"));
                    recommendation.setDurationDays(rs.getInt("duration_days"));
                    recommendation.setMessage(rs.getString("message"));
                    recommendation.setStatus(rs.getString("status"));
                    recommendation.setRecommendation(rs.getString("recommendation"));
                    recommendation.setType(rs.getString("type"));
                    recommendation.setDescription(rs.getString("description"));
                    recommendation.setPhase(rs.getString("phase"));
                    recommendation.setDate(rs.getDate("date"));
                    recommendations.add(recommendation);
                } while (rs.next());
                notifications.setRecommendations(recommendations);
                query = "update notifications set received = 'Y' where id = ?";
                pstmt = conn.prepareStatement(query);
                for (int i = 0; i < recommendations.size(); i++) {
                    pstmt.setInt(1, recommendations.get(i).getNotificationID());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            query = "select * \n"
                    + "from `recommendations-fields`\n"
                    + "where fields_id in (select id from Fields where Farmers_name = ?) and status = 'active'\n"
                    + "and notified ='N'\n"
                    + "and date_add(date, interval duration_days day) >= (select current_date from configuration)\n"
                    + ";";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, farmer);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("duration_days") == 0) {
                    query = "update `recommendations-fields` set duration_days = null where fields_id = ? and recommendations_id = ?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, rs.getInt("fields_id"));
                    pstmt.setInt(2, rs.getInt("recommendations_id"));
                    pstmt.executeUpdate();
                } else {
                    ArrayList<Recommendation> recommendations = new ArrayList<>();
                    do {
                        Recommendation recommendation = new Recommendation();
                        recommendation.setFieldID(rs.getInt("Fields_id"));
                        recommendation.setId(rs.getInt("Recommendations_id"));
                        recommendation.setMessage("Please recheck status of recommendation #: " + recommendation.getId());
                        recommendation.setDate(new Date(Calendar.getInstance().getTimeInMillis()));
                        recommendations.add(recommendation);
                    } while (rs.next());
                    notifications.setReminders(recommendations);
                    query = "update `recommendations-fields` set notified = 'Y' where fields_id in (select id from Fields where Farmers_name = ?) and status = 'active'\n"
                            + "and notified ='N'\n"
                            + "and date_add(date, interval duration_days day) >= (select current_date from configuration);";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, farmer);
                    pstmt.executeUpdate();
                }
            }

            query = "select n.id, n.message, p.message, n.Fields_id, n.Posts_id, p.title, p.date_posted, "
                    + "p.problems_id, p.recommendations_id, p.status\n"
                    + "from notifications n join posts p on n.Posts_id = p.id\n"
                    + "where n.received ='N' and n.disaster = 'N' and n.Posts_id is not null and n.Fields_id in (select id from fields where farmers_name = ?) and n.Recommendations_id = null;";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, farmer);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ArrayList<Post> posts = new ArrayList<>();
                do {
                    Post post = new Post();
                    post.setNotificationID(rs.getInt("id"));
                    post.setMessage2(rs.getString(2));
                    post.setMessage(rs.getString(3));
                    post.setProblemID(rs.getInt("problems_id"));
                    post.setRecommendationID(rs.getInt("Recommendations_id"));
                    post.setStatus(rs.getString("status"));
                    post.setDatePosted(rs.getDate("date_posted"));
                    post.setFieldID(rs.getInt("Fields_id"));
                    post.setTitle(rs.getString("title"));
                    post.setId(rs.getInt("Posts_id"));
                    posts.add(post);
                } while (rs.next());
                notifications.setPosts(posts);
                query = "update notifications set received = 'Y' where id = ?";
                pstmt = conn.prepareStatement(query);
                for (int i = 0; i < posts.size(); i++) {
                    pstmt.setInt(1, posts.get(i).getNotificationID());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            query = "select f.id as'Field_id', da.id as 'DisasterAlerts_id', da.message, da.problems_id, da.date, p.name, p.type ,p.description\n"
                    + "from fields f join disasteralerts da on f.barangay = da.barangay and f.municipality = da.municipality and f.district = da.district\n"
                    + "		join Problems p on da.Problems_id = p.id\n"
                    + "where f.Farmers_name = ?\n"
                    + "and (f.id, da.id) not in (select Fields_id, DisasterAlerts_id from notifications where disaster = 'Y');";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, farmer);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ArrayList<Problem> disasters = new ArrayList<>();
                do {
                    Problem disaster = new Problem();
                    disaster.setFieldsID(rs.getInt("Field_id"));
                    disaster.setDisasterAlertID(rs.getInt("DisasterAlerts_id"));
                    disaster.setMessage(rs.getString("message"));
                    disaster.setProblemsID(rs.getInt("problems_id"));
                    disaster.setDate(rs.getDate("date"));
                    disaster.setName(rs.getString("name"));
                    disaster.setDescription(rs.getString("description"));
                    disaster.setType(rs.getString("type"));
                    disasters.add(disaster);
                } while (rs.next());
                notifications.setDisasters(disasters);
                query = "insert into notifications(disaster, received, Fields_id, date, DisasterAlerts_id) "
                        + "values ('Y', 'Y', ?, curdate(), ?)";
                pstmt = conn.prepareStatement(query);
                for (int i = 0; i < disasters.size(); i++) {
                    pstmt.setInt(1, disasters.get(i).getFieldsID());
                    pstmt.setInt(2, disasters.get(i).getDisasterAlertID());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            query = "insert into `recommendations-fields`(recommendations_id, fields_id, status, date) values (?,?,'Verifying',?)\n"
                    + "on duplicate key update status = 'Verifying' and date = ?;";
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < newRecommendations.size(); i++) {
                pstmt.setInt(1, newRecommendations.get(i).getId());
                pstmt.setInt(2, newRecommendations.get(i).getFieldID());
                pstmt.setDate(3, notifications.getDate());
                pstmt.setDate(4, notifications.getDate());
                pstmt.addBatch();
            }
            if (newRecommendations.size() > 0) {
                pstmt.executeBatch();
            }

            query = "insert into notifications(disaster, Fields_id, message, date, Recommendations_id, Recommendations_Fields_id)\n"
                    + "values ('N', ?, 'A fixed recommendation is sent.',?,?,?);";
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < newRecommendations.size(); i++) {
                pstmt.setInt(1, newRecommendations.get(i).getFieldID());
                pstmt.setDate(2, notifications.getDate());
                pstmt.setInt(3, newRecommendations.get(i).getId());
                pstmt.setInt(4, newRecommendations.get(i).getFieldID());
                pstmt.addBatch();
            }
            if (newRecommendations.size() > 0) {
                pstmt.executeBatch();
            }
            //conn.commit();
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);

        }
        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
        return gson.toJson(notifications);
    }

    public void updateProblemStatus(int id, int decision) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "update `Problems-Fields` set status = ? where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            if (decision == 1) {
                pstmt.setString(1, "Active");
            } else {
                pstmt.setString(1, "Inactive");
            }
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Problem getDisasterSurvey(int problemID) {
        Problem problem = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select * from problems where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, problemID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                problem = new Problem();
                problem.setProblemsID(rs.getInt("id"));
                problem.setName(rs.getString("name"));
                problem.setDescription(rs.getString("description"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return problem;
    }

    public void submitDisasterSurvey(int fieldID, int problemID, double damage, Date date) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "insert into `Problems-Fields`(Problems_id, Fields_id, date, status, damage) "
                    + "values (?,?, ?,'Inactive',?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, problemID);
            pstmt.setInt(2, fieldID);
            pstmt.setDouble(4, damage);
            pstmt.setDate(3, date);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateRecommendationStatus(int recommendationID, int fieldID, int decision, Date date) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "update `Recommendations-Fields` set status = ?, date = ? where Recommendations_id = ? and Fields_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            if (decision == 1) {
                pstmt.setString(1, "Active");
            } else {
                pstmt.setString(1, "Inactive");
            }
            pstmt.setDate(2, date);
            pstmt.setInt(3, recommendationID);
            pstmt.setInt(4, fieldID);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Problem> getProblems(int fieldsID) {
        ArrayList<Problem> problems = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select id, name, description\n"
                    + "from problems\n"
                    + "where type !='subjective' and id not in (select Problems_id from `Problems-Fields` where Fields_id = ? and status = 'active')";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, fieldsID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                problems = new ArrayList();
                do {
                    Problem problem = new Problem();
                    problem.setProblemsID(rs.getInt("id"));
                    problem.setName(rs.getString("name"));
                    problem.setDescription(rs.getString("description"));
                    problems.add(problem);
                } while (rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return problems;
    }

    public ArrayList<Problem> getSubmittedDisasterReports(int fieldID) {
        ArrayList<Problem> problems = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select p.name, p.description, pf.id, pf.date, pf.damage\n"
                    + "from problems p join `problems-fields` pf on p.id = pf.Problems_id\n"
                    + "where p.type != 'subjective'\n"
                    + "	and pf.Fields_id =?\n"
                    + "    and pf.date <= (select current_date from configuration)\n"
                    + "    and pf.status = 'Inactive';";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, fieldID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                problems = new ArrayList();
                do {
                    Problem problem = new Problem();
                    problem.setProblemsID(rs.getInt("id"));
                    problem.setName(rs.getString("name"));
                    problem.setDescription(rs.getString("description"));
                    problem.setDate(rs.getDate("date"));
                    problem.setDamage(rs.getDouble("damage"));
                    problems.add(problem);
                } while (rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return problems;
    }

    public ArrayList<Production> getProduction(int fieldID) {
        ArrayList<Production> productionList = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select *\n"
                    + "from production\n"
                    + "where Fields_id = ?\n"
                    + "	and year = (select crop_year from configuration)\n"
                    + "    and date <= (select current_date from configuration)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, fieldID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                productionList = new ArrayList();
                do{
                    Production production = new Production();
                    production.setAreaHarvested(rs.getDouble("area_harvested"));
                    production.setTonsCane(rs.getDouble("tons_cane"));
                    production.setLkg(rs.getDouble("lkg"));
                    production.setDate(rs.getDate("date"));
                    productionList.add(production);
                }while(rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FarmersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productionList;
    }

}
