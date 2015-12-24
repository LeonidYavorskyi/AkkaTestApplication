package test.akka.application.actors;

import akka.actor.*;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
import test.akka.application.messages.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
* Master actor is a Supervisor actor and responsible for the instantiation of the child actors.
* Master actor is the gateway for all messages that are passed on to the other actors.
*/
public class MasterActor extends UntypedActor {

    private final long start = System.currentTimeMillis();

    private final List<String> inputFilesURLs;

    private final ActorRef mapRouter;
    private final ActorRef reduceRouter;
    private final ActorRef aggregateActor;
    private final ActorRef listener;

    private int nrOfResults;

    public MasterActor(final int nrOfMapActors, final int nrOfReduceActors, List<String> inputFilesURLs, final String aggregatedFileURL, ActorRef listener) {
        this.inputFilesURLs = inputFilesURLs;
        this.listener = listener;

        mapRouter = this.getContext().actorOf(new Props(MapActor.class).withRouter(new RoundRobinRouter(nrOfMapActors)));
        reduceRouter = this.getContext().actorOf(new Props(ReduceActor.class).withRouter(new RoundRobinRouter(nrOfReduceActors)));
        aggregateActor = getContext().actorOf(new Props(new UntypedActorFactory() {
            @Override
            public Actor create() throws Exception {
                return new AggregateActor(aggregatedFileURL);
            }
        }), "aggregate");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Start) {
            for (String url : inputFilesURLs) {
                mapRouter.tell(url, getSelf());
            }
        } else if (message instanceof MapData) {
            reduceRouter.tell(message, getSelf());
        } else if (message instanceof ReduceData) {
            aggregateActor.tell(message);
        } else if (message instanceof Result) {

            nrOfResults++;

            if (nrOfResults == inputFilesURLs.size()) {
                aggregateActor.tell(new Finish());
            }

        } else if (message instanceof Finish) {
            Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            listener.tell(duration, getSelf());
            // Stops this actor and all its supervised children
            getContext().stop(getSelf());
        } else {
            unhandled(message);
        }
    }
}
