package db;

import Entity.Comment;
import Entity.Post;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForumDB {

    public ArrayList<Post> getForumPosts(int pageNum, Date date) {
        ArrayList<Post> posts = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select p.id, p.title, p.message, p.Problems_id, p.status, p.date_posted, fa.name\n"
                    + "from posts p join fields f on p.Fields_id = f.id join farmers fa on fa.name = f.Farmers_name\n"
                    + "where p.status = 'accepted' and p.date_posted <= ? \n"
                    + "order by p.date_posted\n"
                    + "limit 10\n"
                    + "offset ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(2, (pageNum - 1) * 10);
            pstmt.setDate(1, date);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                posts = new ArrayList();
                do {
                    Post post = new Post();
                    post.setId(rs.getInt("id"));
                    post.setProblemID(rs.getInt("Problems_id"));
                    post.setTitle(rs.getString("title"));
                    post.setMessage(rs.getString("message"));
                    post.setStatus(rs.getString("status"));
                    post.setFarmersName(rs.getString("name"));
                    post.setDatePosted(rs.getDate("date_posted"));
                    posts.add(post);
                } while (rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ForumDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return posts;
    }

    public ArrayList<Post> searchForumPost(String search, int pageNum, Date date) {

        ArrayList<Post> posts = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select p.id, p.title, p.Problems_id, p.message, p.status, p.date_posted, fa.name\n"
                    + "from posts p join fields f on p.Fields_id = f.id join farmers fa on fa.name = f.Farmers_name\n"
                    + "where p.status = 'accepted' and p.date_posted <= ? and match(p.title, p.message) against (?)\n"
                    + "order by p.date_posted\n"
                    + "limit 10\n"
                    + "offset ?;";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setDate(1, date);
            pstmt.setString(2, search);
            pstmt.setInt(3, (pageNum - 1) * 10);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                posts = new ArrayList();
                do {
                    Post post = new Post();
                    post.setId(rs.getInt("id"));
                    post.setProblemID(rs.getInt("Problems_id"));
                    post.setTitle(rs.getString("title"));
                    post.setMessage(rs.getString("message"));
                    post.setStatus(rs.getString("status"));
                    post.setFarmersName(rs.getString("name"));
                    post.setDatePosted(rs.getDate("date_posted"));
                    posts.add(post);
                } while (rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ForumDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return posts;
    }

    public void submitPost(String title, String message, int fieldID, ArrayList<String> imagePath, Date date, String phase) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            conn.setAutoCommit(false);
            String query = "SELECT `AUTO_INCREMENT`\n"
                    + "FROM  INFORMATION_SCHEMA.TABLES\n"
                    + "WHERE TABLE_SCHEMA = 'SRA'\n"
                    + "AND   TABLE_NAME   = 'Posts';";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int id = rs.getInt("AUTO_INCREMENT");

            query = "insert into posts (title, Fields_id, message, date_posted, date_started, status, phase) "
                    + "values (?,?,?, ?, ?, 'Pending', ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, title);
            pstmt.setInt(2, fieldID);
            pstmt.setString(3, message);
            pstmt.setDate(4, date);
            pstmt.setDate(5, date);
            pstmt.setString(6, phase);
            pstmt.executeUpdate();
            query = "insert into mediafile(Posts_id, img_url) values (?,?)";
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < imagePath.size(); i++) {
                pstmt.setInt(1, id);
                pstmt.setString(2, imagePath.get(i));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ForumDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Comment> getComments(int postID, int pageNum, Date date) {

        ArrayList<Comment> comments = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select c.id, c.Farmers_name, c.message, c.date, u.name\n"
                    + "from comments c left join users u on c.Users_username = u.username\n"
                    + "where c.Posts_id = ? and c.date <= ?\n"
                    + "limit 10\n"
                    + "offset ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, postID);
            pstmt.setDate(2, date);
            pstmt.setInt(3, (pageNum - 1) * 10);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                comments = new ArrayList();
                do {
                    Comment comment = new Comment();
                    comment.setId(rs.getInt("id"));
                    if (rs.getString("Farmers_name") != null) {
                        comment.setFarmersName(rs.getString("Farmers_name"));
                    } else {
                        comment.setFarmersName(rs.getString("name"));
                    }
                    comment.setMessage(rs.getString("message"));
                    comment.setDate(rs.getDate("date"));
                    comments.add(comment);
                } while (rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ForumDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return comments;
    }

    public Post getPostByRecommendation(int recommendationID, Date date) {
        Post post = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select *\n"
                    + "from posts\n"
                    + "where Recommendations_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, recommendationID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                post = new Post();
                post.setId(rs.getInt("id"));
                post.setProblemID(rs.getInt("Problems_id"));
                post.setTitle(rs.getString("title"));
                post.setMessage(rs.getString("message"));
                post.setStatus(rs.getString("status"));
                post.setDatePosted(rs.getDate("date_posted"));
                
                query = "select c.id, c.Farmers_name, c.message, c.date, u.name\n"
                        + "from comments c left join users u on c.Users_username = u.username\n"
                        + "where c.Posts_id = ? and c.date <= ?\n"
                        + "limit 10\n"
                        + "offset ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, post.getId());
                pstmt.setDate(2, date);
                pstmt.setInt(3, (1 - 1) * 10);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    ArrayList<Comment> comments = new ArrayList();
                    do {
                        Comment comment = new Comment();
                        comment.setId(rs.getInt("id"));
                        if (rs.getString("Farmers_name") != null) {
                            comment.setFarmersName(rs.getString("Farmers_name"));
                        } else {
                            comment.setFarmersName(rs.getString("name"));
                        }
                        comment.setMessage(rs.getString("message"));
                        comment.setDate(rs.getDate("date"));
                        comments.add(comment);
                    } while (rs.next());
                    post.setComments(comments);
                }
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ForumDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return post;
    }

    public void submitComment(String message, int postID, String farmersName, Date date) {
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "insert into comments (Posts_id, Farmers_name, message, recommended, date) values (?,?,?,'N', ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, postID);
            pstmt.setString(2, farmersName);
            pstmt.setString(3, message);
            pstmt.setDate(4, date);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ForumDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void submitProblem(int problemID, int fieldID, ArrayList<String> imagePath, Date date) {

        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            conn.setAutoCommit(false);
            String query = "SELECT `AUTO_INCREMENT`\n"
                    + "FROM  INFORMATION_SCHEMA.TABLES\n"
                    + "WHERE TABLE_SCHEMA = 'SRA'\n"
                    + "AND   TABLE_NAME   = 'Problems-Fields';";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int id = rs.getInt("AUTO_INCREMENT");

            query = "insert into `Problems-Fields`(Problems_id, Fields_id, date, status) values"
                    + " (?, ?, ?, 'Pending')";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, problemID);
            pstmt.setInt(2, fieldID);
            pstmt.setDate(3, date);
            pstmt.executeUpdate();
            query = "insert into ProblemsMedia(problem_id, img_url) values (?,?)";
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < imagePath.size(); i++) {
                pstmt.setInt(1, id);
                pstmt.setString(2, imagePath.get(i));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ForumDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Post> getPostsByFarmer(String farmerName, int pageNum, Date date) {

        ArrayList<Post> posts = null;
        try {
            DBConnectionFactory myFactory = DBConnectionFactory.getInstance();
            Connection conn = myFactory.getConnection();
            String query = "select *\n"
                    + "from posts\n"
                    + "where date_posted <= ? and fields_id in (select id from fields where farmers_name = ? )\n"
                    + "limit 10\n"
                    + "offset ?;";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setDate(1, date);
            pstmt.setString(2, farmerName);
            pstmt.setInt(3, (pageNum - 1) * 10);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                posts = new ArrayList();
                do {
                    Post post = new Post();
                    post.setId(rs.getInt("id"));
                    post.setProblemID(rs.getInt("Problems_id"));
                    post.setFarmersName(farmerName);
                    post.setTitle(rs.getString("title"));
                    post.setMessage(rs.getString("message"));
                    post.setStatus(rs.getString("status"));
                    post.setDatePosted(rs.getDate("date_posted"));
                    posts.add(post);
                } while (rs.next());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ForumDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return posts;
    }

}
