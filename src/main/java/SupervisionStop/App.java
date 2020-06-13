package SupervisionStop;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.apache.log4j.BasicConfigurator;

public class App {

    public static void main(String[] args) {
        BasicConfigurator.configure();

        ActorSystem system = ActorSystem.create("supervisor-system");

        ActorRef supervisor = system.actorOf(Supervisor.props(), "supervisor");

        for(int i=0; i<10;i++) {
            supervisor.tell(new UntrustworthyChild.Command(), ActorRef.noSender());
        }
    }

}
