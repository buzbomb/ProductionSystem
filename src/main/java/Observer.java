
import java.util.Map;
import java.util.TreeMap;

public class Observer {
    private Map<Part, Integer> reOrderCount = new TreeMap<>();
    private Map<Part, Integer> minimumQuantityCheck = new TreeMap<>();

    public void addToReOrderCount(Part part){
        if(reOrderCount.containsKey(part)){
            int x = reOrderCount.get(part) + 1;
            reOrderCount.put(part, x);
        } else {
            reOrderCount.put(part, 1);
        }
    }

    public void addToMinimumQuantityCheck(Part part, int quantity){
        minimumQuantityCheck.put(part, quantity);
    }

    public void checkMinimumReOrderQuantity(){
        for(Map.Entry<Part, Integer> entry : reOrderCount.entrySet()){
            if(entry.getValue() > 1){
                System.out.println("Part: " + entry.getKey().getPartName() + " has hit minimum re-order quantity " + entry.getValue() + " times" +
                        "\nCONSIDER CHANGING MINIMUM REORDER QUANTITY FOR: " + entry.getKey().getPartName());
            }
        }

        for(Map.Entry<Part, Integer> entry : minimumQuantityCheck.entrySet()){
            System.out.println("Part: " + entry.getKey().getPartName() + " has exceeded the minimum re-order quantity by " + entry.getValue() +
                    "\nCONSIDER CHANGING MINIMUM REORDER QUANTITY FOR: " + entry.getKey().getPartName());
        }
    }
}
