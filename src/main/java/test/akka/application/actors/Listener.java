package test.akka.application.actors;

import akka.actor.UntypedActor;
import akka.util.Duration;
/*
* Listener shuts down the ActorSystem.
*/
public class Listener extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Duration) {
            Duration duration = (Duration) message;
            System.out.println(duration);
            getContext().system().shutdown();
        } else {
            unhandled(message);
        }
    }
}
