package mtsik21;

import java.io.*;
import java.util.*;

public class run {
    private static String string;
    private static int numStates, numAccept, numTrans;
    private static ArrayList<Integer> acceptStates = new ArrayList<>();
    private static Map<Integer, Map<String, ArrayList<Integer>>> transitions = new TreeMap<Integer, Map<String, ArrayList<Integer>>>();
    private static String simulate(){
        StringBuilder answer = new StringBuilder();
        ArrayList<Integer> currStates;
        currStates = transitions.get(0).get(Character.toString(string.charAt(0)));
        ;
        for(int i = 0; i < string.length(); i ++){
            ArrayList<Integer> nextStates = new ArrayList<>();
            boolean contains = false;
            for(int j = 0; j < currStates.size(); j ++){
                if(i != string.length() - 1) {
                    if (transitions.get(currStates.get(j)).containsKey(Character.toString(string.charAt(i + 1)))) {
                        nextStates.addAll(transitions.get(currStates.get(j)).get(Character.toString(string.charAt(i + 1))));
                    }
                }
                if (acceptStates.contains(currStates.get(j)) && !contains) {
                    answer.append("Y");
                    contains = true;
                }
            }
            if(!contains){
                answer.append("N");
            }

            currStates = nextStates;
        }
//        answer.append("\n");
        return answer.toString();
    }
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        try {
            string = br.readLine();
            st = new StringTokenizer(br.readLine());
            numStates = Integer.parseInt(st.nextToken());
            numAccept = Integer.parseInt(st.nextToken());
            numTrans = Integer.parseInt(st.nextToken());

            st = new StringTokenizer(br.readLine());
            while (st.hasMoreTokens()){
                acceptStates.add(Integer.parseInt(st.nextToken()));
            }

            for (int i = 0; i < numStates; i++) {
                st = new StringTokenizer(br.readLine());
                int num = Integer.parseInt(st.nextToken());
                Map<String, ArrayList<Integer>> trans = new TreeMap<>();
                for(int j = 0; j < num; j ++){
                    String ch = st.nextToken();
                    Integer state = Integer.valueOf(st.nextToken());
                    if(trans.containsKey(ch)){
                        trans.get(ch).add(state);
                    }else{
                        ArrayList<Integer> list = new ArrayList<>();
                        list.add(state);
                        trans.put(ch, list);
                    }
                }
                transitions.put(i, trans);
            }
            String res = simulate();
            System.out.println(res);
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
