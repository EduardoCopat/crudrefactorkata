package accounttracker.usecases.boundaries;

import java.util.List;

public interface ReadAllDebitReceiver {
    public void sendDebitList(List<DebitData> debitList);
}
