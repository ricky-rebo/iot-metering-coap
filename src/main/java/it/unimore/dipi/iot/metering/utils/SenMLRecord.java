package it.unimore.dipi.iot.metering.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
+---------------+------+---------+
|         SenML | JSON | Type    |
+---------------+------+---------+
|     Base Name | bn   | String  |
|          Name | n    | String  |
|     Base Time | bt   | Number  |
|          Time | t    | Number  |
|   Update Time | ut   | Number  |
|     Base Unit | bu   | String  |
|          Unit | u    | String  |
|    Base Value | bv   | Number  |
|         Value | v    | Number  |
|  String Value | vs   | String  |
| Boolean Value | vb   | Boolean |
|    Data Value | vd   | String  |
|     Value Sum | s    | Number  |
|       Version | bver | Number  |
+---------------+------+---------+
*/

public class SenMLRecord {
	// Name
	@JsonProperty("bn") private String baseName;
	@JsonProperty("n") private String name;

	// Time
	@JsonProperty("bt") private Number baseTime;
	@JsonProperty("t") private Number time;
	@JsonProperty("ut") private Number updateTime;

	// Unit
	@JsonProperty("bu") private String baseUnit;
	@JsonProperty("u") private String unit;

	// Value
	@JsonProperty("bv") private Number baseValue;
	@JsonProperty("v") private Number value;
	@JsonProperty("vs") private String valueString;
	@JsonProperty("vb") private Boolean valueBoolean;
	@JsonProperty("vd") private String valueData;
	@JsonProperty("s") private Number sum; // Sum

	// Version
	@JsonProperty("bver") private Number version;

	public SenMLRecord() {
	}

	public SenMLRecord(String bn, Number bt, String bu, Number bv, Number bver, String n, String u, Number v, String vs, Boolean vb, String vd, Number s, Number t, Number ut) {
		this.baseName = bn;
		this.baseTime = bt;
		this.baseUnit = bu;
		this.baseValue = bv;
		this.version = bver;
		this.name = n;
		this.unit = u;
		this.value = v;
		this.valueString = vs;
		this.valueBoolean = vb;
		this.valueData = vd;
		this.sum = s;
		this.time = t;
		this.updateTime = ut;
	}

	public String getBn() {
		return baseName;
	}

	public void setBn(String bn) {
		this.baseName = bn;
	}

	public Number getBt() {
		return baseTime;
	}

	public void setBt(Number bt) {
		this.baseTime = bt;
	}

	public String getBu() {
		return baseUnit;
	}

	public void setBu(String bu) {
		this.baseUnit = bu;
	}

	public Number getBv() {
		return baseValue;
	}

	public void setBv(Number bv) {
		this.baseValue = bv;
	}

	public Number getBver() {
		return version;
	}

	public void setBver(Number bver) {
		this.version = bver;
	}

	public String getN() {
		return name;
	}

	public void setN(String n) {
		this.name = n;
	}

	public String getU() {
		return unit;
	}

	public void setU(String u) {
		this.unit = u;
	}

	public Number getV() {
		return value;
	}

	public void setV(Number v) {
		this.value = v;
	}

	public String getVs() {
		return valueString;
	}

	public void setVs(String vs) {
		this.valueString = vs;
	}

	public Boolean getVb() {
		return valueBoolean;
	}

	public void setVb(Boolean vb) {
		this.valueBoolean = vb;
	}

	public String getVd() {
		return valueData;
	}

	public void setVd(String vd) {
		this.valueData = vd;
	}

	public Number getS() {
		return sum;
	}

	public void setS(Number s) {
		this.sum = s;
	}

	public Number getT() {
		return time;
	}

	public void setT(Number t) {
		this.time = t;
	}

	public Number getUt() {
		return updateTime;
	}

	public void setUt(Number ut) {
		this.updateTime = ut;
	}

	@Override
	public String toString() {
		return String.format(
				"SenML {%s%s%s%s%s%s%s%s%s%s%s%s%s%s}",
				baseName != null ? "bn=" + baseName + "  " : "",
				baseTime != null ? "bt=" + baseTime + "  " : "",
				baseUnit != null ? "bu=" + baseUnit + "  " : "",
				baseValue != null ? "bv=" + baseValue + "  " : "",
				version != null ? "bver=" + version + "  " : "",
				name != null ? "n=" + name + "  " : "",
				unit != null ? "u=" + unit + "  " : "",
				value != null ? "v=" + value + "  " : "",
				valueString != null ? "vs=" + valueString + "  " : "",
				valueBoolean != null ? "vb=" + valueBoolean + "  " : "",
				valueData != null ? "vd=" + valueData + "  " : "",
				sum != null ? "s=" + sum + "  " : "",
				time != null ? "t=" + time + "  " : "",
				updateTime != null ? "ut=" + updateTime + "  " : ""
		);
	}

}