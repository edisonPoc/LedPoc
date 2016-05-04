package com.mindtree.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class DeviceDaoImpl {
	
public String checkCommandStatus(int counter)
{
	String command="";
	MongoClient mongoClient = new MongoClient("localhost", 27017);

	// Now connect to your databases
	DB db = mongoClient.getDB("test");
	System.out.println("Connect to database successfully");
	DBCollection table = db.getCollection("commands");
	BasicDBObject searchQuery = new BasicDBObject();
	searchQuery.put("count", counter);
	DBCursor cursor = table.find(searchQuery);
	while (cursor.hasNext()) {
		BasicDBObject obj = (BasicDBObject) cursor.next();
		command=obj.getString("message");
	}
	mongoClient.close();
	return command;
}
}
