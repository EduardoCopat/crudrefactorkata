package accounttracker.usecases;


import accounttracker.usecases.boundaries.DebitData;
import accounttracker.usecases.boundaries.DebitRequest;
import accounttracker.usecases.boundaries.DebitStore;
import accounttracker.usecases.boundaries.ReadAllDebitReceiver;
import accounttracker.usecases.entities.Debit;

import java.util.ArrayList;
import java.util.List;

public class ReadAllDebitsCommand {
    DebitRequest request;
    ReadAllDebitReceiver receiver;
    DebitStore store;

    public ReadAllDebitsCommand(DebitRequest request, ReadAllDebitReceiver receiver, DebitStore store) {
        this.request = request;
        this.receiver = receiver;
        this.store = store;
    }

    public void execute(){
        List<Debit> debitList = store.readAll();
        List<DebitData> debitsData = new ArrayList<DebitData>();

        maptoReturnModel(debitList, debitsData);

        receiver.sendDebitList(debitsData);
    }

    private void maptoReturnModel(List<Debit> debitList, List<DebitData> debitsData) {
        for(Debit debit : debitList){
            DebitData debitData = new DebitData();
            debitData.id = debit.id();
            debitData.description = debit.description();
            debitData.value = debit.value();
            debitsData.add(debitData);
        }
    }
}
