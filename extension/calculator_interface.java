package gov.nist.sip.proxy.extension;

import java.sql.Timestamp;

public interface calculator_interface {

	public double calculate_cost(int caller, int callee, Timestamp start, Timestamp end);
	
}
