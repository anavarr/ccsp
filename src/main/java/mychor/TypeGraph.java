package mychor;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TypeGraph extends Automaton {

    private HashMap<Character, String> labelMapping;

    public TypeGraph(HashMap<Character, String> labelMapping){
        this.labelMapping = labelMapping;
    }

    @Override
    public String toString() {
        var dip = super.toString();
        for (Map.Entry<Character, String> characterStringEntry : labelMapping.entrySet()) {
            var c = characterStringEntry.getKey();
            var b = new  StringBuilder();
            if (c >= '!' && c <= '~' && c != '\\' && c != '"') {
            } else {
                b.append("\\u");
                String s = Integer.toHexString(c);
                if (c < 16) {
                    b.append("000").append(s);
                } else if (c < 256) {
                    b.append("00").append(s);
                } else if (c < 4096) {
                    b.append("0").append(s);
                } else {
                    b.append(s);
                }
            }
            dip = dip.replace(b.toString(), characterStringEntry.getValue());
        }
        return dip;
    }

    public HashMap<Character, String> getLabelMapping() {
        return labelMapping;
    }
}
