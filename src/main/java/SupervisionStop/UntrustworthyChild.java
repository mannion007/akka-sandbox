package SupervisionStop;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

public class UntrustworthyChild extends AbstractActor {

    public static class Command {}

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Command.class, this::onMessage)
                .build();
    }

    public static Props props() {
        return Props.create(UntrustworthyChild.class);
    }

    private void onMessage(Command message) {
        throw new RuntimeException("Something went wrong, I cannot be trusted");
    }
}
