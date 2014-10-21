package edu.ucsd.xmlrpc.xmlrpc.mapreduce;

import java.util.UUID;

import edu.ucsd.xmlrpc.xmlrpc.multiclient.MultiClient;

public class StreamMapper {

  private MultiClient client;
  private String uid;
  private int size;

  public StreamMapper(MultiClient client) {
    this.client = client;
    uid = UUID.randomUUID().toString();
  }

  public void processData(String handlerMethodName, Object...args) {
    client.executeStreamRequest(this.uid + '.' + size, handlerMethodName, args);
    size++;
  }
}