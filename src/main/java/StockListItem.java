import java.util.UUID;

public class StockListItem implements Comparable<StockListItem> {
    private UUID partId;
    private int quantity, minimumReOrder;
    private String partName;

    public UUID getPartId() {
        return partId;
    }

    public void setPartId(UUID partId) {
        this.partId = partId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinimumReOrder() {
        return minimumReOrder;
    }

    public void setMinimumReOrder(int minimumReOrder) {
        this.minimumReOrder = minimumReOrder;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    @Override
    public int compareTo(StockListItem o) {
        return this.getPartName().compareTo( o.getPartName() );
    }
}
