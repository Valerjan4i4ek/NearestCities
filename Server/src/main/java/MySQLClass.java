import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class MySQLClass {
    private final static String fileName = "database.properties";

    static{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MySQLClass(){
        baseCreate();
        tableTicketCreate();
    }

    public Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try(InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)){
            if(in != null){
                props.load(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        return DriverManager.getConnection(url, username, password);
    }

    public void baseCreate(){
        try{
            Connection conn = null;
            Statement st = null;

            try{
                conn = getConnection();
                st = conn.createStatement();
                st.executeUpdate("CREATE DATABASE IF NOT EXISTS NearestCities");
            }
            finally {
                try{
                    if(conn != null){
                        conn.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    if(st != null){
                        st.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void tableTicketCreate(){
        try{
            Connection conn = null;
            Statement st = null;

            try{
                conn = getConnection();
                st = conn.createStatement();
                st.executeUpdate("CREATE TABLE IF NOT EXISTS NearestCities.ticket " +
                        "(id INT NOT NULL, lat DOUBLE NOT NULL, lon DOUBLE NOT NULL, distance INT NOT NULL)");
            }finally {
                try{
                    if(conn != null){
                        conn.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    if(st != null){
                        st.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addTicket(Ticket ticket){
        try{
            Connection conn = null;
            PreparedStatement ps = null;

            try{
                conn = getConnection();
                ps = conn.prepareStatement("INSERT INTO ticket (id, lat, lon, distance) VALUES (?, ?, ?, ?)");
                ps.setInt(1, ticket.getId());
                ps.setDouble(2, ticket.getLat());
                ps.setDouble(3, ticket.getLon());
                ps.setInt(4, ticket.getDistance());
                ps.executeUpdate();
            }finally {
                try{
                    if(conn != null){
                        conn.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    if(ps != null){
                        ps.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Integer> getTicketId(){
        List<Integer> list = new LinkedList<>();

        try{
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try{
                conn = getConnection();
                String query = "SELECT id FROM ticket";
                ps = conn.prepareStatement(query);
                rs = ps.executeQuery();

                while (rs.next()){
                    int id = rs.getInt("id");
                    list.add(id);
                }
            }finally {
                try{
                    if(conn != null){
                        conn.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    if(ps != null){
                        ps.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    if(rs != null){
                        rs.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
