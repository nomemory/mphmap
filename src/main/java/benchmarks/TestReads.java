package benchmarks;


import net.andreinc.mockneat.abstraction.MockUnitString;
import net.andreinc.mockneat.unit.objects.From;
import net.andreinc.jperhash.ReadOnlyMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.andreinc.mockneat.unit.text.Strings.strings;
import static net.andreinc.mockneat.unit.text.Words.words;
import static net.andreinc.mockneat.unit.types.Ints.ints;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 3, jvmArgs = {"-Xms6G", "-Xmx16G"})
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 5, time = 10)
public class TestReads {

    private Map<String, String> map;
    private ReadOnlyMap<String, String> readOnlyMap;
    private MockUnitString stringsGenerator = words().map(s -> s + ints().get()).mapToString();
    private List<String> keys = stringsGenerator.list(20_000_000).get();

    @Setup(Level.Trial)
    public void initMaps() {
        this.map = new HashMap<>();
        keys.forEach(key -> map.put(key, "abc"));
        this.readOnlyMap = ReadOnlyMap.snapshot(map);
    }

    @Benchmark
    public void testGetInMap(Blackhole bh) {
        bh.consume(map.get(From.from(keys).get()));
    }

    @Benchmark
    public void testGetInReadOnlyMap(Blackhole bh) {
        bh.consume(map.get(From.from(keys).get()));
    }
}
