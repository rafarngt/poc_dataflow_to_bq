package com.processing.dataflow.model;

import java.util.ArrayList;

import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.beam.sdk.schemas.JavaBeanSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.joda.time.DateTime;

@DefaultSchema(JavaBeanSchema.class)
public class Sensor implements Cloneable  {

	@JsonProperty("sensorID")
    @Nullable private String sensorID;
	@JsonProperty("uniqueID")
    @Nullable private String uniqueID;
	@JsonProperty("timecollected")
    @Nullable private String timecollected;
	@JsonProperty("value")
    @Nullable private Double value;
	@JsonProperty("deviceName")
	@Nullable private String deviceName;
	@JsonProperty("deviceType")
	@Nullable private String deveceType;
    
	public Sensor() {}


	@Nullable
	public String getSensorID() {
		return sensorID;
	}

	public void setSensorID(@Nullable String sensorID) {
		this.sensorID = sensorID;
	}

	@Nullable
	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(@Nullable String uniqueID) {
		this.uniqueID = uniqueID;
	}

	@Nullable
	public String getTimecollected() {
		return timecollected;
	}

	public void setTimecollected(@Nullable String timecollected) {
		this.timecollected = timecollected;
	}

	@Nullable
	public Double getValue() {
		return value;
	}

	public void setValue(@Nullable Double value) {
		this.value = value;
	}

	@Nullable
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(@Nullable String deviceName) {
		this.deviceName = deviceName;
	}

	@Nullable
	public String getDeveceType() {
		return deveceType;
	}

	public void setDeveceType(@Nullable String deveceType) {
		this.deveceType = deveceType;
	}

	public Sensor(@Nullable String sensorID, @Nullable String uniqueID, @Nullable String timecollected, @Nullable Double value, @Nullable String deviceName, @Nullable String deveceType) {
		this.sensorID = sensorID;
		this.uniqueID = uniqueID;
		this.timecollected = timecollected;
		this.value = value;
		this.deviceName = deviceName;
		this.deveceType = deveceType;
	}

	@Override
	public String toString() {
		return "Sensor{" +
				"sensorID='" + sensorID + '\'' +
				", uniqueID='" + uniqueID + '\'' +
				", timecollected=" + timecollected +
				", value=" + value +
				", deviceName='" + deviceName + '\'' +
				", deveceType='" + deveceType + '\'' +
				'}';
	}

	@Override
	public Object clone() {

		try {
			return (Sensor)super.clone();
		} catch (Exception e) {
			// either handle the exception or throw it
			return new Sensor(
					this.sensorID,
					this.uniqueID,
					this.timecollected,
					this.value,
					this.deviceName,
					this.deveceType
			);
		}
	}


}
