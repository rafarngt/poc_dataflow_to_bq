package com.processing.dataflow.process;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.pubsub.v1.PubsubMessage;
import com.processing.dataflow.model.Sensors;
import org.apache.beam.sdk.transforms.DoFn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;


public class ConvierteJsonAPojoSensorsProcess extends DoFn<String, Sensors>  {


	private static final long serialVersionUID = 1462827258689031685L;
	private static final Logger LOG = LoggerFactory.getLogger(ConvierteJsonAPojoSensorsProcess.class);
	private ObjectMapper mapper;
	// private JavaTimeModule javaTimeModule;
	
	@Setup
    public void init() {
		mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		/*javaTimeModule = new JavaTimeModule();
		
		javaTimeModule.addSerializer(LocalTime.class, 
				new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalDate.class, 
				new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		mapper.registerModule(javaTimeModule); */
		
    }
	
	@ProcessElement
	public void processElement(DoFn<String, Sensors>.ProcessContext c) throws Exception {

		try {
			Sensors Sensors = mapper.readValue(c.element(), Sensors.class);
			LOG.info("Sensors {}", Sensors);
			
			c.output(Sensors);
		
		} catch(Exception e)
		{
			LOG.error("Error al intentar convertir Json to Pojo:" + e.getMessage(), e);
		}
	}
	
}
