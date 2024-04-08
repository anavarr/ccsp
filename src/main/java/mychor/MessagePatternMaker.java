package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MessagePatternMaker extends MessagePatternBaseVisitor<String>{
    static class VisitorContext {
        HashMap<String, Session> sessions = new HashMap<>();
        boolean firstExchange = true;

        private String currentTitle;
        private HashMap<String, Session> currentSessions = new HashMap<>();
        public VisitorContext(){

        }
        public void setCurrentTitle(String title){
            currentTitle = title;
        }
    }

    public HashMap<String, Session> getSessionsMap(){
        return vctx.sessions;
    }

    VisitorContext vctx = new VisitorContext();

    @Override
    public String visitSequent(MessagePatternParser.SequentContext ctx) {
        // 0 expr
        // 1 ;
        // 2 expr
        vctx.currentSessions = new HashMap<>();
        vctx.firstExchange = true;
        ctx.getChild(0).accept(this);
        var leftSessions = vctx.currentSessions;
        vctx.currentSessions = new HashMap<>();
        vctx.firstExchange = true;
        ctx.getChild(2).accept(this);
        var rightSessions = vctx.currentSessions;
        for (String s : leftSessions.keySet()) {
            leftSessions.get(s).addLeafCommunicationRoots(rightSessions.get(s).communicationsRoots());
        }
        vctx.currentSessions = leftSessions;
        return null;
    }

    @Override
    public String visitExchange(MessagePatternParser.ExchangeContext ctx) {
        // 0 id
        // 1 ->
        // 2 id
        var sender = ctx.getChild(0).getText();
        var rcver = ctx.getChild(2).getText();
        var comSend = new Communication(Utils.Direction.SEND);
        var comReceive = new Communication(Utils.Direction.RECEIVE);
        if(vctx.firstExchange){
            vctx.currentSessions.put(sender,  new Session(sender, rcver, comSend));
            vctx.currentSessions.put(rcver,  new Session(rcver, sender, comReceive));
            vctx.firstExchange = false;
        }else{
            vctx.currentSessions.get(sender).addLeafCommunicationRoots(new ArrayList<>(List.of(comSend)));
            vctx.currentSessions.get(rcver).addLeafCommunicationRoots(new ArrayList<>(List.of(comReceive)));
        }
        return null;
    }

    @Override
    public String visitPattern_single(MessagePatternParser.Pattern_singleContext ctx) {
        // 0 label
        // 1 "
        // 2 expression
        // 3 "
        vctx.firstExchange = true;
        vctx.setCurrentTitle(ctx.getChild(0).getText());
        ctx.getChild(2).accept(this);
        for (String s : vctx.currentSessions.keySet()) {
            vctx.sessions.put(vctx.currentTitle+"_"+s, vctx.currentSessions.get(s));
        }
        return null;
    }

    @Override
    public String visitChoice(MessagePatternParser.ChoiceContext ctx) {
        // 0 expr
        // 1 |
        // 2 expr
        vctx.currentSessions = new HashMap<>();
        vctx.firstExchange = true;
        ctx.getChild(0).accept(this);
        var leftSessions = vctx.currentSessions;

        vctx.currentSessions = new HashMap<>();
        vctx.firstExchange = true;
        ctx.getChild(2).accept(this);
        var rightSessions = vctx.currentSessions;


        for (String s : leftSessions.keySet()) {
            leftSessions.get(s).expandTopCommunicationRoots(rightSessions.get(s).communicationsRoots());
        }
        vctx.currentSessions = leftSessions;
        return null;
    }

    @Override
    public String visitRepeat(MessagePatternParser.RepeatContext ctx) {
        // 0 (
        // 1 expr
        // 2 )
        // 3 (REPEATER)?
        vctx.currentSessions = new HashMap<>();
        vctx.firstExchange = true;
        ctx.getChild(1).accept(this);
        var nestedSession = vctx.currentSessions;

        if(ctx.children.size() <= 3){
            return null;
        }

        switch (ctx.getChild(3).getText()){
            case "*" :
                for (String s : nestedSession.keySet()) {
                    var session = nestedSession.get(s);
                    var topNodes = session.communicationsRoots();
                    var bottomNodes = session.getLeaves();
                    for (Communication bottomNode : bottomNodes) {
                        bottomNode.addLeafCommunicationRoots(topNodes);
                        bottomNode.addLeafCommunicationRoots(new ArrayList<>(List.of(
                                new Communication(Utils.Direction.VOID))));
                    }
                    session.expandTopCommunicationRoots(new ArrayList<>(List.of(
                            new Communication(Utils.Direction.VOID))));
                }
                break;
            case "?" :
                for (String s : nestedSession.keySet()) {
                    var session = nestedSession.get(s);
                    session.expandTopCommunicationRoots(new ArrayList<>(List.of(
                            new Communication(Utils.Direction.VOID))));
                }
                break;
            case "+" :
                for (String s : nestedSession.keySet()) {
                    var session = nestedSession.get(s);
                    var topNodes = session.communicationsRoots();
                    var bottomNodes = session.getLeaves();
                    for (Communication bottomNode : bottomNodes) {
                        bottomNode.addLeafCommunicationRoots(topNodes);
                        bottomNode.addLeafCommunicationRoots(new ArrayList<>(List.of(
                                new Communication(Utils.Direction.VOID))));
                    }
                }
                break;
        }
        vctx.currentSessions = nestedSession;
        return null;
    }
}
