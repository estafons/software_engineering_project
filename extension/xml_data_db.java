package gov.nist.sip.proxy.extension;

import net.java.sip.communicator.common.PropertiesDepot_non_static;

public class xml_data_db{
	private static String DB_NAME="";
	private static String user = "";
	private static String ip = "";
	// db ip must be the same as proxy
	private static String pass = "";
	private static Boolean init = false;
	private static String replace_empty_by_null(String s){
		return ((s == null) ? "" : s);
	}
	public xml_data_db(){
	}
	public static void parse_xml(String Filename, String Address){
		PropertiesDepot_non_static p = new PropertiesDepot_non_static();
		if(!Address.equals(null)){
			p.setDesignatedAddress(Address);
		}
		if(Filename != ""){
			p.setDefault(Filename);
		}
		p.loadProperties();
        DB_NAME = replace_empty_by_null(p.getProperty("db.sip.DB_NAME"));
		user = replace_empty_by_null(p.getProperty("db.sip.USER_NAME"));
		ip = replace_empty_by_null(p.getProperty("db.sip.IP_ADDRESS")); // db ip must be the same as proxy
		pass = replace_empty_by_null(p.getProperty("db.sip.PASSWORD"));
	}
	public static String[] getData(String Filename, String Designated_xml_address){
		String[] Result = new String[4];
		if(!init){
			parse_xml(Filename,Designated_xml_address);
		}
		Result[0] = DB_NAME;
		Result[1] = user;
		Result[2] = ip;
		Result[3] = pass;
		init = true;
		return Result;
	}
	public static void Reset(){
		DB_NAME="";
		user = "";
		ip = "";
		pass = "";
		init = false;
	}
}
