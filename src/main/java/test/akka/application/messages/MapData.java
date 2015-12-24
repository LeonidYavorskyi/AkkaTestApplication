package test.akka.application.messages;

import java.util.List;

/*
* MapData is the message that is passed from the Map actor to the Reduce actor.
* The message consists of a list of the Item objects.
*/
public final class MapData {

    private final List<Item> dataList;

    public MapData(List<Item> dataList) {
        this.dataList = dataList;
    }

    public List<Item> getDataList() {
        return dataList;
    }
}
