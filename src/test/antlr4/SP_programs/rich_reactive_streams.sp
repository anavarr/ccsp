subscriber [ call Subscriber ] | publisher [ call Publisher ]


Publisher:
    subscriber?firstSubscribe @?"";

    subscriber &
        {
            "subscribe":
            subscriber?subName @?"";
            subscriber+onSubscribe @+"";
            Call Publisher
        }
    subscription &
        {
            "request":
            subscription?pendingRequests @?"";
            //send all stuff
            Call Sending
        }
        //
        {
            "cancel":
            Call Publisher
        }


Subscriber:
    publisher+ "subscribe" @+"";
    publisher?subscription @?"";
    //doStuff()

Sending:
    If (mustSend(pendingRequests)) Then
        If (canSend(payload)) Then
            subscriber+"nextItem" @+"";
            subscriber!item @!"";
            //decrease the number of requests to send
            Call Sending
        Else
            //should also notify in case of errors but must simplify
            subscriber+"complete" @+"";
            Call Publisher
    Else
        Call Publisher