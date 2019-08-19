import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProductionSystem {
    public static void main(String[] args) throws IOException {
        MQTTHandler theHandler = new MQTTHandler();
        ObjectMapper om = new ObjectMapper();
        Map<UUID, StockListItem> theStock = om.<List<StockListItem>>readValue(MQTTHandler.class.getResourceAsStream("/stockList.json"), new TypeReference<List<StockListItem>>(){})
                .stream()
                .collect(Collectors.toMap(StockListItem::getPartId, Function.identity()));

        theHandler.setTheStock(theStock);
        theHandler.subscribeToStockCheck();
    }
}
