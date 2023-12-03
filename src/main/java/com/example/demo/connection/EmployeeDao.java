package com.example.demo.connection;

import com.example.demo.Employee;
import com.example.demo.Position;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet resultSet = null;
    int st;//status

    public static void main(String[] args) {
        EmployeeDao t = new EmployeeDao();
       System.out.println(t.fetchAll());
        t.fetchAll().forEach(System.out::println);
    }

    public int insert(Employee employee) {
        connection = ConnectionFactory.getConnection();
        try {
            String query = "insert into employee(first_name, last_name, position_id, salary) values (?, ?, ?,?)";
            ps = connection.prepareStatement(query);

            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setInt(3, employee.getPosition().getId());
            ps.setDouble(4, employee.getSalary());


            st = ps.executeUpdate();
            System.out.println("inserted employee " + st);
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

    public int update(Employee employee) {
        connection = ConnectionFactory.getConnection();
        try {
            String query = "update employee set first_name = ?, last_name = ?, position_id = ?, salary = ?  where id=? ";
            ps = connection.prepareStatement(query);
            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setInt(3, employee.getPosition().getId());
            ps.setDouble(4, employee.getSalary());
            ps.setInt(5, employee.getId());
            st = ps.executeUpdate();
            System.out.println("updated employee " + st);
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
            String query = "delete from employee where id=? ";
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


    public List<Employee> fetchAll() {

        List<Employee> employeeList =  new ArrayList<>();

        connection = ConnectionFactory.getConnection();
        try {
            String query = "select * from employee order by id asc";

            ps = connection.prepareStatement(query);

            resultSet = ps.executeQuery();
            employeeList = createEmployeeListFromResultSet(resultSet);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return employeeList;
    }

    private List<Employee> createEmployeeListFromResultSet(ResultSet resultSet) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                double salary = resultSet.getDouble("salary");
                System.out.println("Salary from ResultSet: " + salary);
                Employee employee =  new Employee();
                employee.setId(resultSet.getInt("id"));
                employee.setFirstName(resultSet.getString("first_name"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setSalary(resultSet.getDouble("salary"));
                Position position = new Position();
                position.setName(resultSet.getString("position_id"));
                employee.setPosition(position);
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }
}


