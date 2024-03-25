package mychor;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MessageQueues extends HashMap<String, Queue<Message>>{

    static int DEFAULT_CAPACITY = 100;
    public MessageQueues(){
        super();
    }

    public boolean add(Utils.Direction direction, String source, String dest, String label){
        var key = getKey(source, dest);
        var msg = new Message(direction, label);
        if(!containsKey(key)){
            put(key, new ArrayBlockingQueue<>(DEFAULT_CAPACITY));
        }
        return get(key).add(msg);
    }
    public Message poll(String source, String dest){
        var key = getKey(source, dest);
        var q = get(key);
        if(q != null){
            return q.poll();
        }else{
            return null;
        }
    }

    static String getKey(String src, String dest){
        return src +"-"+dest;
    }

    public boolean areEmpty(){
        for (String s : keySet()) {
            if (!get(s).isEmpty()) return false;
        }
        return true;
    }
    public MessageQueues duplicate() {
        var qs = new MessageQueues();
        for (String s : keySet()) {
            var q = new ArrayBlockingQueue<Message>(DEFAULT_CAPACITY);
            q.addAll(get(s));
            qs.put(s, q);
        }
        return qs;
    }
    public boolean equals(MessageQueues qs){
        if(!(keySet().containsAll(qs.keySet()) &&qs.keySet().containsAll(keySet()))) return false;
        for (String s : keySet()) {
            if(!(get(s).containsAll(qs.get(s)) && qs.get(s).containsAll(get(s)))) return false;
        }
        return true;
    }
}
