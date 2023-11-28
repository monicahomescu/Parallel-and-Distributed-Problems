import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {
    private List<Account> accounts;
    private int serialNumber;
    private ReentrantLock mutexSerialNumber;

    public Bank(List<Account> accounts) {
        this.accounts = accounts;
        this.serialNumber = 1;
        this.mutexSerialNumber = new ReentrantLock();
    }

    public int transferMoney(int senderId, int receiverId, int amount) throws InterruptedException {
        Account sender = null;
        Account receiver = null;

        for (Account account: accounts) {
            if (account.getId() == senderId)
                sender = account;
            else if (account.getId() == receiverId)
                receiver = account;
        }

        if (sender.getCurrentBalance() >= amount) {
            sender.getMutexAccount().lock();
            receiver.getMutexAccount().lock();
            mutexSerialNumber.lock();

            sender.setCurrentBalance(sender.getCurrentBalance() - amount);
            receiver.setCurrentBalance(receiver.getCurrentBalance() + amount);

            Transaction transaction = new Transaction(serialNumber, senderId, receiverId, amount);
            serialNumber++;

            sender.getLog().add(transaction);
            receiver.getLog().add(transaction);

            sender.getMutexAccount().unlock();
            receiver.getMutexAccount().unlock();
            mutexSerialNumber.unlock();

            Thread.sleep(1000);

            return transaction.getSerialNumber();
        }

        return -1;
    }

    public boolean logCheck() {
        List<Integer> senderIds = new ArrayList<>();
        List<Integer> receiverIds = new ArrayList();

        for (Account account : accounts) {
            account.getMutexAccount().lock();

            for (Transaction transaction : account.getLog()) {
                if (transaction.getSenderId() == account.getId())
                    senderIds.add(transaction.getSerialNumber());
                if (transaction.getReceiverId() == account.getId())
                    receiverIds.add(transaction.getSerialNumber());
            }

            account.getMutexAccount().unlock();
        }

        for (int serialNumber : senderIds) {
            if (!receiverIds.contains(serialNumber))
                return false;
        }

        return true;
    }

    public boolean consistencyCheck() {
        for (Account account : accounts) {
            account.getMutexAccount().lock();

            int balance = account.getInitialBalance();

            for (Transaction transaction : account.getLog()) {
                if (transaction.getSenderId() == account.getId())
                    balance -= transaction.getAmount();
                else if (transaction.getReceiverId() == account.getId())
                    balance += transaction.getAmount();
            }

            if (balance != account.getCurrentBalance())
                return false;

            account.getMutexAccount().unlock();
        }

        return true;
    }

    public void printAccounts() {
        for (Account account : accounts)
            System.out.println(account.toString());
    }
}
