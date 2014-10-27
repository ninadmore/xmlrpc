package edu.ucsd.xmlrpc.xmlrpc.mapreduce;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import edu.ucsd.xmlrpc.xmlrpc.multiclient.MultiClient;

public class StreamMapper {

  private static final char DELIMITER = '.';

  MultiClient client;
  String jobId;
  private int size;

  public StreamMapper(MultiClient client) {
    this.client = client;
    jobId = UUID.randomUUID().toString();
  }

  public Iterator<String> getMapJobIdIterator() {
    return new MapJobIdIterator();
  }

  public int getSize() {
    return size;
  }

  public void processData(String handlerMethodName, Object...args) {
    client.executeStreamRequest(handlerMethodName, this.jobId + DELIMITER + size, args);
    size++;
  }

  private class MapJobIdIterator implements Iterator<String> {

    int jobIndex = 0;

    @Override
    public boolean hasNext() {
      return jobIndex < size;
    }

    @Override
    public String next() {
      if (hasNext()) {
        return jobId + DELIMITER + jobIndex++;
      }
      throw new NoSuchElementException("no more jobs");
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove not supported");
    }
  }
}
