package benchmarks;


import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Benchmark {
    public static void main(String[] args) throws IOException, RunnerException {
        Options options = new OptionsBuilder()
                // Benchmarks to include
                .include(TestReads.class.getName())
                // Configuration
                .timeUnit(TimeUnit.MICROSECONDS)
                .shouldDoGC(true)
                .resultFormat(ResultFormatType.JSON)
                .addProfiler(GCProfiler.class)
                .result("benchmarks_" + System.currentTimeMillis() + ".json")
                .build();

        new Runner(options).run();
    }
}
