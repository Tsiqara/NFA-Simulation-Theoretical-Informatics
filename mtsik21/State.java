package mtsik21;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class State{
    private static int counter = 0;
    public int id;
    public Map<String , ArrayList<State>> transitions;
    public State(){
        transitions = new TreeMap<>();
        id = counter;
        counter ++;
    }

    public void putAll(Map<String , ArrayList<State>> trans){
        for(String str : trans.keySet()){
            if(transitions.containsKey(str)){
              //  transitions.get(str).addAll(trans.get(str));
                for(int i = 0; i<trans.get(str).size();i++){
                    if(!transitions.get(str).contains(trans.get(str).get(i))){
                        transitions.get(str).add(trans.get(str).get(i));
                    }
                }
            }
            else{
                transitions.put(str,trans.get(str));
            }
        }
      //  transitions.putAll(trans);
    }

    public int getTransNumber() {
        int n = 0;
        for (String s: transitions.keySet()) {
            n += transitions.get(s).size();
        }
        return n;
    }
}
