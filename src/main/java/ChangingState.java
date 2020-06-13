import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

/**
 * This demonstrates how actors can change internal state based on messages recieved
 * As it is, this is not too impressive given that this could be implemented with straightforward method calls.
 * The important thing of note is that this solution is completely safe from a concurrency perspective
 */
public class ChangingState {

    static class AlarmActor extends AbstractActor {

        private String password;
        private boolean isArmed;

        static class Arm {
            final String password;

            public Arm(String password) {
                this.password = password;
            }
        }

        static class Disarm {
            final String password;

            public Disarm(String password) {
                this.password = password;
            }
        }

        static class Trip { }

        @Override
        public Receive createReceive() {
            return ReceiveBuilder.create()
                    .match(Arm.class, this::onMessage)
                    .match(Disarm.class, this::onMessage)
                    .match(Trip.class, this::onMessage)
                    .build();
        }

        private void onMessage(Arm message) {
            password = message.password;
            isArmed = true;

            System.out.printf("System armed with password [%s]\n", password);
        }

        private void onMessage(Disarm message) {
            if (password.equals(message.password)) {
                isArmed = false;
                System.out.println("System disarmed");
            } else {
                System.out.println("Attempt to disarm the system with an invalid password");
            }

        }

        private void onMessage(Trip message) {
            if (isArmed) {
                System.out.println("nenenenene ALARM ALARM ALARM");
            } else {
                System.out.println("boop");
            }
        }

        public static Props props() {
            return Props.create(AlarmActor.class);
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello world");

        ActorSystem actorSystem = ActorSystem.create("alarm-system");

        ActorRef alarm = actorSystem.actorOf(AlarmActor.props(), "alarm-actor");

        alarm.tell(new AlarmActor.Trip(), ActorRef.noSender());
        alarm.tell(new AlarmActor.Arm("el-barto"), ActorRef.noSender());
        alarm.tell(new AlarmActor.Trip(), ActorRef.noSender());
        alarm.tell(new AlarmActor.Disarm("el-barto"), ActorRef.noSender());
        alarm.tell(new AlarmActor.Trip(), ActorRef.noSender());
    }
}
