package test.akka.application.actors;

import akka.actor.UntypedActor;
import test.akka.application.messages.Item;
import test.akka.application.messages.MapData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
* MapActor sends the mapped data list to the Master actor,
* who will send it to Reduce actor.
*/
public class MapActor extends UntypedActor {

    private static final String SEPARATOR = "[;]+";

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            String work = (String) message;
            // map the items in the file and send the result to MasterActor
            getSender().tell(evaluateExpression(work));
        } else {
            unhandled(message);
        }
    }

    private MapData evaluateExpression(String fileURL) throws IOException, URISyntaxException {
        List<Item> dataList;
        try (Stream<String> stream = Files.lines(Paths.get(ClassLoader.getSystemResource(fileURL).toURI()))) {
            dataList = stream
                    .map(line -> line.split(SEPARATOR)) // Stream<String[]>
                    .map(array -> new Item(Integer.valueOf(array[0]), Integer.valueOf(array[1]))) // Item
                    .collect(Collectors.toList());
        }
        return new MapData(dataList);
    }
}
