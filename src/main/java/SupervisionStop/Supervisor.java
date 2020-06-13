package SupervisionStop;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.*;

public class Supervisor extends AbstractActor {

    private static final int RETRY_LIMIT = 5;
    private static final Duration WITHIN = Duration.create("5 seconds");

    // Restart strategy will attempt the tell upto RETRY_LIMIT times, subsequent messages go to deadletter
    private static final OneForOneStrategy RESTART_STRATEGY = new OneForOneStrategy(
            RETRY_LIMIT,
            WITHIN,
            DeciderBuilder.match(RuntimeException.class, ex -> restart()).build()
        );

    // Stop strategy will prevent the child processing a message more than once, subsequent messages go to deadletter
    private static final OneForOneStrategy STOP_STRATEGY = new OneForOneStrategy(
            DeciderBuilder.match(RuntimeException.class, ex -> stop()).build()
    );

    // Resume strategy will allow the child to keep processing messages, even though it has been seen to fail
    private static final OneForOneStrategy RESUME_STRATEGY = new OneForOneStrategy(
            DeciderBuilder.match(RuntimeException.class, ex -> resume()).build()
    );

    final ActorRef child = getContext().actorOf(UntrustworthyChild.props(), "child");

    public static Props props() {
        return Props.create(Supervisor.class);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .matchAny(any -> child.forward(any, getContext()))
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return RESUME_STRATEGY;
    }
}
