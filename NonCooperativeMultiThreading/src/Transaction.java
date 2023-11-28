public class Transaction {
    private int serialNumber;
    private int senderId;
    private int receiverId;
    private int amount;

    public Transaction(int serialNumber, int senderId, int receiverId, int amount) {
        this.serialNumber = serialNumber;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction { SerialNumber: " + serialNumber +
                ", SenderId: " + senderId +
                ", ReceiverId: " + receiverId +
                ", Amount: " + amount + " }";
    }
}
