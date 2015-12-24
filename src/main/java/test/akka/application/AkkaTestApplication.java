package test.akka.application;

import akka.actor.*;
import test.akka.application.actors.Listener;
import test.akka.application.actors.MasterActor;
import test.akka.application.messages.Start;

import java.util.Arrays;
import java.util.List;

public class AkkaTestApplication {

    private static final String INPUT_FILE_URL_1 = "testInputFile1.txt";
    private static final String INPUT_FILE_URL_2 = "testInputFile2.txt";
    private static final String INPUT_FILE_URL_3 = "testInputFile3.txt";
    private static final String INPUT_FILE_URL_4 = "testInputFile4.txt";
    private static final String INPUT_FILE_URL_5 = "testInputFile5.txt";
    private static final String OUTPUT_FILE_URL = "src/main/resources/outputFile.txt";

    private static final int MAP_ACTORS_COUNT = 10;
    private static final int REDUCE_ACTORS_COUNT = 10;

    public static void main(String[] args) throws Exception {

        AkkaTestApplication application = new AkkaTestApplication();

        List<String> urls = Arrays.asList(INPUT_FILE_URL_1, INPUT_FILE_URL_2,
                INPUT_FILE_URL_3, INPUT_FILE_URL_4, INPUT_FILE_URL_5);

        application.mergeFileData(MAP_ACTORS_COUNT, REDUCE_ACTORS_COUNT, urls, OUTPUT_FILE_URL);
    }

    private void mergeFileData(final int nrOfMapActors, final int nrOfReduceActors, final List<String> inputFilesURL, final String outputFileURL) throws
            Exception {

        // Create an Akka system
        ActorSystem system = ActorSystem.create("MergeFileDataSystem");

        // create the result listener, which will print the result and shutdown the system
        final ActorRef listener = system.actorOf(new Props(Listener.class), "listener");

        // create the master
        ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                return new MasterActor(nrOfMapActors, nrOfReduceActors, inputFilesURL, outputFileURL, listener);
            }
        }), "master");

        master.tell(new Start());
    }
}
