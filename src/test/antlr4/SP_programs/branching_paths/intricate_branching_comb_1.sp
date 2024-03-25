client [Call Client] | server [Call Server] | service [Call Service]

Client :
    server!request @!"";
    server?resp @?"";
    server+"label1" @+"";
    server?data @?"";
    End

Server :
    client?req @?"";
    service!prototype(req) @!"";
    service &
    { "post" : Some(
        service?data @?"";
        client!data @!"";
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