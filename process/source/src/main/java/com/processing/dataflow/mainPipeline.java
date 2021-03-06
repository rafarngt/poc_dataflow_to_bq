package com.processing.dataflow;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.bigquery.model.TableRow;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.processing.dataflow.option.OptionsPipeline;
import com.processing.dataflow.process.*;
import com.processing.dataflow.model.Sensor;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.TableDestination;
import org.apache.beam.sdk.io.gcp.bigquery.TableRowJsonCoder;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.processing.dataflow.utils.LoadConfigurationsPipeline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class mainPipeline {
	private static final Logger LOG = LoggerFactory.getLogger(com.processing.dataflow.mainPipeline.class);
	private static final String DEFAULT_CONFIG_FILE = "application_gcp.properties";
	private OptionsPipeline optionsPipeline;

	public static void main(String[] args) {

		com.processing.dataflow.mainPipeline sp = new com.processing.dataflow.mainPipeline();

		sp.init();

		sp.run();
	}

	public void init() {

		optionsPipeline = new LoadConfigurationsPipeline(DEFAULT_CONFIG_FILE).inicia();

	}

	public void run()
	{

		Pipeline pipeline = Pipeline.create(optionsPipeline);

		doDataProcessing(pipeline);

		pipeline.run();
	}

	public void doDataProcessing(Pipeline pipeline)
	{



		PCollection<PubsubMessage> mensajePubSub = pipeline
				  .apply("Escucha Mensajes PubSub",
						  PubsubIO.readMessagesWithAttributes().fromSubscription((optionsPipeline.getNombreSuscripcion())));

		Window<PubsubMessage> processingWindow =
				Window.into(FixedWindows.of(Duration.standardSeconds(5)));

		PCollection<Sensor> sensorPojo = mensajePubSub
				.apply(processingWindow)
				.apply("Convierte Mensaje PubSub a Sensor",
				ParDo.of(new ConvertJsonToSensorProcess()));

		PCollection<Sensor> enrichSensor = sensorPojo
				.apply("Enriquece Data sensor",
						ParDo.of(new EnrichDataProcess(optionsPipeline.getProject())));


		enrichSensor.apply("Escribe en Bigquery", BigQueryIO.<Sensor>write()
			.to(input -> getTableDestination("poc-datahack-ps-df-bq-01", "raw","sensors"))
			.withFormatFunction((Sensor msg) -> convertJsonToTableRow(msg))
			.withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER)
			.withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
			.withMethod(BigQueryIO.Write.Method.STREAMING_INSERTS));


	}


	static TableRow convertJsonToTableRow(Sensor msg) {
		ObjectMapper mapper = new ObjectMapper();
		TableRow row = null;
		String json;
		try {
			json = mapper.writeValueAsString(msg);

		try(InputStream  inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))){
			row = TableRowJsonCoder.of().decode(inputStream, Coder.Context.OUTER);
		} catch (IOException e) {
			e.printStackTrace();

		}
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return row;
	}

	static TableDestination getTableDestination(String project, String dataset, String table){

		return new TableDestination(String.format("%s:%s.%s", project, dataset, table), null);

	}







}
