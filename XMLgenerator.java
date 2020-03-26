// XMLgenerator generates readable summary report in XML format

/*---------------------------Import statements------------------------------*/
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
	 
public class XMLgenerator {
	
	// get file path from SummaryGenerator
    public static final String xmlFilePath = SummaryGenerator.path;
    
    /*
	 generateXml method:
	  * functionality: This method is used to process data from customer_list,
	  * product_list, supplier_list and generate Xml
	 */
    public static void generateXml() {
 
        try {
        	
        	// create instance of document using DocumentBuilderFactory
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
 
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
 
            Document document = documentBuilder.newDocument();
 
            // root element
            Element root = document.createElement("year_end_summary");
            document.appendChild(root);
            
            // year element child of root
            Element year = document.createElement("year");
            root.appendChild(year);
            
            // start_date child of year and gets startDate from SummaryGenerator
            Element start_date = document.createElement("start_date");
            start_date.appendChild(document.createTextNode(SummaryGenerator.startDate));
            year.appendChild(start_date);
            
            // end_date child of year and gets endDate from SummaryGenerator
            Element end_date = document.createElement("end_date");
            end_date.appendChild(document.createTextNode(SummaryGenerator.endDate));
            year.appendChild(end_date);
            
            // customer_list element child of year_end_summary
            Element customer_list = document.createElement("customer_list");
            root.appendChild(customer_list);
            
            // product_list element child of year_end_summary
            Element product_list = document.createElement("product_list");
            root.appendChild(product_list);
            
            // supplier_list element child of year_end_summary
            Element supplier_list = document.createElement("supplier_list");
            root.appendChild(supplier_list);
            
            // process data in customer_list to form xml
            for (HashMap<String, String> m: SummaryGenerator.customer_list) {
            	Element customer = document.createElement("customer");
            	customer_list.appendChild(customer);
            	Element address = document.createElement("address");
            	customer.appendChild(address);
            	Iterator<Entry<String, String>> hmIterator = m.entrySet().iterator();
            	while (hmIterator.hasNext()) { 
                    Map.Entry<String, String> mapElement = hmIterator.next(); 
                    String Key = mapElement.getKey();
                    String Val =  mapElement.getValue();
                    Element prop = document.createElement(Key);
                    prop.appendChild(document.createTextNode(Val));
                    if (Key.equals("customer_name") || Key.equals("num_orders") 
                    		|| Key.equals("order_value")) {
	                	customer.appendChild(prop);
                    } else {
                    	address.appendChild(prop);
                    }
            	}
            }
            
            // process data in customer_list to form xml
            for (Map.Entry<String, ArrayList<HashMap<String, String>>> category_prods : SummaryGenerator.product_list.entrySet()) {
            	Element category = document.createElement("category");
            	product_list.appendChild(category);
            	
            	String categoryName = category_prods.getKey();
            	
            	Element category_name = document.createElement("category_name");
            	category_name.appendChild(document.createTextNode(categoryName));
            	category.appendChild(category_name);
            	
            	ArrayList<HashMap<String, String>> productCatList=  category_prods.getValue();
            	for (HashMap<String, String> prod:productCatList) {
            		Element product = document.createElement("product");
                	category.appendChild(product);
                	
            		Iterator<Entry<String, String>>  prodItr = prod.entrySet().iterator();
                    while (prodItr.hasNext()) { 
                    	
                    	Map.Entry<String, String> mapElement = prodItr.next(); 
                        String Key =  mapElement.getKey();
                        String Val =  mapElement.getValue();
                        
                        Element prop = document.createElement(Key);
                        prop.appendChild(document.createTextNode(Val));
                        product.appendChild(prop);
                    }
            	}

            }
            
            // process data in supplier_list to form xml
            for (Map.Entry<String, HashMap<String, String>> supplier_info : SummaryGenerator.supplier_list.entrySet()) {
            	Element supplier = document.createElement("supplier");
            	supplier_list.appendChild(supplier);
            	
            	String supplierName = supplier_info.getKey();
            	Element supplier_name = document.createElement("supplier_name");
            	supplier_name.appendChild(document.createTextNode(supplierName));
            	supplier.appendChild(supplier_name);
            	
            	Element address = document.createElement("address");
            	supplier.appendChild(address);
            	
            	HashMap<String, String> supplyMap = supplier_info.getValue();
            	Iterator<Entry<String, String>> supplyItr = supplyMap.entrySet().iterator();
                while (supplyItr.hasNext()) { 
                	
                	Map.Entry<String, String> mapElement = supplyItr.next(); 
                    String Key =  mapElement.getKey();
                    String Val =  mapElement.getValue();
                    
                    Element prop = document.createElement(Key);
                    prop.appendChild(document.createTextNode(Val));
                    
                    if(Key.equals("num_products") || Key.equals("product_value")) {
                    	supplier.appendChild(prop);
                    } else {
                    	address.appendChild(prop);
                    }
                }
            }

            // finally generate xml with proper indentation
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
 

            transformer.transform(domSource, streamResult);
 
            System.out.println("Done creating XML File");
 
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
}
