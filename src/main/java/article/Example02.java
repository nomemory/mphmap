package article;

import org.apache.commons.codec.digest.MurmurHash3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Example02 {
    public static void main(String[] args) {
        Set<String> emperors =
                Set.of("Augustus", "Tiberius", "Caligula",
                        "Claudius", "Nero", "Vespasian",
                        "Titus", "Dominitian", "Nerva",
                        "Trajan", "Hadrian", "Antonious Pius",
                        "Marcus Aurelius", "Lucius Verus", "Commodus");
        List<ArrayList<String>> buckets =
            Stream.generate(() -> new ArrayList<String>()).limit(5).toList();

        emperors.forEach(s -> {
            int hash = (MurmurHash3.hash32x86(s.getBytes(),0,s.getBytes().length, 0) & 0xffffff) % buckets.size();
            buckets.get(hash).add(s);
        });
        for (int i = 0; i < buckets.size(); i++) {
            System.out.printf("bucket[%d]=%s\n", i, buckets.get(i));
        }
    }
}
