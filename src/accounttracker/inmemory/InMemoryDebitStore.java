package accounttracker.inmemory;

import accounttracker.usecases.boundaries.DebitData;
import accounttracker.usecases.boundaries.DebitNotFoundException;
import accounttracker.usecases.boundaries.DebitStore;
import accounttracker.usecases.entities.Debit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDebitStore implements DebitStore {
    private Map<String, Debit> debits;
    private int incrementalId;

    public InMemoryDebitStore(){
        debits = new HashMap<String, Debit>();
    }

    public Debit create() {
        return new InMemoryDebit(String.valueOf(++incrementalId));
    }

    public Debit read(String id) {
        if (debits.containsKey(id)) return debits.get(id);
        else throw new DebitNotFoundException(id);
    }

    public void delete(String id) {
        debits.remove(id);
    }

    public Debit save(Debit debit) {
        return debits.put(debit.id(), debit);
    }

    @Override
    public List<Debit> readAll() {
        List<DebitData> debitsData = new ArrayList<DebitData>();
        return  generateDebitList();
    }

    private List<Debit> generateDebitList() {
        List<Debit> debitList = new ArrayList<Debit>();

        for (String key: debits.keySet()) {
            debitList.add(debits.get(key));

        }
        return debitList;
    }


}
