client [
    server!req @!"";
    server?res @?"";

    If isCompliant(res)
    Then
        server+"pushData" @+"";
        server!newData @!"";
        server?ack @?"";
        End
    Else
        server+"abort" @+"";
        End
]

|

server [
    client?req @?"";
    client!checkSum(req) @!"";
    client &
    {"pushData" :
        Some(
            client?newData @?"";
            client!ack @!"";
            End
        )
    }
    //
    {"abort":
        Some(
            client?x @?"";
            End
        )
    }
]