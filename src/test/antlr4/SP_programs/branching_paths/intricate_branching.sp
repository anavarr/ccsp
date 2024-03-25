client [Call Client] | server [Call Server] | service [Call Service]

Client :
    server!request @!"";
    server?resp @?"";
    If check(resp)
    Then
        server+"label1" @+"";
        server?data @?"";
        End
    Else
        server+"label2" @+"";
        server?data @?"";
        If isValid(data)
        Then
            server+"modify" @+"";
            server &
            { "typeA" : Some(
                server?type  @?"";
                If checkType(type)
                Then
                    server+"ack" @+"";
                    End
                Else
                    server+"refuse" @+"";
                    End
            )} //
            { "typeB" : Some(
                server?type @?"";
                If checkTypeAgainstLocal(type)
                Then
                    server+"continue" @+"";
                    Call Client
                Else
                    End
            )} //
            { "typeC": None }
        Else
            server+"post" @+"";
            End

Server :
    client?req @?"";
    service!prototype(req) @!"";
    service &
    { "post" : Some(
        service?data @?"";
        If check(data)
        Then
            client!data @!"";
            Call ServerContinuation
        Else
            client!error @!"";
            Call ServerContinuation
    )}//
    { "put" : Some(
        service?data @?"";
        client!data  @!"";
        Call ServerContinuation
    )}

ServerContinuation:
    client &
    { "label1": Some(
        client!defaultData @!"";
        End
    )}//
    { "label2": None
    }

Service :
    End