client [ Call Client ] | server [ Call Server] | service [ Call Service ]

Client:
    If isValid() Then
        server + "continue" @+""; service!test @!""; Call Client
    Else
        server + "quit" @+""; Call Client

Server:
    client & { "continue": None } // { "quit": None }

Service:
    client ?request @?""; End

