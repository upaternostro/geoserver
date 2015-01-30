package it.phoops.geoserver.ols.solr.utils;

import java.util.ArrayList;

/**
 * Created by davide.cesaroni on 04/12/14.
 */
public class SolrBeanResultsList extends ArrayList<OLSAddressBean> {

	private long numFound = 0L;
	private long start = 0L;
	private Float maxScore = null;


	public long getNumFound() {
		return numFound;
	}

	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public Float getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Float maxScore) {
		this.maxScore = maxScore;
	}

	public String toString() {
		return "{numFound=" + this.numFound + ",start=" + this.start + (this.maxScore != null?",maxScore=" + this.maxScore:"") + ",docs=" + super.toString() + "}";
	}

}
