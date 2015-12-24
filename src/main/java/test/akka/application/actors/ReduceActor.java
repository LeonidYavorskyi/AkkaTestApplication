package test.akka.application.actors;

import akka.actor.UntypedActor;
import test.akka.application.messages.Item;
import test.akka.application.messages.MapData;
import test.akka.application.messages.ReduceData;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
* Map actor will send the MapData message to the Master actor, who passes it to the Reduce actor.
* The Reduce actor will go through the list of items and reduce for duplicate items,
* and accordingly increase the number of instances counted for such items.
* The reduced list is then sent back to the Master actor.
*/
public class ReduceActor extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof MapData) {
            MapData mapData = (MapData) message;
            // reduce the incoming data and forward the result to Master actor
            getSender().tell(reduce(mapData.getDataList()));
        } else {
            unhandled(message);
        }
    }

    private ReduceData reduce(List<Item> dataList) {

        Map<Integer, Integer> reducedMap = new TreeMap<>();

        for (Item item : dataList) {
            if (reducedMap.containsKey(item.getId())) {
                Integer value = reducedMap.get(item.getId());
                value += item.getCount();
                reducedMap.put(item.getId(), value);
            } else {
                reducedMap.put(item.getId(), item.getCount());
            }
        }

        return new ReduceData(reducedMap);
    }
}
