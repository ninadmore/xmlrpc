package edu.ucsd.xmlrpc.xmlrpc.mapreduce;

public class TransformFunction {

  String[] args;
  String methodName;

  /** Args is a list of jobIds whose results are used to invoke function specified by methodName. */
  public TransformFunction(String methodName, String[] args) {
    this.args = args;
    this.methodName = methodName;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(methodName)
      .append(" called with ");
    for (String jobId : args) {
      sb.append(jobId)
        .append(", ");
    }
    return sb.toString();
  }
}
