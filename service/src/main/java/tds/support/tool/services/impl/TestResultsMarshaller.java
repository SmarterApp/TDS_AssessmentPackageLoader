package tds.support.tool.services.impl;

import org.springframework.stereotype.Service;
import tds.trt.model.TDSReport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Service
public class TestResultsMarshaller {

    public static String mashall(final TDSReport testResults) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(TDSReport.class);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final StringWriter sw = new StringWriter();

        marshaller.marshal(testResults, sw);

        return sw.toString();
    }
}
