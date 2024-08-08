client [ Call Client ] | server [ Call Server ]

Client :
    server!data @!"";
    Call StreamResponseClient

StreamResponseClient:
    server&
    { "continue" :
     Some(
        server?data @?"";
        Call StreamResponseClient
     )}
    //
    { "end" : None}

Server :
    client?data @?"";
    Call StreamResponseServer

StreamResponseServer:
    client!data@!"";
    If check(x)
    Then
        client+"continue" @+"";
        client!data @!"";
        Call StreamResponseServer
    Else
        client+"end" @+"";
        End