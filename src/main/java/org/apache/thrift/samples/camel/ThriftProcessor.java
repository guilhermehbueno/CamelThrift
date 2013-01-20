package org.apache.thrift.samples.camel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.http.HttpMessage;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.samples.CalculatorHandler;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.InitializingBean;

import tutorial.Calculator;

/**
 * Simple processor that delegates incoming requests to Thrift Handlers
 * 
 * @author leogsilva at gmail dot com
 * 
 */
public class ThriftProcessor implements Processor, InitializingBean {

    private TProtocolFactory inProtocolFactory;
    private TProtocolFactory outProtocolFactory;
	private tutorial.Calculator.Processor<CalculatorHandler> processor;

	@Override
	public void process(Exchange exchange) throws Exception {
		HttpMessage httpMessage = exchange.getIn(HttpMessage.class);
		InputStream inputBody = (InputStream) exchange.getIn().getBody();
		HttpServletRequest request = httpMessage.getRequest();
		HttpServletResponse response = httpMessage.getResponse();
		doPost(request, response, inputBody);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response, InputStream is)
			throws ServletException, IOException {

		TTransport inTransport = null;
		TTransport outTransport = null;

		try {
			response.setContentType("application/x-thrift");
			OutputStream out = response.getOutputStream();

			TTransport transport = new TIOStreamTransport(is, out);
			inTransport = transport;
			outTransport = transport;

			TProtocol inProtocol = inProtocolFactory.getProtocol(inTransport);
			TProtocol outProtocol = outProtocolFactory
					.getProtocol(outTransport);

			processor.process(inProtocol, outProtocol);
			out.flush();
		} catch (TException te) {
			throw new ServletException(te);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
        this.inProtocolFactory = new TBinaryProtocol.Factory(true, true);
        this.outProtocolFactory = new TBinaryProtocol.Factory(true,true);
        this.processor = new Calculator.Processor(new CalculatorHandler());
	}

}
