package accounttracker.usecases.boundaries;

import accounttracker.usecases.entities.Debit;

import java.util.List;

public interface DebitStore {
    Debit create();
    Debit read(String id) throws DebitNotFoundException;
    void delete(String id);
    Debit save(Debit debit);

    List<Debit> readAll();
}
