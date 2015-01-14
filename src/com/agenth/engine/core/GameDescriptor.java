package com.agenth.engine.core;

import java.io.Serializable;

public class GameDescriptor {
	
	/** Version for the serialized data when there is no serialized data */
	public static final int INVALID_DATA_VERSION = -1;
	
	
	/** Name of this saved game */
	private String mName;
	/** Actual data needed to reconstruct the game state. This attribute is optional */
	private Serializable mData;
	/** Version number for the data (ensures compatibility) */
	private int mDataVersion = INVALID_DATA_VERSION;
	
	
	// TODO thumbnail, date, whatever useful info
	
	public GameDescriptor(String name) {
		mName = name;
	}
	
	public GameDescriptor(String name, Serializable data, int dataVersion) {
		mName = name;
		mData = data;
		mDataVersion = dataVersion;
	}
	
	public GameDescriptor(Serializable data, int dataVersion) {
		mData = data;
		mDataVersion = dataVersion;
	}
	
	public String getName() {
		return mName;
	}
	
	public int getDataVersion() {
		return mDataVersion;
	}
	
	public Serializable getData() {
		return mData;
	}
}
