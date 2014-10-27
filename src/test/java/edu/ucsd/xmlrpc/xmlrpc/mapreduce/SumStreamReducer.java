package edu.ucsd.xmlrpc.xmlrpc.mapreduce;

import java.util.LinkedList;
import java.util.List;

public class SumStreamReducer extends StreamReducer {

  List</*jobId*/String> args = new LinkedList<>();

  public SumStreamReducer(StreamMapper mapper) {
    super(mapper);
    args.add(null);
    if (getMapJobIdIterator().hasNext()) {
      args.add(getMapJobIdIterator().next());
    }
  }

  @Override
  protected boolean hasNextFunction() {
    return getMapJobIdIterator().hasNext();
  }

  @Override
  protected TransformFunction nextFunction() {
    args.remove(0);
    args.add(getMapJobIdIterator().next());
    return new TransformFunction("SampleHandler.sum", args.toArray(new String[0]));
  }
}
