package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MessagePatternMaker extends MessagePatternBaseVisitor<String>{
    static class VisitorContext {
        HashMap<String, Session> sessions = new HashMap<>();
        boolean firstExchange = true;

        private String[] currentEnds = new String[2];
        private String currentTitle;
        public VisitorContext(){

        }
        public void resetEnds(){
            currentEnds[0]=null;
            currentEnds[1]=null;
        }
        public void setCurrentTitle(String title){
            currentTitle = title;
        }
        public String getCurrentTitle(){
            return currentTitle;
        }
        public String[] getCurrentTitles(){
            String[] titles = new String[2];
            getCurrentEnds().ifPresentOrElse(
                    (ends) -> {
                        titles[0] = currentTitle+"_"+ends[0];
                        titles[1] = currentTitle+"_"+ends[1];
                    },
                    () -> {}
            );
            return titles;
        }
        public boolean setCurrentEnds(String a, String b){
            if(currentEnds[0] != null || currentEnds[1] != null) return false;
            currentEnds[0] = a;
            currentEnds[1] = b;
            return true;
        }

        public Optional<String[]> getCurrentEnds(){
            if(currentEnds[0] == null || currentEnds[1] == null) return Optional.empty();
            return Optional.of(currentEnds);
        }

        public void reset() {
            resetEnds();
            currentTitle = null;
            firstExchange = true;
        }
    }

    public HashMap<String, Session> getSessionsMap(){
        return vctx.sessions;
    }

    VisitorContext vctx = new VisitorContext();
    @Override
    public String visitExchange(MessagePatternParser.ExchangeContext ctx) {
        var sender = ctx.getChild(0).getText();
        var rcver = ctx.getChild(2).getText();
        var comA = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE);
        var comB = new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE);
        if(vctx.firstExchange){
            vctx.setCurrentEnds(sender, rcver);
            vctx.sessions.put(vctx.getCurrentTitles()[0], new Session(sender, rcver, comA));
            vctx.sessions.put(vctx.getCurrentTitles()[1], new Session(rcver, sender, comB));
            vctx.firstExchange = false;
        }else{
            var cts = vctx.getCurrentTitles();
            vctx.sessions.get(cts[0]).addLeafCommunicationRoots(new ArrayList<>(List.of(comA)));
            vctx.sessions.get(cts[0]).addLeafCommunicationRoots(new ArrayList<>(List.of(comB)));
        }
        return super.visitExchange(ctx);
    }

    @Override
    public String visitPattern_single(MessagePatternParser.Pattern_singleContext ctx) {
        // 0 label
        // 1 "
        // 2 expression
        // 3 "
        vctx.reset();
        vctx.setCurrentTitle(ctx.getChild(0).getText());
        return super.visitPattern_single(ctx);
    }
}
