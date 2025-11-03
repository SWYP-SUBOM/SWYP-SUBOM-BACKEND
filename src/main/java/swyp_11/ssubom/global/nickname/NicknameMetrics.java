//package swyp_11.ssubom.writing.service.nickname;
//
//import org.springframework.stereotype.Component;
//import org.w3c.dom.css.Counter;
//
//@Component
//public class NicknameMetrics {
//    private final Counter attemptsCounter;
//    private final Counter failuresCounter;
//    private final Counter successCounter;
//    private final Histogram attemptDistribution;
//
//    public NicknameMetrics(MeterRegistry registry) {
//        this.attemptsCounter = Counter.builder("nickname.generation.attempts")
//                .description("Total number of generation attempts")
//                .register(registry);
//
//        this.failuresCounter = Counter.builder("nickname.generation.failures")
//                .description("Total number of generation failures")
//                .register(registry);
//
//        this.successCounter = Counter.builder("nickname.generation.success")
//                .tag("phase", "unknown")
//                .description("Total number of successful generations by phase")
//                .register(registry);
//
//        this.attemptDistribution = Histogram.builder("nickname.generation.attempts.distribution")
//                .description("Distribution of attempts needed for success")
//                .register(registry);
//    }
//
//    public void recordSuccess(int attempts, int phase) {
//        attemptsCounter.increment(attempts);
//        attemptDistribution.record(attempts);
//
//        Counter.builder("nickname.generation.success")
//                .tag("phase", String.valueOf(phase))
//                .register(new SimpleMeterRegistry())
//                .increment();
//    }
//
//    public void recordFailure() {
//        failuresCounter.increment();
//    }
//
//}
