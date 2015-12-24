package test.akka.application.messages;

/*
* Finish is the message that is passed from the Master actor to the Aggregate actor and back.
* The Aggregate actor will write all data into the file and then Master actor will stop itself.
*/
public final class Finish {
}
