package benchmarks;

import net.andreinc.jperhash.PHF;
import net.andreinc.jperhash.ReadOnlyMap;
import net.andreinc.mockneat.unit.objects.From;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 3, jvmArgs = {"-Xms6G", "-Xmx16G"})
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 5, time = 10)
public class HashFunctionBenchmark {

    private static Set<String> emperors =
            Set.of("Augustus", "Tiberius", "Caligula",
                    "Claudius", "Nero", "Vespasian",
                    "Titus", "Dominitian", "Nerva",
                    "Trajan", "Hadrian", "Antonious Pius",
                    "Marcus Aurelius", "Lucius Verus", "Commodus");

    private static PHF phf;

    static {
        phf = new PHF(1.0, 4, Integer.MAX_VALUE);
        phf.build(emperors, String::getBytes);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void testPHF(Blackhole bh) {
        emperors.forEach(e -> phf.hash(e.getBytes()));
    }

    @Benchmark
    public void testHashCode(Blackhole bh) {
        emperors.forEach(e -> e.hashCode());
    }
}
