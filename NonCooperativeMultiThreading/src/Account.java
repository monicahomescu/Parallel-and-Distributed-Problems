import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private int id;
    private int initialBalance;
    private int currentBalance;
    private List<Transaction> log;
    private ReentrantLock mutexAccount;

    public Account(int id, int initialBalance) {
        this.id = id;
        this.initialBalance = initialBalance;
        this.currentBalance = initialBalance;
        this.log = new ArrayList<>();
        this.mutexAccount = new ReentrantLock();
    }

    public int getId() {
        return id;
    }

    public int getInitialBalance() {
        return initialBalance;
    }

    public int getCurrentBalance() {
        return currentBalance;
    }

    public List<Transaction> getLog() {
        return log;
    }

    public ReentrantLock getMutexAccount() {
        return mutexAccount;
    }

    public void setCurrentBalance(int currentBalance) {
        this.currentBalance = currentBalance;
    }

    @Override
    public String toString() {
        return "Account { Id: " + id +
                ", InitialBalance: " + initialBalance +
                ", CurrentBalance: " + currentBalance +
                ", Log: \n" + log + " }";
    }
}
