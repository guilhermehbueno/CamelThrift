package org.apache.thrift.samples.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class ThriftRouteBuilder extends RouteBuilder {

	@Autowired
	ThriftProcessor processor;
	
	@Override
	public void configure() throws Exception {
		from("jetty://http://localhost:8182/thrift").process(processor).end();
	}

}
