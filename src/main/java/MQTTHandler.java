import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MQTTHandler implements MqttCallback {
    private ObjectMapper om = new ObjectMapper();
    private Observer theObserver = new Observer();
    private CheckList currentCheckList = new CheckList();
    private Map<UUID, Part> parts;
    private Map<UUID, StockListItem> theStock;

    public void subscribeToStockCheck(){

        String topic        = "stockCheck";
        String broker       = "tcp://192.168.1.4:1883";
        String clientId     = "ProductionSystem";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            connOpts.setAutomaticReconnect(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.setCallback(this);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe(topic);
            Thread.sleep(5000);
        } catch(MqttException | InterruptedException me) {
            System.out.println("reason "+me);
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost! " + cause);
        subscribeToStockCheck();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        byte[] json = message.getPayload();
        this.currentCheckList = om.readValue(json, CheckList.class);
        try{
            parts = om.<List<Part>>readValue(MQTTHandler.class.getResourceAsStream("/partList.json"), new TypeReference<List<Part>>() {})
                    .stream()
                    .collect(Collectors.toMap(Part::getPartId, Function.identity()));
        }catch(IOException e){
            e.printStackTrace();
        }

        for(Map.Entry<UUID, Integer> checkListItem : currentCheckList.getTheList().entrySet()){
            if(checkListItem.getKey().equals(this.theStock.get(checkListItem.getKey()).getPartId())){
                int tempQuantity = this.theStock.get(checkListItem.getKey()).getQuantity() - checkListItem.getValue();
                this.theStock.get(checkListItem.getKey()).setQuantity(tempQuantity);
            }
        }

        stockCheck(this.theStock);
        theObserver.checkMinimumReOrderQuantity();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void printCurrentStock(Map<UUID, StockListItem> theStock){
        System.out.println("Current Stock:");
        for(Map.Entry<UUID, StockListItem> item : theStock.entrySet()){
            System.out.println("Part: " + item.getValue().getPartName());
            System.out.println("\t" + "Quantity: " + item.getValue().getQuantity());
            System.out.println("\t" + "Minimum Re-Order Quantity: " + item.getValue().getMinimumReOrder());
        }
    }

    public void printStockAfterOrderStock(Map<UUID, StockListItem> theStock){
        System.out.println("Stock After Order:");
        for(Map.Entry<UUID, StockListItem> item : theStock.entrySet()){
            System.out.println("Part: " + item.getValue().getPartName());
            System.out.println("\t" + "Quantity: " + item.getValue().getQuantity());
            System.out.println("\t" + "Minimum Re-Order Quantity: " + item.getValue().getMinimumReOrder());
        }
    }

    public void stockCheck(Map<UUID, StockListItem> theStock){
        for(Map.Entry<UUID, StockListItem> stockItem : theStock.entrySet()){
            if(stockItem.getValue().getQuantity() <= stockItem.getValue().getMinimumReOrder()){
                int minimumReOrderQuantity = stockItem.getValue().getMinimumReOrder() - stockItem.getValue().getQuantity();
                System.out.println("Part: " + stockItem.getValue().getPartName() + " is needing re-ordered" + "\tRe-order quantity to satisfy Orders: " + minimumReOrderQuantity);
                theObserver.addToReOrderCount(parts.get(stockItem.getValue().getPartId()));
                if(stockItem.getValue().getQuantity() <= (stockItem.getValue().getMinimumReOrder()-50)){
                    theObserver.addToMinimumQuantityCheck(parts.get(stockItem.getValue().getPartId()), minimumReOrderQuantity);
                }
            }
        }
    }

    public Map<UUID, StockListItem> getTheStock() {
        return theStock;
    }

    public void setTheStock(Map<UUID, StockListItem> theStock) {
        this.theStock = theStock;
    }
}
