package com.processing.dataflow.process;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.processing.dataflow.model.Sensor;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.transforms.DoFn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertJsonToSensorProcess extends DoFn<PubsubMessage, Sensor>  {


	private static final long serialVersionUID = 1462827258689031685L;
	private static final Logger LOG = LoggerFactory.getLogger(ConvertJsonToSensorProcess.class);
	private ObjectMapper mapper;
	
	@Setup
    public void init() {
		mapper = new ObjectMapper();
		 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 df.setTimeZone(TimeZone.getTimeZone("UTC"));
		 mapper.setDateFormat(df);
		 mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);


		
    }
	
	@ProcessElement
	public void processElement(DoFn<PubsubMessage, Sensor>.ProcessContext c) throws Exception {

		try {
			String data = new String(c.element().getPayload(), StandardCharsets.UTF_8);
			Sensor Sensors = mapper.readValue(data, Sensor.class);
			LOG.info("Sensor {}", Sensors);
			
			c.output(Sensors);
		
		} catch(Exception e)
		{
			LOG.error("Error al intentar convertir Json to Pojo:" + e.getMessage(), e);
		}
	}
	
}
