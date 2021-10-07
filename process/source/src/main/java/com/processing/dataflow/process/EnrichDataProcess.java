package com.processing.dataflow.process;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.processing.dataflow.model.Sensor;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;



public class EnrichDataProcess extends DoFn<Sensor, Sensor>  {


	private static final long serialVersionUID = 1462827258689031685L;
	private static final Logger LOG = LoggerFactory.getLogger(EnrichDataProcess.class);
	private ObjectMapper mapper;
	private String projectId;
	private String COLLECTION_NAME = "devices";
	private Firestore db;
	private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public EnrichDataProcess(String projectId){
		this.projectId = projectId;
	}

	@Setup
    public void init() {
		mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		db = buildConnexionFirestore();
		
    }
	
	@ProcessElement
	public void processElement(ProcessContext c) {

		try {
			Sensor data = c.element();
			Sensor copySensor = (Sensor) data.clone();

			DocumentReference docRef = db.collection(COLLECTION_NAME).document(data.getUniqueID());
			// asynchronously retrieve the document
			ApiFuture<DocumentSnapshot> future = docRef.get();
			// ...
			// future.get() blocks on response
			DocumentSnapshot document = future.get();
			if (document.exists()) {
				LOG.info("Document data: {} " , document.getData());
				copySensor.setDeviceName((String) document.getData().get("deviceName"));
				copySensor.setDeveceType((String) document.getData().get("deviceType"));

			} else {
				LOG.info("No such document!");
			}
			
			c.output(copySensor);
		
		} catch(Exception e)
		{
			LOG.error("Error al intentar obtener data de firestore" + e.getMessage(), e);
		}
	}


	private Firestore buildConnexionFirestore()  {
		// Use the application default credentials

		GoogleCredentials credentials = null;
		try {
			credentials = GoogleCredentials.getApplicationDefault();
		} catch (IOException e) {

			LOG.error("Error al intentar conecta con db firestore" );

		}
		FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(credentials)
					.setProjectId(projectId)
					.build();
			LOG.info("FirebaseApp {}", FirebaseApp.getApps());
			if(FirebaseApp.getApps().isEmpty()){
				FirebaseApp.initializeApp(options);
			}else{
				FirebaseApp.getApps();
			}


			return FirestoreClient.getFirestore();


	}
	
}
