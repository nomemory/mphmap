package article;

import net.andreinc.jperhash.ReadOnlyMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Example03 {
    public static void main(String[] args) {
        Set<String> emperors =
                Set.of("Augustus", "Tiberius", "Caligula",
                        "Claudius", "Nero", "Vespasian",
                        "Titus", "Dominitian", "Nerva",
                        "Trajan", "Hadrian", "Antonious Pius",
                        "Marcus Aurelius", "Lucius Verus", "Commodus");

        // Creates a "normal map" from the given keys
        final Map<String, String> mp = new HashMap<>();
        emperors.forEach(emp -> {
            mp.put(emp, emp+"123");
        });

        // Creates a "read-only map" from the previous map
        final ReadOnlyMap<String, String> romp = ReadOnlyMap.snapshot(mp);
        emperors.forEach(emp -> {
            System.out.println(emp + ":" + romp.get(emp));
        });
    }
}
