package edu.ucsd.xmlrpc.xmlrpc.server;

import java.util.LinkedList;

import edu.ucsd.xmlrpc.xmlrpc.mapreduce.StreamMapper;
import edu.ucsd.xmlrpc.xmlrpc.multiclient.MultiClient;

/**
 * Work in progress
 */

public class SumMapper extends StreamMapper {

	int taskSize = 0;

	public SumMapper(MultiClient client, int[] numbers) {
		super(client);

		dataStream = new LinkedList<Object>();
		for (Object n : numbers) {
			dataStream.add(n);
		}
		taskSize = numbers.length;
	}

	@Override
	public Object map() {
		if (taskSize == 0)
			return 0;

		while (taskSize > 1) { //TODO remove busy wait
			synchronized(dataStreamLock) {
				if (dataStream.size() > 1) {
					Integer i1 = (Integer) dataStream.remove(0);
					Integer i2 = (Integer) dataStream.remove(0);

					client.executeAsync("SampleHandler.sum", i1, i2);

					taskSize--;
				}
			}
		}

		//while (dataStream.size()==0); Doesn't work
		while (dataStream.size()==0){System.out.print("");}//TODO remove busy wait

		return dataStream.remove(0);
	}

}
