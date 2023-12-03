package com.example.demo.connection;

import com.example.demo.Position;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PositionDao {
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet resultSet = null;
    int st;//status


    public int insert(Position position) {
        connection = ConnectionFactory.getConnection();
        try {
            String query = "insert into position(name) values (?)";
            ps = connection.prepareStatement(query);

            ps.setString(1, position.getName());

            st = ps.executeUpdate();
            System.out.println("inserted course " + st);
        } catch (Exception e) {
            st = -2;
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return st;
    }

    public int update(Position position) {
        connection = ConnectionFactory.getConnection();
        try {
            String query = "update position set name = ?  where id=? ";
            ps = connection.prepareStatement(query);
            ps.setString(1, position.getName());
            ps.setInt(2, position.getId());
            st = ps.executeUpdate();
            System.out.println("updated student " + st);
        } catch (Exception e) {
            st = -2;
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return st;
    }

    public int delete(int id) {
        connection = ConnectionFactory.getConnection();
        try {
            String query = "delete from position where id=? ";
            ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            st = ps.executeUpdate();
            System.out.println("deleted course " + st);
        } catch (Exception e) {
            st = -2;
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return st;
    }


    public List<Position> fetchAll() {

        List<Position> positions =  new ArrayList<>();

        connection = ConnectionFactory.getConnection();
        try {
            String query = "select * from position order by id asc";

            ps = connection.prepareStatement(query);

            resultSet = ps.executeQuery();
            positions = createDisciplineListFromResultSet(resultSet);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return positions;
    }

    private List<Position> createDisciplineListFromResultSet(ResultSet resultSet) {
        List<Position> positions = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Position position =  new Position();
                position.setId(resultSet.getInt("id"));
                position.setName(resultSet.getString("name"));

                positions.add(position);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }

    public Position findPositionByName(String name) {
        connection = ConnectionFactory.getConnection();
        try {
            String query = "select * from position where name = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, name);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Position position = new Position();
                position.setId(resultSet.getInt("id"));
                position.setName(resultSet.getString("name"));
                return position;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null; // Возвращаем null, если должность с указанным названием не найдена
    }
}
