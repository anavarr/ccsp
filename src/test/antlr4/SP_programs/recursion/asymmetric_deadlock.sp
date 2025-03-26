client [ Call Client ] | server [ Call Server ]

Client:
    If isValid() Then
        server+"continue" @+"";
        server!data @!"";
        End
    Else
        server+"quit" @+"";
        Call Client

Server:
    client&{
        "continue": Some(client?res @?""; End)
    } // {
        "quit": None
    }