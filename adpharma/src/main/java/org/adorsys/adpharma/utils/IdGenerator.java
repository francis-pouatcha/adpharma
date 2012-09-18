package org.adorsys.adpharma.utils;

import java.util.UUID;

import org.hibernate.id.UUIDGenerator;

/**
 * The id generator is a class that generates entity id's based on
 * a synchronized time, the bean name and server id.
 *  
 * @author fpo
 *
 */
public class IdGenerator {
	
	public static final String generateId(){
		return UUID.randomUUID().toString();
	}

}
