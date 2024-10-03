package mtsik21;

import java.util.*;

public class NFA{
    public int root;
    public ArrayList<Integer> acceptStates;
    public Map<Integer, State> states;

    public NFA(){
        State rootState = new State();
        root = rootState.id;
        acceptStates = new ArrayList<>();
        states = new TreeMap<>();
        states.put(root, rootState);
    }

    public NFA(char ch){
        this();
        if(ch == '$'){
            acceptStates.add(root);
        }
        else {
            State newState = new State();
            acceptStates.add(newState.id);
            states.put(newState.id, newState);
            Map<String, ArrayList<State>> trans = new TreeMap<>();
            ArrayList<State> list = new ArrayList<>();
            list.add(newState);
            trans.put(String.valueOf(ch), list);
            states.get(root).putAll(trans);
        }
    }

    public int getNumStates(){
        return states.size();
    }

    public int getNumAccept(){
        Set<Integer> set = new HashSet<>();
        int n = 0;
        for (Integer i: acceptStates) {
            if(!set.contains(i)){
                n ++;
                set.add(i);
            }
        }
        return n;
    }

    public int getNumTrans() {
        int result = 0;
        for (State i : states.values()) {
            result += i.getTransNumber();
        }
        return result;
    }

    public void setAcceptStates(ArrayList<Integer> acceptStates){
        this.acceptStates = acceptStates;
    }

    public ArrayList<Integer> getAcceptStates() {
        ArrayList<Integer> unique = new ArrayList<>();
        for (Integer i: acceptStates) {
            if(!unique.contains(i)){
                unique.add(i);
            }
        }
        return unique;
    }

    public Map<Integer, State> getTransitions() {
        return states;
    }

    public void setTransitions(Map<Integer, State> transitions) {
        this.states = transitions;
    }

//        private void addTransitions(Integer i, Map<String, ArrayList<Integer>> trans){
//            if(transitions.keySet().contains(i)) {
//                transitions.get(i).putAll(trans);
//            }else{
//                transitions.put(i, trans);
//            }
//        }

    public int getTransNumberOfState(int i){
        return states.get(i).getTransNumber();
    }

//        public void removeDuplicateStates(){
//            //identify duplicates
//            HashMap<Integer, Integer> duplicates = new HashMap<>();
//            for(int i = 0; i < transitions.size()-1; i ++){
//                int ind = i + 1;
//                while(i+1 < transitions.size() && transitions.get(i+1).equals(transitions.get(i))){
//                    if(duplicates.containsKey(ind)){
//                        int n = duplicates.get(ind) + 1;
//                        duplicates.remove(ind);
//                        duplicates.put(i + 1, n);
//                    }else{
//                        duplicates.put(i + 1, 1);
//                    }
//                    i ++;
//                }
//            }
//
//            //remove duplicate states
////            System.out.println(duplicates);
//            for(int i = transitions.size()-1; i >= 0; i --){
//                if(duplicates.containsKey(i)){
////                    System.out.println(i);
//                    int ind = i - duplicates.get(i);
//                    for (String s: transitions.get(ind).keySet()) {
//                        for(int j = 0; j < transitions.get(ind).get(s).size();j ++) {
//                            if(transitions.get(ind).get(s).get(j) > ind) {
//                                if(duplicates.containsKey(transitions.get(ind).get(s).get(j))){
//                                    transitions.get(ind).get(s).set(j, transitions.get(ind).get(s).get(j) - duplicates.get(i) + 1);
//                                }
////                                System.out.println(transitions.toString());
//                                transitions.get(ind).get(s).set(j, transitions.get(ind).get(s).get(j) - duplicates.get(i));
//                            }
//                        }
//                    }
////                    System.out.println(transitions.toString());
//                }
//
//                if(duplicates.containsKey(i)) {
//                    for(int j = 0; j < duplicates.get(i); j ++) {
//                        transitions.remove(i - j);
//                        if(acceptStates.contains(i-j)){
//                            acceptStates.remove((Integer)(i - j));
//                        }
//                        for (int a = 0; a < acceptStates.size(); a ++) {
//                            if(acceptStates.get(a) > i){
//                                acceptStates.set(a, acceptStates.get(a) - (acceptStates.get(a)-i));
//                            }
//                        }
//                    }
//                    duplicates.remove(i);
//                }
//            }
//        }

    private Map<Integer,Integer> ids;
    private Map<Integer, Integer> ids2;
    public void correctIndexes(){
        ind = 0;
        ids = new TreeMap<>();
        ids2 = new TreeMap<>();
        State curState = states.get(root);
        Set<Integer> visited = new HashSet<>();
        dfs(visited, ids, curState);
    }

    private int ind;
    private void dfs(Set<Integer> visited, Map<Integer, Integer> ids, State curState) {
        if(visited.contains(curState.id)){
            return;
        }
        visited.add(curState.id);
        ids.put(ind, curState.id);
        ids2.put(curState.id, ind);
        ind ++;
        for (String s : curState.transitions.keySet()){
            for(State next : curState.transitions.get(s)){
                dfs(visited, ids, next);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        correctIndexes();
        //n a t
        sb.append(getNumStates()).append(" ");
        sb.append(getNumAccept()).append(" ");
        sb.append(getNumTrans()).append("\n");
        //accept states
        for (int i = 0; i < acceptStates.size()-1; i++) {
            sb.append(ids2.get(acceptStates.get(i))).append(" ");
        }
        sb.append(ids2.get(acceptStates.get(acceptStates.size()-1))).append("\n");
        //transitions
        for(int i = 0; i < states.size(); i ++){
            State cur = states.get(ids.get(i));
            sb.append(cur.getTransNumber()).append(" ");
            for (String s: cur.transitions.keySet()) {
                for (State st : cur.transitions.get(s)){
                    sb.append(s).append(" ").append(ids2.get(st.id)).append(" ");
                }
            }
            if(i != states.size() - 1) {
                sb.append("\n");
            }
        }
//            for (State s: transitions) {
//                sb.append(getTransNumberOfState(i)).append(" ");
//                for (String s: transitions.get(i).keySet()) {
//                    for (Integer ind : transitions.get(i).get(s)){
//                        sb.append(s).append(" ").append(ind).append(" ");
//                    }
//                }
//                if(i != transitions.size()) {
//                    sb.append("\n");
//                }
//            }
        return sb.toString();
    }

}
