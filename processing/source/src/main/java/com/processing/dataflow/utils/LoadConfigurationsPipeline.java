package com.processing.dataflow.utils;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.processing.dataflow.option.OptionsPipeline;


public class LoadConfigurationsPipeline {
	private static final Logger LOG = LoggerFactory.getLogger(LoadConfigurationsPipeline.class);
	private String rutaArchivoConfiguraciones;
	private OptionsPipeline options;

	public LoadConfigurationsPipeline(String rutaArchivoConfiguraciones) {
		LOG.info(rutaArchivoConfiguraciones);
		this.rutaArchivoConfiguraciones = rutaArchivoConfiguraciones;
	}
	
	public OptionsPipeline inicia() {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
				new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.properties()
						.setFileName(this.rutaArchivoConfiguraciones));
		
		//define pipeline options
		 options = PipelineOptionsFactory.create().as(OptionsPipeline.class);

			try
			{
				LOG.info("Inicia Carga de Configuraciones");
				Configuration config = builder.getConfiguration();

				// Set DataFlow options
				options.setAppName(config.getString("gcp.df.registra.clientes.appName"));
				LOG.info("La AppName es:" + config.getString("gcp.df.registra.clientes.appName"));
				
				LOG.info("Temp location:" + config.getString("gcp.df.temp"));
				options.setTempLocation(config.getString("gcp.df.temp"));
				
				options.setStagingLocation(config.getString("gcp.df.staging"));
				LOG.info("Staging location:" + config.getString("gcp.df.staging"));
				
				LOG.info("Runner:DataflowRunner");
				options.setRunner(DataflowRunner.class);
				//options.setRunner(DirectRunner.class);
				//options.setStreaming(false);
				LOG.info("Project ID:" + config.getString("gcp.projectId"));
				options.setProject(config.getString("gcp.projectId"));
				//options.setAutoscalingAlgorithm(AutoscalingAlgorithmType.THROUGHPUT_BASED);

				options.setMaxNumWorkers(Integer.parseInt(config.getString("gcp.df.maxWorkers")));
				options.setJobName(config.getString("gcp.df.registra.clientes.appName")  + "dfjob");




				
				LOG.info("Service Account:" + config.getString("gcp.df.service.account"));
				options.setServiceAccount(config.getString("gcp.df.service.account"));
				
				options.setRegion(config.getString("gcp.df.region"));
				LOG.info(config.getString("gcp.df.region"));
				// Set custom DataFlow options
				options.setNombreTopico(config.getString("gcp.pubsub.topic.raw"));
				options.setNombreSuscripcion(config.getString("gcp.pubsub.topic.subscription.raw"));

				options.setDiskSizeGb(Integer.parseInt(config.getString("gcp.df.diskSize")));
				LOG.info("DiskSizeGb:" + config.getString("gcp.df.diskSize"));
				options.setWorkerMachineType(config.getString("gcp.df.machineType"));
				// Set Bigquery configuration
				options.setNombreBigqueryDataset(config.getString("gcp.bigquery.dataset"));
				options.setNombreProyectoBigquery(config.getString("gcp.bigquery.projectid"));
				options.setNombreBigqueryTabla(config.getString("gcp.bigquery.table"));
			

			}
			catch(ConfigurationException cex)
			{
				LOG.error("Error al cargar configuraciones iniciales:", cex);
				cex.printStackTrace();
			} catch(Exception ex) {
				LOG.error("Error al cargar configuraciones iniciales:", ex);
			}
		
		return options;
	}
	
	

}

