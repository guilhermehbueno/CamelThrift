package org.apache.thrift.samples;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import shared.thrift.SharedStruct;
import tutorial.Calculator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleThriftTest {

	@Test
	public void simpleTest() throws Exception {
		TServerTransport transport = new TServerSocket(7878);
		CalculatorHandler handler = new CalculatorHandler();
		Calculator.Processor<CalculatorHandler> processor = new Calculator.Processor<CalculatorHandler>(handler);
		final TServer server = new TSimpleServer(new Args(transport).processor(processor));
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				server.serve();
			}
		});
		t.start();
		
		TTransport clientTransport = new TSocket("localhost",7878);
		clientTransport.open();
		TProtocol protocol = new TBinaryProtocol(clientTransport);
		Calculator.Client client = new Calculator.Client(protocol);
		SharedStruct ss = client.getStruct(1);
		Assert.assertNotNull(ss);
		Assert.assertEquals("Hello",ss.getValue());
		int r = client.add(1, 2);
		Assert.assertEquals(3,r);
	}

	@Test
	public void simpleHttpTest() throws Exception {
		String address = "http://localhost:8182/thrift";
		TTransport clientTransport = new THttpClient(address);
		clientTransport.open();
		TProtocol protocol = new TBinaryProtocol(clientTransport);
		Calculator.Client client = new Calculator.Client(protocol);
		int r = client.add(1, 2);
		Assert.assertEquals(3,r);
	}

}
