package edu.ucsd.xmlrpc.xmlrpc.mapreduce;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientRequestImpl;
import edu.ucsd.xmlrpc.xmlrpc.multiserver.CoreHandler;

public abstract class StreamReducer {

  private static final char DELIMITER = '.';
  // jobs encoded as "uuid.##"
  private static final int JOB_INDEX = UUID.randomUUID().toString().length() + 1;

  private StreamMapper mapper;
  private Iterator<String> mapJobIdIterator;
  private int size;

  public static int getjobIndex(XmlRpcClientRequestImpl request) {
    try {
      return -Integer.parseInt(request.getJobID().substring(JOB_INDEX)) - 1;
    } catch (Exception ignore) {}
    return -1;
  }

  public StreamReducer(StreamMapper mapper) {
    this.mapper = mapper;
    this.mapJobIdIterator = mapper.getMapJobIdIterator();
  }

  /** Automatically instantiated by constructor. Does not create a new instance of the iterator. */
  public Iterator<String> getMapJobIdIterator() {
    return mapJobIdIterator;
  }

  /** Results will be returned to the client callback function on each call to next(). */
  public Iterator<Class<Void>> getResultIterator() {
    return new ResultIterator();
  }

  public int getInputSize() {
    return mapper.getSize();
  }

  public int getOutputSize() {
    return size;
  }

  /** Start running the transformer */
  public void start() {
    init();
    while (hasNextFunction()) {
      TransformFunction function = nextFunction();
      mapper.client.executeJobIdFunction(
          function.methodName, mapper.jobId + DELIMITER + (-size - 1), function.args);
      size++;
    }
  }

  /** Override if needed. */
  protected void init() {}

  protected abstract boolean hasNextFunction();

  /** Returns the next transform function to be processed. */
  protected abstract TransformFunction nextFunction();

  private class ResultIterator implements Iterator<Class<Void>> {

    int jobIndex = -1;
    String baseJobId = UUID.randomUUID().toString();

    @Override
    public boolean hasNext() {
      return -jobIndex <= size;
    }

    @Override
    public Class<Void> next() {
      if (hasNext()) {
        String fetchJobId = mapper.jobId + DELIMITER + jobIndex;
        String execJobId = baseJobId + DELIMITER + jobIndex;
        jobIndex--;
        mapper.client.executeAsyncJob(CoreHandler.FETCH_RESULT_METHOD, execJobId, fetchJobId);
        return Void.TYPE;
      }
      throw new NoSuchElementException("no more jobs");
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove not supported");
    }
  }
}
