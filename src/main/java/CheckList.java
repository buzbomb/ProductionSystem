import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckList {
    private UUID checkListId, order;
    private Map<UUID, Integer> theList = new HashMap<>();

    public void addToCheckList(UUID partId, int quantity){
        this.theList.put(partId, quantity);
    }

    public UUID getCheckListId() {
        return checkListId;
    }

    public void setCheckListId(UUID checkListId) {
        this.checkListId = checkListId;
    }

    public UUID getOrder() {
        return order;
    }

    public void setOrder(UUID order) {
        this.order = order;
    }

    public Map<UUID, Integer> getTheList() {
        return theList;
    }

    public void setTheList(Map<UUID, Integer> theList) {
        this.theList = theList;
    }
}
