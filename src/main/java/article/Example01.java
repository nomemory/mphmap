package article;

import net.andreinc.jperhash.PHF;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Example01 {
    public static void main(String[] args) {
        Set<String> emperors =
                Set.of("Augustus", "Tiberius", "Caligula",
                        "Claudius", "Nero", "Vespasian",
                        "Titus", "Dominitian", "Nerva",
                        "Trajan", "Hadrian", "Antonious Pius",
                        "Marcus Aurelius", "Lucius Verus", "Commodus");

//        List<ArrayList<String>> buckets =
//                Stream.generate(() -> new ArrayList<String>()).limit(emperors.size()).toList();
//
//        emperors.forEach(s -> {
//            int hash = (s.hashCode() & 0xfffffff) % buckets.size();
//            buckets.get(hash).add(s);
//        });
//        for (int i = 0; i < buckets.size(); i++) {
//            System.out.printf("bucket[%d]=%s\n", i, buckets.get(i));
//        }

        // We create the MPHF based on the input
            PHF phf = new PHF(1.0, 4, Integer.MAX_VALUE);
            phf.build(emperors, String::getBytes);

            final String[] buckets = new String[emperors.size()];
            emperors.forEach(emperor -> buckets[phf.hash(emperor.getBytes())] = emperor);
            for (int i = 0; i < buckets.length; i++) {
                System.out.printf("bucket[%d]=%s\n", i, buckets[i]);
            }
    }
}
