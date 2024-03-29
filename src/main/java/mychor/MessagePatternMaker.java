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
        public String getCurrentTitle(){
            return currentTitle;
        }

//        public VisitorContext duplicate(){
//            var vc = new VisitorContext();
//            vc.firstExchange = this.firstExchange;
//            vc.currentTitle = this.currentTitle;
//            for (String s : this.sessions.keySet()) {
//                var session = sessions.get(s);
//                var communicationRootsDuplicate = new ArrayList<Communication>();
//                for (Communication communicationsRoot : session.communicationsRoots()) {
//                    communicationRootsDuplicate.add(communicationsRoot.duplicate());
//                }
//                vc.sessions.put(s, new Session(session.peerA(),session.peerB(), session.communicationsRoots()));
//            }
//            vc.sessions
//        }
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
        for (String s : vctx.currentSessions.keySet()) {
            System.out.println(vctx.currentSessions.get(s));
        }
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
        System.out.println(vctx.sessions.keySet());
        return null;
    }
}
