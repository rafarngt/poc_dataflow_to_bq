package com.processing.dataflow.option;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.ValueProvider;


public interface OptionsPipeline extends DataflowPipelineOptions  {

	@Description("Pub/Sub Nombre Topico")
    String getNombreTopico();
	void setNombreTopico(String value);

	
	@Description("Pub/Sub Nombre Suscripcion")
    String getNombreSuscripcion();
	void setNombreSuscripcion(String value);


    @Description("Bigquery Project")
    String getNombreProyectoBigquery();
    void setNombreProyectoBigquery(String value);

    @Description("Bigquery dataset")
    String getNombreBigqueryDataset();
    void setNombreBigqueryDataset(String value);

    @Description("Bigquery tabla")
    String getNombreBigqueryTabla();
    void setNombreBigqueryTabla(String value);

	

    
    
}
