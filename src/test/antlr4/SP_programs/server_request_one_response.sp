client [ Call Client ] | server [ Call Server ]

Client :
    Call StreamRequestClient

StreamRequestClient:
    If check(x)
    Then
        server+"continue" @+"";
        server!data @!"";
        Call StreamRequestClient
    Else
        server+"end" @+"";
        server?answer @?"";
        End


Server :
    client&
    { "continue" : Some(
        client?request@?"";
        Call Server
    )}
    //
    { "end" : Some(
        client!response@!"";
        End
    )}

