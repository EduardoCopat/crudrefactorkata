package accounttracker;

import accounttracker.inmemory.InMemoryDebitStore;
import accounttracker.usecases.*;
import accounttracker.usecases.boundaries.DebitData;
import accounttracker.usecases.boundaries.DebitNotFoundException;
import accounttracker.usecases.boundaries.DebitStore;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DebitCRUDTest {
    private ReceiverSpy receiver;
    public  IterableReceiverSpy iterableReceiver;
    private DebitStore debitStore = new InMemoryDebitStore();
    private int numberOfDebits;

    private void createDebit(double value, String description) {
        new CreateDebitCommand(new RequestStub(value, description), receiver, debitStore).execute();
    }

    private void readDebit(String id) {
        new ReadDebitCommand(new RequestStub(id), receiver, debitStore).execute();
    }

    private void updateDebit(double value, String description) {
        new UpdateDebitCommand(new RequestStub(receiver.id, value, description), receiver, debitStore).execute();
    }

    private void deleteDebit() {
        new DeleteDebitCommand(new RequestStub(receiver.id), debitStore).execute();
    }

    private void assertReturnedError(String... messages) {
        assertEquals(messages.length, receiver.messages.size());
        assertArrayEquals(messages, receiver.messages.toArray());
        receiver.messages.clear();
    }

    private void assertDebitAttributes(String id, double value, String description) {
        readDebit(id);
        assertEquals(value, receiver.value, 0.01);
        assertEquals(description, receiver.description);
    }

    private void assertDebitNotFound(String id) {
        try {
            readDebit(id);
            fail();
        } catch (DebitNotFoundException exception) {
            assertEquals(id, exception.debitId());
        }
    }

    @Before
    public void setUp() {
        receiver = new ReceiverSpy();
        iterableReceiver = new IterableReceiverSpy();
    }

    @Test
    public void whenCreatingWithInvalidValue_mustReturnMessage() {
        createDebit(0d, "Free food");
        assertNull(receiver.id);
        assertReturnedError("valueMustBeGreaterThanZero");

        createDebit(-1d, "Free food");
        assertNull(receiver.id);
        assertReturnedError("valueMustBeGreaterThanZero");
    }

    @Test
    public void whenCreatingWithInvalidDescription_mustReturnMessage() {
        createDebit(100d, "");
        assertNull(receiver.id);
        assertReturnedError("descriptionMustNotBeEmpty");

        createDebit(100d, null);
        assertNull(receiver.id);
        assertReturnedError("descriptionMustNotBeEmpty");
    }

    @Test
    public void whenReadingWithTheReturnedId_mustReturnAttributes() {
        createDebit(10d, "Lunch");
        assertDebitAttributes(receiver.id, 10d, "Lunch");
    }

    @Test
    public void whenCreatingMultiples_shouldBeAbleToReadThemOutOfOrder() {
        createDebit(20d, "Dinner");
        String id2 = receiver.id;
        createDebit(10d, "Lunch");
        String id1 = receiver.id;
        assertDebitAttributes(id1, 10d, "Lunch");
        assertDebitAttributes(id2, 20d, "Dinner");
    }

    @Test
    public void afterUpdatingDebit_mustReturnNewAttributes() {
        createDebit(12d, "Snack");
        updateDebit(12.5, "Late Snack");
        assertDebitAttributes(receiver.id, 12.5, "Late Snack");
    }

    @Test
    public void whenUpdatingWithInvalidValue_mustReturnMessage() {
        createDebit(1d, "Cheap food");
        updateDebit(0d, "Free Food");
        assertReturnedError("valueMustBeGreaterThanZero");

        updateDebit(-1d, "They Paid Me To Eat Their Food");
        assertReturnedError("valueMustBeGreaterThanZero");
    }

    @Test
    public void whenUpdatingWithInvalidDescription_mustReturnMessage() {
        createDebit(1d, "Cheap food");
        updateDebit(1d, "");
        assertReturnedError("descriptionMustNotBeEmpty");

        updateDebit(1d, null);
        assertReturnedError("descriptionMustNotBeEmpty");
    }

    @Test
    public void whenUpdatingWithError_mustNotUpdate() {
        createDebit(1d, "Cheap Food");
        updateDebit(1d, "");
        assertDebitAttributes(receiver.id, 1d, "Cheap Food");

        updateDebit(0, "Free Food");
        assertDebitAttributes(receiver.id, 1d, "Cheap Food");
    }

    @Test
    public void afterDeletingDebit_itMayNotBeReadAnymore() {
        createDebit(12d, "Snack");
        deleteDebit();
        assertDebitNotFound(receiver.id);
    }

    @Test
    public void whenReadingAllDebits_shouldReturnAll(){
        createDebit(20d, "Dinner");
        createDebit(10d, "Lunch");

        new ReadAllDebitsCommand(new RequestStub(), iterableReceiver, debitStore).execute();

        assertDebitListContains(20d, "Dinner");
        assertDebitListContains(10d, "Lunch");
        Assert.assertEquals(2, numberOfDebits);


    }

    private void assertDebitListContains(double value, String description) {
        boolean contains = false;
        for(DebitData debit : iterableReceiver.debitList){
            if(debit.value == value && debit.description == description){
                contains = true;
                numberOfDebits++;
            }
        }
        Assert.assertEquals(true, contains);
    }
}
