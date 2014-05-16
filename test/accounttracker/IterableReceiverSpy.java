package accounttracker;

import accounttracker.usecases.boundaries.DebitData;
import accounttracker.usecases.boundaries.ReadAllDebitReceiver;

import java.util.List;

public class IterableReceiverSpy implements ReadAllDebitReceiver {

    List<DebitData> debitList;

    public void sendDebitList(List<DebitData> debitList) {
        this.debitList = debitList;
    }
}
