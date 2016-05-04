package com.mindtree.dao;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public final class ConnectionH {
MongoClient mongoClient;
	public MongoClient getMongoClient() {
	return mongoClient;
}
public void setMongoClient(MongoClient mongoClient) {
	this.mongoClient = mongoClient;
}
	public static DB getConnection() {
DB db=null;
		try {

			// To connect to mongodb server
			MongoClient mongoClient = new MongoClient("localhost", 27017);

			// Now connect to your databases
			db = mongoClient.getDB("test");
			System.out.println("Connect to database successfully");
			// boolean auth = db.authenticate(myUserName, myPassword);
			// System.out.println("Authentication: "+auth);
			//DBCollection coll = db.createCollection("commands", null);
		
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return db;
	}
}