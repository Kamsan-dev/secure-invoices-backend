package io.kamsan.secureinvoices.utils;

import static nl.basjes.parse.useragent.UserAgent.AGENT_NAME;
import static nl.basjes.parse.useragent.UserAgent.DEVICE_NAME;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent.ImmutableUserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

public class RequestUtils {
	
	public static String getIpAddress(HttpServletRequest request) {
		String ipAddress = "Unknown IP";
		if (request != null) {
			ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null || "".equals(ipAddress)) {
				ipAddress = request.getRemoteAddr();
			}
		}
		
		return ipAddress;
	}
	
	public static String getDevice(HttpServletRequest request) {
		UserAgentAnalyzer uaa  =UserAgentAnalyzer
	            .newBuilder()
	            .hideMatcherLoadStats()
	            .withCache(1000)
	            .build();
		
		ImmutableUserAgent agent = uaa.parse(request.getHeader("user-agent"));
//		return agent.getValue(OPERATING_SYSTEM_NAME) + " - " 
//				+ agent.getValue(AGENT_NAME) + " - " 
//				+ agent.getValue(DEVICE_NAME);
		return agent.getValue(AGENT_NAME) + " - " 
		+ agent.getValue(DEVICE_NAME);
	}
}
