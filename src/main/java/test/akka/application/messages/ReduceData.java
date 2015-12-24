package test.akka.application.messages;

import java.util.Map;

/*
 * ReduceData is the message passed between the Reduce actor and the Aggregate actor.
 * The Reduce actor will reduce the message passed in MapData
 * and pass the results as ReduceData to the Aggregate actor.
 */
public final class ReduceData {

    private final Map<Integer, Integer> reduceDataList;

    public ReduceData(Map<Integer, Integer> reduceDataList) {
        this.reduceDataList = reduceDataList;
    }

    public Map<Integer, Integer> getReduceDataList() {
        return reduceDataList;
    }

}
