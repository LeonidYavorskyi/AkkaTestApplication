package test.akka.application.actors;

import akka.actor.UntypedActor;
import test.akka.application.messages.Finish;
import test.akka.application.messages.ReduceData;
import test.akka.application.messages.Result;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

/*
* Aggregate actor receives the reduced data list from the Master actor and aggregates
* it into one big list. Aggregate actor will maintain a state variable that will hold the
* list of items and get updated on receipt of the reduced data list message.
*/
public class AggregateActor extends UntypedActor {

    private Map<Integer, Integer> finalReducedMap = new TreeMap<>();
    private final String aggregatedFileURL;

    public AggregateActor(String aggregatedFileURL) {
        this.aggregatedFileURL = aggregatedFileURL;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ReduceData) {
            getSender().tell(new Result(), getSelf());
            ReduceData reduceData = (ReduceData) message;
            aggregateInMemoryReduce(reduceData.getReduceDataList());
            getContext().parent().tell(new Result());
        } else if (message instanceof Finish) {
            writeToFile(finalReducedMap);
            getContext().parent().forward(message, getContext());
        } else {
            unhandled(message);
        }
    }

    private void writeToFile(Map<Integer, Integer> reducedList) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(aggregatedFileURL))) {
            reducedList
                    .entrySet()
                    .stream()
                    .map(entry -> new StringBuilder().append(entry.getKey()).append(";").append(entry.getValue()).append(";\n").toString())
                    .forEach(a -> writeLine(writer, a));
        }
    }

    private void aggregateInMemoryReduce(Map<Integer, Integer> reducedList) {
        Integer count;

        for (Map.Entry<Integer, Integer> line : reducedList.entrySet()) {
            if (finalReducedMap.containsKey(line.getKey())) {
                count = line.getValue() + finalReducedMap.get(line.getKey());
                finalReducedMap.put(line.getKey(), count);
            } else {
                finalReducedMap.put(line.getKey(), line.getValue());
            }
        }
    }

    private void writeLine(BufferedWriter writer, String line) {
        try {
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
