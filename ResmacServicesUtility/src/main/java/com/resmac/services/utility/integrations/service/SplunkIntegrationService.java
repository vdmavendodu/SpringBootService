package com.resmac.services.utility.integrations.service;

import java.util.HashMap;
import java.util.Map;

import com.resmac.constants.Constants;
import com.resmac.dto.common.PropObjectLoader;
import com.resmac.dto.primitive.JString;
import com.resmac.integrations.dto.SplunkLoggerDTO;
import com.splunk.Args;
import com.splunk.HttpService;
import com.splunk.Receiver;
import com.splunk.SSLSecurityProtocol;

public class SplunkIntegrationService {

	public Map<String, Object> populateConnetionArguments()throws Exception{

		Map<String, Object> connectionArgs = new HashMap<String, Object>();
		try {
			PropObjectLoader propObjectLoader = new PropObjectLoader();
			connectionArgs.put("host", propObjectLoader.getPropertyValue(Constants.SPLUNK_HOST));
			connectionArgs.put("username", propObjectLoader.getPropertyValue(Constants.SPLUNK_USERNAME));
			connectionArgs.put("password", propObjectLoader.getPropertyValue(Constants.SPLUNK_PASSWORD));
			connectionArgs.put("port", propObjectLoader.getPropertyValue(Constants.SPLUNK_PORT));
			connectionArgs.put("scheme", propObjectLoader.getPropertyValue(Constants.SPLUNK_SCHEME));
			connectionArgs.put("app", propObjectLoader.getPropertyValue(Constants.SPLUNK_APPLICATION));
		} catch (Exception e) {
			throw e;
		}
		return connectionArgs;
	
	}
	public void logErrorsToSplunk(SplunkLoggerDTO dto) {
		try {
			HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

			com.splunk.Service service = com.splunk.Service.connect(populateConnetionArguments());
			Receiver receiver = service.getReceiver();

			Map<String, Object> logsArgsMap = new HashMap<String, Object>();
			if(!JString.isCompletlyEmpty(dto.getLenderCompanyId()))
				logsArgsMap.put("LenderId",dto.getLenderCompanyId());
			if(!JString.isCompletlyEmpty(dto.getLoanId()))
				logsArgsMap.put("LoanId", dto.getLoanId());
			if(!JString.isCompletlyEmpty(dto.getBrokerCompanyId()))
				logsArgsMap.put("BrokerCompanyId",dto.getBrokerCompanyId());
			if(!JString.isCompletlyEmpty(dto.getUserId()))
				logsArgsMap.put("Userid",dto.getUserId());
			if(!JString.isCompletlyEmpty(dto.getCompnayName()))
				logsArgsMap.put("Company", dto.getCompnayName());

			Args logsArgs = new Args();
			logsArgs.put("sourcetype", logsArgsMap);

			receiver.log(new PropObjectLoader().getPropertyValue(Constants.SPLUNK_APPLICATION_INDEX), logsArgs, dto.getExceptionBody());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
