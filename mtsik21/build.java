package mtsik21;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class build {
    private static String regex;
    private static int counter = 0;

    private static int getPrecedence(char ch){
        if(ch == '(') return 1;
        if(ch == '|') return 2;
        if(ch == '.') return 3; // concat
        if(ch == '*') return 4;
        return 5;
    }

    //insert '.' where concat is implicit
    private static String formatRegex(String regex){
        String result = new String();
        List<Character> operators = Arrays.asList('|','*');

        for (int i = 0; i < regex.length() - 1; i++) {
            char curr = regex.charAt(i);
            char next = regex.charAt(i + 1);
            result += curr;
            if(curr != '(' && next != ')' && !operators.contains(next) && curr != '|'){
                result += '.';
            }
        }

        result += regex.charAt(regex.length() - 1);
        return result;
    }

    private static String infixToPostFix(String regex){
        String result = new String();
        String formattedRegex = formatRegex(regex);
        Stack<Character> stack = new Stack<>();
        List<Character> operators = Arrays.asList('|','*', '.');
        for (Character ch: formattedRegex.toCharArray()) {
            if(ch.equals('(')){
                stack.push(ch);
            }else if(ch.equals(')')){
                    while (!stack.peek().equals('(')){
                        result += stack.pop();
                    }
                    stack.pop();
            }else if(operators.contains(ch)){
                while(!stack.isEmpty()){
                    Character peekedChar = stack.peek();
                    if(getPrecedence(peekedChar) >= getPrecedence(ch)){
                        result += stack.pop();
                    }else{
                        break;
                    }
                }
                stack.push(ch);
            }else{
                result += ch;
            }
        }

        while (stack.size() > 0) {
            result += stack.pop();
        }

        return result;
    }





    public static NFA unionNFAs(NFA nfa1, NFA nfa2){
        NFA result = nfa1;
        //union accept states
        ArrayList<Integer> acceptStates = new ArrayList<>(nfa1.getAcceptStates());
        acceptStates.addAll(nfa2.getAcceptStates());
        if(nfa2.acceptStates.contains(nfa2.root)){
            acceptStates.remove((Integer) nfa2.root);
        }
//        for (Integer i:nfa2.getAcceptStates()) {
//            acceptStates.add(i + nfa1.getNumStates() - 1);
//        }
        if(nfa2.acceptStates.contains(nfa2.root) && !nfa1.acceptStates.contains(nfa1.root)){
            acceptStates.add(nfa1.root);
        }
        result.setAcceptStates(acceptStates);
        //union transitions
        //add first NFA transactions
        Map<Integer, State> states = new TreeMap<>(nfa1.getTransitions());
        //add second NFA transactions
        Map<Integer, State> states2 = nfa2.getTransitions();
        states.get(nfa1.root).putAll(states2.get(nfa2.root).transitions); // root state
        for(Integer index : states2.keySet()){
            if(index!=nfa2.root){
                states.put(index,states2.get(index));
            }
        }
        //add transitions of second starts state to first start state
        result.setTransitions(states);
        return result;
    }

    private static boolean mapContains(int i, Map<String, ArrayList<Integer>> trans){
        for (ArrayList list:trans.values()) {
            if(list.contains(i)){
                return true;
            }
        }
        return false;
    }

    public static NFA star(NFA nfa){
        NFA result = nfa;
        ArrayList<Integer> acceptStates = new ArrayList<>(nfa.getAcceptStates());
        result.setTransitions(nfa.getTransitions());
        Map<String, ArrayList<State>> startTrans = nfa.getTransitions().get(nfa.root).transitions;
        for (Integer i: acceptStates) {
            nfa.getTransitions().get(i).putAll(startTrans);
        }
        if(!acceptStates.contains(nfa.root)) {
            acceptStates.add(nfa.root);
        }
        result.setAcceptStates(acceptStates);
        return result;
    }

    public static NFA concatNFAs(NFA nfa1, NFA nfa2){
      //  NFA result = new NFA();
        NFA result = nfa1;
        ArrayList<Integer> acceptStates = nfa1.getAcceptStates();
        //add first NFA transactions
        Map<Integer, State> states = nfa1.getTransitions();
        Map<Integer, State> states2 = nfa2.getTransitions();
        for(Integer index : states2.keySet()){
            if(index!=nfa2.root){
                states.put(index,states2.get(index));
            }
        }
        //add second NFA transactions
        Map<String, ArrayList<State>> transitions2root = nfa2.getTransitions().get(nfa2.root).transitions;
        for(Integer a : acceptStates) {
            states.get(a).putAll(transitions2root);
        }

        result.setTransitions(states);
        if(nfa2.acceptStates.contains(nfa2.root)){
            acceptStates.addAll(nfa2.getAcceptStates());
            acceptStates.remove((Integer)nfa2.root);
        }
        else{
            acceptStates = nfa2.getAcceptStates();
        }
        result.setAcceptStates(acceptStates);
        return result;
//        int nfa1Size = nfa1.getNumStates();
//        for (Integer i: transitions2.keySet()) {
//            Map<String, ArrayList<Integer>> trans = new TreeMap<>();
//            for (String s : transitions2.get(i).keySet()) {
//                ArrayList<Integer> list = new ArrayList<>();
//                for (Integer state : transitions2.get(i).get(s)) {
//                    list.add(state + nfa1Size - 1);
//                }
//                trans.put(s, list);
//            }
//            if(i != 0) {
//                transitions.put(i + nfa1Size - 1, trans);
//            }else{
//                for (Integer ind: acceptStates) {
//                    transitions.get(ind).putAll(trans);
//                }
//            }
//        }

    }

    public static NFA regexToNFA(String regex){
        List<Character> operators = Arrays.asList('|','*', '.');
        Stack<NFA> stack = new Stack<>();
        for(int i = 0; i < regex.length(); i ++){
            char ch = regex.charAt(i);
            if(!operators.contains(ch)){
                    NFA current = new NFA(ch);
        //        System.out.println(current.toString());
                    stack.push(current);
            }else{
                if(ch == '|'){
                    NFA nfa2 = stack.pop();
                    NFA nfa1 = stack.pop();
                    stack.push(unionNFAs(nfa1,nfa2));
        //            System.out.println("union");
     //               System.out.println(stack.peek().toString());
                }
                if(ch == '.'){
                    NFA nfa2 = stack.pop();
                    String str = nfa2.toString();
           //         if(!stack.isEmpty()) {
                        NFA nfa1 = stack.pop();
                        stack.push(concatNFAs(nfa1, nfa2));
        //            }else {
        //                stack.push(nfa2);
       //             }
           //         System.out.println("concat");
             //       System.out.println(stack.peek().toString());
                }
                if(ch == '*'){
                    NFA nfa = stack.pop();
                    stack.push(star(nfa));
             //       System.out.println("star");
              //      System.out.println(stack.peek().toString());
                }

            }
        }
        return stack.pop();
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            regex = reader.readLine();

        //    System.out.println(infixToPostFix(regex));
            String res = "";
            for(int i = 0; i< regex.length();i++){
                if(regex.charAt(i) == '('){
                    int counter = 1;
                    int start = i;
                    while(counter!=0){
                        i++;
                        if(i == regex.length()){
                            break;
                        }
                        if(regex.charAt(i) == '('){
                            counter++;
                        }
                        else if(regex.charAt(i) == ')'){
                            counter--;
                        }
                        else{
                            break;
                        }
                    }
                    if(counter == 0){
                        res+='$';
                    }
                    else{
                        res+='(';
                        i = start;
                    }
                }
                else{
                    res+=regex.charAt(i);
                }
            }
            regex = res;
            NFA nfa = regexToNFA(infixToPostFix(regex));
//            nfa.removeDuplicateStates();
            System.out.println(nfa.toString());
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

