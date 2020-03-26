import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SummaryGenerator {
	public static List<HashMap<String, String>> customer_list = new ArrayList<HashMap<String, String>>();
	public static Map<String, ArrayList<HashMap<String, String>>> product_list = new HashMap<String, ArrayList<HashMap<String, String>>>();
	public static Map<String, HashMap<String, String>> supplier_list = new HashMap<String, HashMap<String, String>>();
	public static String startDate;
	public static String endDate;
	public static String path = "";
	public static void runQueries() {
		Connection connect = null;      
        Statement statement1 = null;     
        Statement statement2 = null;
        Statement statement3 = null;
        ResultSet resultSet1 = null;     
        ResultSet resultSet2 = null;
        ResultSet resultSet3 = null;
        

        try (Scanner sc = new Scanner(System.in)) {
			System.out.println("Please enter start date yyyy-mm-dd");
			startDate = sc.nextLine();
			System.out.println("Start Date:" + startDate);
			
			System.out.println("Please enter end date yyyy-mm-dd");
			endDate = sc.nextLine();
			System.out.println("End Date:" + endDate);
			
			System.out.println("Please enter file path");
			path = sc.nextLine();
			System.out.println("Path:" + path);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        

        
        String q="with order_data as (select OrderID, sum(Quantity*UnitPrice) as order_value from orderdetails  group by OrderID),"+
        		"customer_info as (select CustomerID, count(orders.OrderID) as num_orders, sum(order_value) as order_total_val "+
        				"from orders join order_data using(OrderID) where OrderDate between '"+startDate+"' and '"+endDate+"' group by CustomerID)"+
        		"select CustomerID, num_orders, order_total_val, ContactName as customer_name, Address as street_address,City, Region,"+ 
        		"PostalCode, Country from customers join customer_info using(CustomerID);";
        
        String q2 ="with product_info as (select ProductID, ProductName,CategoryID, SupplierID, CategoryName from products natural join categories),"+
        		"supplier_info as (select ProductID, ProductName, CategoryName, CompanyName from suppliers join product_info using(SupplierID) ),"+
        		"product_sales as (select ProductID, OrderID, Sum(Quantity) as units_sold, Sum(Quantity*orderdetails.UnitPrice)as total_value from orderdetails join orders using(OrderID)"+
        		 "where OrderDate between '"+startDate+"' and '"+endDate+"' group by ProductID)"+
        		"Select ProductID, ProductName, CategoryName, CompanyName, units_sold, total_value from product_sales join supplier_info using(ProductID);";
        
        String q3 = "with supplier_info as(select SupplierID, CompanyName, Address, City, Region, PostalCode, Country, ProductID,"+ 
        		"ProductName from suppliers join products using(SupplierID)),"+
        	    "product_sales as(select ProductID, OrderID, Sum(Quantity) as units_sold, Sum(Quantity*orderdetails.UnitPrice)"+
        		"as total_value from orderdetails join orders using(OrderID) where OrderDate "
        		+ "between '"+startDate+"' and '"+endDate+"' group by ProductID)"+
        		"select SupplierID,CompanyName, Address, City, Region, PostalCode, Country, sum(units_sold),"+
        		"sum(total_value) from product_sales join supplier_info using(ProductID) group by SupplierID;";

        String user;
        String password;
        String dbName;
        
        user = "pkaur";
        password = "B00843735";
        dbName = "csci3901";
        
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            
            connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC&useSSL=false", user, password);


            statement1 = connect.createStatement();
            statement1.executeQuery("use " + dbName + ";");
            resultSet1 = statement1.executeQuery( q );
            System.out.println("resultset1"+resultSet1);
            while (resultSet1.next()) {
            	Map<String, String> customer_info = new HashMap<String, String>();
            	customer_info.put("customer_name", resultSet1.getString("customer_name"));
            	customer_info.put("street_address", resultSet1.getString("street_address"));
            	customer_info.put("city", resultSet1.getString("City"));
            	String region = (resultSet1.getString("Region")==null)?"":resultSet1.getString("Region");
            	customer_info.put("region", region);
            	String PostalCode = (resultSet1.getString("PostalCode")==null)?"":resultSet1.getString("PostalCode");
            	customer_info.put("postal_code", PostalCode);
            	customer_info.put("country", resultSet1.getString("Country"));
            	customer_info.put("num_orders",Integer.toString(resultSet1.getInt("num_orders")));
            	customer_info.put("order_value", resultSet1.getString("order_total_val"));
            	customer_list.add((HashMap<String, String>) customer_info);
            }

            
            statement2 = connect.createStatement();
            statement2.executeQuery("use " + dbName + ";");
            resultSet2 = statement2.executeQuery( q2 );
            while (resultSet2.next()) {
            	Map<String, String> product_info = new HashMap<String, String>();
            	product_info.put("product_name", resultSet2.getString("ProductName"));
            	product_info.put("supplier_name", resultSet2.getString("CompanyName"));
            	product_info.put("units_sold", resultSet2.getString("units_sold"));
            	product_info.put("sale_value", resultSet2.getString("total_value"));
            	String category = resultSet2.getString("CategoryName");
            	if (product_list.containsKey(category)) {
            		ArrayList<HashMap<String, String>> category_products = product_list.get(category);
            		category_products.add((HashMap<String, String>) product_info);
            	} else {
            		ArrayList<HashMap<String, String>> category_products= new ArrayList<HashMap<String, String>>();
            		category_products.add((HashMap<String, String>) product_info);
            		product_list.put(category, category_products);
            	}
            }
            
            
            statement3 = connect.createStatement();
            statement3.executeQuery("use " + dbName + ";");
            resultSet3 = statement3.executeQuery( q3 );
            while (resultSet3.next()) {
            	Map<String, String> supplier_info = new HashMap<String, String>();
            	supplier_info.put("street_address", resultSet3.getString("Address"));
            	supplier_info.put("city", resultSet3.getString("City"));
            	String region = (resultSet3.getString("Region")==null)?"":resultSet3.getString("Region");
            	String PostalCode = (resultSet3.getString("PostalCode")==null)?"":resultSet3.getString("PostalCode");
            	supplier_info.put("postal_code", PostalCode);
            	supplier_info.put("region", region);
            	supplier_info.put("country", resultSet3.getString("Country"));
            	supplier_info.put("num_products", resultSet3.getString("sum(units_sold)"));
            	supplier_info.put("product_value", resultSet3.getString("sum(total_value)"));
            	String supplierName= resultSet3.getString("CompanyName");
            	supplier_list.put(supplierName,(HashMap<String, String>) supplier_info);
            }
            


        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

	}
	public static void main(String[] args) {
		runQueries();
		XMLgenerator.generateXml();
	}
}
