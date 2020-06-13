import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

/**
 * This demonstrates the safety of actors from a concurrency perspective
 * The actor processes each message in the order it is received in complete isolation
 * This reliably counts to 2500 every time
 */
public class Actor {
    static class Counter extends AbstractActor {
        static class Message {
        }

        private int counter = 0;

        private void onMessage(Message message) {
            counter++;
            System.out.println("Increased counter to " + counter);
        }

        public Receive createReceive() {
            return ReceiveBuilder.create()
                    .match(Message.class, this::onMessage)
                    .build();
        }

        public static Props props() {
            return Props.create(Counter.class);
        }
    }

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("counter-system");

        ActorRef counter = system.actorOf(Counter.props(), "counter");

        for (int i = 0; i < 50; i++) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j < 50; j++) {
                            counter.tell(new Counter.Message(), ActorRef.noSender());
                        }
                    }
                }).start();
        }
    }
}
