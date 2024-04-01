package Project.OpenBook.WeightedRandomSelection;

import lombok.Getter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Getter
public class WeightedRandomBag<T extends Object> {

    class Entry {
        double accumulatedWeight;
        T object;
    }

    private Set<Entry> entries = new HashSet<>();
    private double accumulatedWeight;
    private Random rand = new Random();

    public void addEntry(T object, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.object = object;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public void removeEntry(Entry entry) {
        accumulatedWeight -= entry.accumulatedWeight;
        entries.remove(entry);
    }

    public T getRandom() {
        double r = rand.nextDouble() * accumulatedWeight;

        for (Entry entry : entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return null;
    }

    public Entry getRandomEntry() {
        double r = rand.nextDouble() * accumulatedWeight;

        for (Entry entry : entries) {
            if (entry.accumulatedWeight >= r) {
                return entry;
            }
        }
        return null;
    }
}
