/**
 * 
 */
package org.adorsys.adpharma.platform.rest;

import java.util.List;

import org.adorsys.adpharma.platform.rest.exchanges.PlatformRestException;

/**
 * @author w2b
 *
 */
public interface PlatformResponseEntity<T> {
	public boolean hasExceptionOccured();
	public List<String> getExceptions();
	public T getResponseObject();
	public boolean add(PlatformRestException exception);
	public boolean remove(PlatformRestException exception);
	public void clearExceptions();
	public String toJson();
}
