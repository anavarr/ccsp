package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MessageQueues extends HashMap<String, Queue<Message>>{

    public int accessed = 0;
    static int DEFAULT_CAPACITY = 1000;
    public ArrayList<ArrayList<Message>> qsHistory = new ArrayList<>();
    public ArrayList<ArrayList<Message>> leftOvers = new ArrayList<>();
    int currentHistory = 0;
    public MessageQueues(){
        super();
        qsHistory.add(new ArrayList<>());
        leftOvers.add(new ArrayList<>());
    }

    public boolean messagesMustBeProcessed(String process){
        var processQs = keySet().stream().filter(el -> el.contains("-"+process)).toList();
        for (String processQ : processQs) {
            if(!get(processQ).isEmpty()) return true;
        }
        return false;
    }

    public boolean add(Utils.Direction direction, String source, String dest, String label){
        var key = getKey(source, dest);
        var msg = new Message(direction, label);
        if(!containsKey(key)){
            accessed++;
            put(key, new ArrayBlockingQueue<>(DEFAULT_CAPACITY));
        }
        qsHistory.get(currentHistory).add(msg);
        return get(key).add(msg);
    }
    public Message poll(String source, String dest){
        var key = getKey(source, dest);
        var q = get(key);
        if(q != null){
            var m = q.poll();
            if(m != null){
                accessed ++;
            }
            return m;
        }else{
            return null;
        }
    }
    public Message peek(String source, String dest){
        var key = getKey(source, dest);
        var q = get(key);
        if(q != null){
            return q.peek();
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
            qs.accessed = accessed;
        }
        return qs;
    }
    public boolean equals(MessageQueues qs){
        if(!(keySet().containsAll(qs.keySet()) &&qs.keySet().containsAll(keySet()))) return false;
        for (String s : keySet()) {
            if(qs.get(s).size() != this.get(s).size() || !(get(s).containsAll(qs.get(s)) && qs.get(s).containsAll(get(s)))) return false;
        }
        return accessed == qs.accessed;
    }

    public void saveIteration(){
        for (String s : keySet()) {
            while(!get(s).isEmpty()){
                leftOvers.get(currentHistory).add(get(s).poll());
            }
        }
    }

    public void saveIterationAndPrepareNextOne() {
        for (String s : keySet()) {
            while(!get(s).isEmpty()){
                leftOvers.get(currentHistory).add(get(s).poll());
            }
        }
        currentHistory++;
        leftOvers.add(new ArrayList<>());
        qsHistory.add(new ArrayList<>());
    }
}
